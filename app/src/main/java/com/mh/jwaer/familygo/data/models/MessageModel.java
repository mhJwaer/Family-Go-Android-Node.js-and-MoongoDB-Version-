package com.mh.jwaer.familygo.data.models;

public class MessageModel {


    private String senderId;
    private String content;
    private String circle;
    private String createdAt;

    public MessageModel(String senderId, String content, String circle, String createdAt) {
        this.senderId = senderId;
        this.content = content;
        this.circle = circle;
        this.createdAt = createdAt;
    }

    public MessageModel() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
