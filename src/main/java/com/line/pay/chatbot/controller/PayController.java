package com.line.pay.chatbot.controller;

import com.google.gson.Gson;
import com.line.pay.chatbot.events.TemplateMessage;
import com.line.pay.chatbot.payment.ReserveResponse;
import com.line.pay.chatbot.service.LineMessageService;
import com.line.pay.chatbot.service.LinePayService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * TODO
 *
 * @author tianqi
 * @date 2020-06-30
 */
@Controller
public class PayController {
    private static Logger logger = LogManager.getLogger(CallbackController.class.getName());
    @Autowired
    private LineMessageService lineMessageService;
    @Autowired
    private LinePayService linePayService;


    @RequestMapping(value = "/pay", method = RequestMethod.GET)
    @ResponseBody
    public String handleConfirm(@RequestParam("amount") int amount, @RequestParam("userId") String userId) throws Exception {
        logger.info("amout:{}，userId:{}", amount, userId);
        ReserveResponse reserveResponse = new ReserveResponse();

        reserveResponse = linePayService.invokeReserve(amount, userId);

        logger.info("transactionId:{}",reserveResponse.getInfo().getTransactionId());
        logger.info("paymentAccessToken:{}",reserveResponse.getInfo().getPaymentAccessToken());
        String appUrl = reserveResponse.getInfo().getPaymentUrl().getApp();
        logger.info("appUrl:{}", appUrl);
        String webUrl = reserveResponse.getInfo().getPaymentUrl().getWeb();
        logger.info("webUrl:{}", webUrl);


        TemplateMessage templateMessage = lineMessageService.getTemplateMessage("", appUrl);

        Gson gson = new Gson();
        String json = gson.toJson(templateMessage);

        logger.info("Reply TemplateMessage:" + json);

//        return lineMessageService.replyMessage(json);
        return "success";
    }

}
