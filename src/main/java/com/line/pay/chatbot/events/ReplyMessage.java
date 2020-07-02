
package com.line.pay.chatbot.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReplyMessage {

    @SerializedName("replyToken")
    @Expose
    private String replyToken;
    @SerializedName("messages")
    @Expose
    private List<ButtonMessage> messages;

    public String getReplyToken() {
        return replyToken;
    }

    public void setReplyToken(String replyToken) {
        this.replyToken = replyToken;
    }

    public List<ButtonMessage> getMessages() {
        return messages;
    }

    public void setMessage(List<ButtonMessage> messages) {
        this.messages = messages;
    }

}
