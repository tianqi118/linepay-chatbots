package com.line.pay.chatbot.controller;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.line.pay.chatbot.events.CommonMessage;
import com.line.pay.chatbot.events.Event;
import com.line.pay.chatbot.events.TextMessage;
import com.line.pay.chatbot.events.WebhookEvent;
import com.line.pay.chatbot.payment.ReserveResponse;
import com.line.pay.chatbot.service.LineMessageService;
import com.line.pay.chatbot.service.LinePayService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wangtianqi20
 */
@Controller
public class CallbackController implements ServletContextAware {
    private static Logger logger = LogManager.getLogger(CallbackController.class.getName());

    private static final String X_LINE_SIGNATURE = "X-Line-Signature";

    @Autowired
    private LineMessageService lineMessageService;

    @Autowired
    private LinePayService linePayService;

    @Value("${line.id}")
    private String lineId;

    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    public ResponseEntity handleCallback(HttpServletRequest request, HttpServletResponse response) {
        try {
            logger.info("Callback call in...");
            String signature = request.getHeader(X_LINE_SIGNATURE);
            logger.info("signature:{}", signature);
            byte[] body = ByteStreams.toByteArray(request.getInputStream());

            if (!lineMessageService.isSignatureValid(signature, body)) {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }

            String bodyStr = new String(body);

            logger.info("body:" + bodyStr);

            WebhookEvent webhookEvent = lineMessageService.handleWebhookEvent(bodyStr);

            for (Event event : webhookEvent.getEvents()) {
                logger.info("event type:" + event.getType());

                if ("message".equals(event.getType())) {
                    replyTemplateMessage(event);
                }
            }
        logger.info("call back completed.");
        } catch (Exception e) {
            logger.error(e);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    private void replyTemplateMessage(Event event) throws Exception {
        String msgString = event.getMessage().getText();
        String replyToken = event.getReplyToken();
        String userId = event.getSource().getUserId();

        logger.info("event content:" + msgString);
        logger.info("reply token:" + replyToken);
        logger.info("user id:" + userId);

        ReserveResponse reserveResponse = new ReserveResponse();

        if ("pay".equals(msgString.split(" ")[0].toLowerCase())) {
            Long amount = Long.valueOf(msgString.split(" ")[1]);

            reserveResponse = linePayService.invokeReserve(amount, userId);
        }

        String appUrl = reserveResponse.getInfo().getPaymentUrl().getApp();
        String webUrl = reserveResponse.getInfo().getPaymentUrl().getWeb();

//        CommonMessage templateMessage = lineMessageService.getTemplateMessage(replyToken, appUrl);
        CommonMessage templateMessage = lineMessageService.getTextMessage(replyToken, webUrl);

        Gson gson = new Gson();
        String json = gson.toJson(templateMessage);

        logger.info("Reply TemplateMessage:" + json);

        lineMessageService.replyMessage(json);
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.GET)
    public String handleConfirm(@RequestParam("transactionId") long transactionId,
                                @RequestParam("amount") int amount, @RequestParam("userId") String userId) {

        try {
            linePayService.invokeConfirm(transactionId, amount);

            lineMessageService.pushMessage(userId, "2", "35");
        } catch (Exception e) {
            logger.error(e);
        }
        return "redirect:line://ti/p/" + lineId;
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.GET)
    public String handleCancel(@RequestParam("userId") String userId) {

        try {

            lineMessageService.pushMessage(userId, "2", "32");
        } catch (Exception e) {
            logger.error(e);
        }
        return "redirect:line://ti/p/" + lineId;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {

    }
}
