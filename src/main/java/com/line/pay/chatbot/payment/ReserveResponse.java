
package com.line.pay.chatbot.payment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReserveResponse {

    @SerializedName("returnCode")
    @Expose
    private String returnCode;
    @SerializedName("returnMessage")
    @Expose
    private String returnMessage;
    @SerializedName("info")
    @Expose
    private Info info;

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public class Info {

        @SerializedName("paymentUrl")
        @Expose
        private PaymentUrl paymentUrl;
        @SerializedName("transactionId")
        @Expose
        private long transactionId;
        @SerializedName("paymentAccessToken")
        @Expose
        private String paymentAccessToken;

        public PaymentUrl getPaymentUrl() {
            return paymentUrl;
        }

        public void setPaymentUrl(PaymentUrl paymentUrl) {
            this.paymentUrl = paymentUrl;
        }

        public long getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(long transactionId) {
            this.transactionId = transactionId;
        }

        public String getPaymentAccessToken() {
            return paymentAccessToken;
        }

        public void setPaymentAccessToken(String paymentAccessToken) {
            this.paymentAccessToken = paymentAccessToken;
        }

    }

    public class PaymentUrl {

        @SerializedName("web")
        @Expose
        private String web;
        @SerializedName("app")
        @Expose
        private String app;

        public String getWeb() {
            return web;
        }

        public void setWeb(String web) {
            this.web = web;
        }

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }

    }

}
