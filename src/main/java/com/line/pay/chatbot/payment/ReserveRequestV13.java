package com.line.pay.chatbot.payment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author wangtianqi20
 */
public class ReserveRequestV13 {

    @SerializedName("amount")
    @Expose
    private long amount;

    @SerializedName("currency")
    @Expose
    private String currency;

    @SerializedName("orderId")
    @Expose
    private String orderId;

    @SerializedName("packages")
    @Expose
    private List<Packages> packages;

    @SerializedName("redirectUrls")
    @Expose
    private RedirectUrls redirectUrls;


    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<Packages> getPackages() {
        return packages;
    }

    public void setPackages(List<Packages> packages) {
        this.packages = packages;
    }

    public RedirectUrls getRedirectUrls() {
        return redirectUrls;
    }

    public void setRedirectUrls(RedirectUrls redirectUrls) {
        this.redirectUrls = redirectUrls;
    }

    public class Packages {

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("amount")
        @Expose
        private long amount;

        @SerializedName("userFee")
        @Expose
        private long userFee;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("products")
        @Expose
        private List<Products> products;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }

        public long getUserFee() {
            return userFee;
        }

        public void setUserFee(long userFee) {
            this.userFee = userFee;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Products> getProducts() {
            return products;
        }

        public void setProducts(List<Products> products) {
            this.products = products;
        }
    }


    public class RedirectUrls {

        @SerializedName("appPackageName")
        @Expose
        private String appPackageName;

        @SerializedName("confirmUrl")
        @Expose
        private String confirmUrl;

        @SerializedName("confirmUrlType")
        @Expose
        private String confirmUrlType;

        @SerializedName("cancelUrl")
        @Expose
        private String cancelUrl;


        public String getAppPackageName() {
            return appPackageName;
        }

        public void setAppPackageName(String appPackageName) {
            this.appPackageName = appPackageName;
        }

        public String getConfirmUrl() {
            return confirmUrl;
        }

        public void setConfirmUrl(String confirmUrl) {
            this.confirmUrl = confirmUrl;
        }

        public String getCancelUrl() {
            return cancelUrl;
        }

        public void setCancelUrl(String cancelUrl) {
            this.cancelUrl = cancelUrl;
        }

        public String getConfirmUrlType() {
            return confirmUrlType;
        }

        public void setConfirmUrlType(String confirmUrlType) {
            this.confirmUrlType = confirmUrlType;
        }
    }

}