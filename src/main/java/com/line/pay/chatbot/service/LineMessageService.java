package com.line.pay.chatbot.service;

import com.google.gson.Gson;
import com.line.pay.chatbot.events.*;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author wangtianqi20
 */
@Service
public class LineMessageService {
    private static Logger logger = LogManager.getLogger(LineMessageService.class.getName());

    private static final String HASH_ALGORITHM = "HmacSHA256";

    @Value("${line.channel.id}")
    private String channelId;

    @Value("${line.channel.secret}")
    private String channelSecret;

    @Value("${line.access.token}")
    private String accessToken;

    @Value("${line.api.url}")
    private String apiUrl;

    @Value("${line.api.message.reply.url}")
    private String replyUrl;

    @Value("${line.api.message.push.url}")
    private String pushUrl;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public String getChannelSecret() {
        return this.channelSecret;
    }

    public WebhookEvent handleWebhookEvent(String bodyStr) throws Exception {
        Gson gson = new Gson();
        return gson.fromJson(bodyStr, WebhookEvent.class);
    }

    public void replyMessage(String json) throws Exception {

        String url = apiUrl + replyUrl;
        sendMessage(json, url);

//                    var textList = new ArrayList<String>();
//                    textList.add(event.getMessage().getText());

        //dialogFlowService.detectIntentTexts("linepaydev", textList, event.getReplyToken(), "en-US");

    }

    public void sendMessage(String json, String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);
        String token = "Bearer " + accessToken;

        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        logger.info("Response HTTP Status:" + response.code());
        logger.info("Response body:" + response.body().string());

        response.close();
    }

    public void pushMessage(String userId, String packageId, String stickerId) throws Exception {
        PushMessage pushMsg = new PushMessage();
        StickerMessage stickerMsg = new StickerMessage();

        stickerMsg.setPackageId(packageId);
        stickerMsg.setStickerId(stickerId);
        stickerMsg.setType("sticker");

        List messages = new ArrayList<StickerMessage>();
        messages.add(stickerMsg);

        pushMsg.setMessages(messages);
        pushMsg.setTo(userId);

        Gson gson = new Gson();
        String json = gson.toJson(pushMsg);

        String url = apiUrl + pushUrl;

        sendMessage(json, url);
    }

    /**
     * Text Message
     *
     * @param replyToken
     * @param appUrl
     * @return
     */
    public CommonMessage getTextMessage(String replyToken, String appUrl) {
        CommonMessage commonMessage = new CommonMessage();

        TextMessage message = new TextMessage();

        message.setType("text");
        message.setText(appUrl);

        List messages = new ArrayList<Message>();
        messages.add(message);

        commonMessage.setReplyToken(replyToken);
        commonMessage.setMessages(messages);
        return commonMessage;
    }


    public CommonMessage getTemplateMessage(String replyToken, String appUrl) {
        CommonMessage templateMessage = new CommonMessage();

        Action action = new Action();

        List actions = new ArrayList<Action>();
        actions.add(action);

        action.setType("uri");
        action.setLabel("LINE Pay");
        action.setUri(appUrl);

        Template template = new Template();

        template.setType("buttons");
        template.setText("Tap button to pay");
        template.setActions(actions);

        Message message = new Message();

        message.setType("template");
        message.setAltText("Paid by LINE Pay");
        message.setTemplate(template);

        List messages = new ArrayList<Message>();
        messages.add(message);

        templateMessage.setReplyToken(replyToken);
        templateMessage.setMessages(messages);
        return templateMessage;
    }

    public boolean isSignatureValid(String signature, byte[] body) {
        boolean result = false;

        try {
            Mac sha256HMAC = Mac.getInstance(HASH_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(this.getChannelSecret().getBytes(StandardCharsets.UTF_8), HASH_ALGORITHM);
            sha256HMAC.init(secretKey);

            final byte[] headerSignature = Base64.getDecoder().decode(signature);
            final byte[] bodySignature = sha256HMAC.doFinal(body);

            logger.info("headerSignature = " + headerSignature);
            logger.info("bodySignature = " + bodySignature);

            if (MessageDigest.isEqual(headerSignature, bodySignature)) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            return result;
        }
    }
}
