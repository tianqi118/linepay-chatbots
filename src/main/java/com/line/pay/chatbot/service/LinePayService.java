package com.line.pay.chatbot.service;

import com.google.gson.Gson;
import com.line.pay.chatbot.payment.*;
import okhttp3.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author wangtianqi20
 */
@Service
public class LinePayService {
    private static Logger logger = LogManager.getLogger(LineMessageService.class.getName());

    @Value("${line.pay.channel.id}")
    private String channelId;

    @Value("${line.pay.channel.secret}")
    private String channelSecret;

    @Value("${line.pay.api.url}")
    private String payApiUrl;

    @Value("${line.pay.api.reserve.url}")
    private String payApiReserveUrl;

    @Value("${line.pay.confirm.url}")
    private String confirmUrl;

    @Value("${line.pay.cancel.url}")
    private String cancelUrl;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public ReserveResponse invokeReserve(long amount, String userId) {
        ReserveRequestV13 reserveRequest = getReserveRequest(amount, userId);

        Gson gson = new Gson();

        String json = gson.toJson(reserveRequest);
        logger.info("Reserve request:" + json);

        ReserveResponse reserveResponse = getReserveResponse(gson, json);

        return reserveResponse;
    }

    public void invokeConfirm(long transactionId, int amount) throws Exception {

        String url = payApiUrl + "/v3/payments/" + transactionId + "/confirm";

        logger.info("confirm API:" + url);

        OkHttpClient client = new OkHttpClient();

        ConfirmRequest confirmRequest = new ConfirmRequest();

        confirmRequest.setAmount(amount);
        confirmRequest.setCurrency("TWD");

        Gson gson = new Gson();
        String json = gson.toJson(confirmRequest);

        logger.info("confirm request:" + json);

        RequestBody body = RequestBody.create(JSON, json);

        String once = UUID.randomUUID().toString();
        String signature = encrypt(channelSecret, channelSecret + payApiReserveUrl + json + once);

        Request request = buildLinePayRequest(body, url, signature, once);
        Response response = client.newCall(request).execute();

        logger.info("Response HTTP Status:" + response.code());

        String responseBody = response.body().string();

        logger.info("Response Body:" + responseBody);

        response.close();

        //var confirmResponse = gson.fromJson(responseBody, ConfirmResponse.class);
    }

    public ReserveRequestV13 getReserveRequest(long amount, String userId) {
        ReserveRequestV13 reserveRequest = new ReserveRequestV13();

        String orderId = UUID.randomUUID().toString();

        reserveRequest.setAmount(amount);
        reserveRequest.setCurrency("THB");
        reserveRequest.setOrderId(orderId);

        List<ReserveRequestV13.Packages> packagesList = new ArrayList<>();
        ReserveRequestV13.Packages packages = new ReserveRequestV13().new Packages();
        packages.setId(UUID.randomUUID().toString());
        packages.setAmount(amount);
        packages.setName("Paykage name candys");

        List<Products> productsList = new ArrayList<>();
        Products products = new Products();
        products.setName("Products Name1");
        products.setPrice(amount);
        products.setQuantity(1);
        productsList.add(products);
        packages.setProducts(productsList);

        packagesList.add(packages);
        reserveRequest.setPackages(packagesList);


        ReserveRequestV13.RedirectUrls redirectUrls = new ReserveRequestV13().new RedirectUrls();
        redirectUrls.setConfirmUrl(confirmUrl + "?amount=" + amount + "&userId=" + userId);
        redirectUrls.setConfirmUrlType("CLIENT");
        redirectUrls.setCancelUrl(cancelUrl + "?userId=" + userId);
        reserveRequest.setRedirectUrls(redirectUrls);

        return reserveRequest;
    }


//    public ReserveRequest getReserveRequest(long amount, String userId) {
//        var reserveRequest = new ReserveRequest();
//
//        var orderId = UUID.randomUUID().toString();
//
//        reserveRequest.setAmount(amount);
//        reserveRequest.setCapture("true");
//        reserveRequest.setCheckConfirmUrlBrowser("false");
//        reserveRequest.setConfirmUrl(confirmUrl + "?amount=" + amount + "&userId=" + userId);
//        reserveRequest.setCancelUrl(cancelUrl + "?userId=" + userId);
//        reserveRequest.setConfirmUrlType("CLIENT");
//        reserveRequest.setCurrency("HTB");
//        reserveRequest.setOrderId(orderId);
//        reserveRequest.setPayType("NORMAL");
////        reserveRequest.setProductImageUrl("testUrl");
//        reserveRequest.setProductName("TEST");
//        return reserveRequest;
//    }

    public ReserveResponse getReserveResponse(Gson gson, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        String url = payApiUrl + payApiReserveUrl;
        logger.info("request Url:{}", url);
        OkHttpClient client = new OkHttpClient();

        String once = UUID.randomUUID().toString();
        String signature = encrypt(channelSecret, channelSecret + payApiReserveUrl + json + once);

        Request request = buildLinePayRequest(body, url, signature, once);

        ReserveResponse reserveResponse = new ReserveResponse();

        try {
            Response response = client.newCall(request).execute();
            logger.info("Response HTTP Status:" + response.code());

            String responseBody = response.body().string();
            logger.info("responseBody:{}", responseBody);
            response.close();

            reserveResponse = gson.fromJson(responseBody, ReserveResponse.class);

        } catch (Exception e) {
            logger.error(e);
        }
        return reserveResponse;
    }


    public static String encrypt(final String keys, final String data) {
        return toBase64String(HmacUtils.getHmacSha256(keys.getBytes()).doFinal(data.getBytes()));
    }

    public static String toBase64String(byte[] bytes) {
        byte[] byteArray = Base64.encodeBase64(bytes);
        return new String(byteArray);
    }

    public Request buildLinePayRequest(RequestBody body, String url, String signature, String once) {
        return new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("X-LINE-ChannelId", channelId)
                .addHeader("X-LINE-Authorization-Nonce", once)
                .addHeader("X-LINE-Authorization", signature)
                .url(url)
                .post(body)
                .build();
    }

    //back up 2020-6-30
//    public Request buildLinePayRequest(RequestBody body, String url) {
//        return new Request.Builder()
//                .addHeader("X-LINE-ChannelId", channelId)
//                .addHeader("X-LINE-ChannelSecret", channelSecret)
//                .url(url)
//                .post(body)
//                .build();
//    }

}
