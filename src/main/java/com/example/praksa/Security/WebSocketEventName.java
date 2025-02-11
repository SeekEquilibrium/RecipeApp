package com.example.praksa.Security;

public enum WebSocketEventName {
    SUBSCRIBE(""),
    CONNECT("/chat/login"),
    DISCONNECT("/chat/logout"),
    MESSAGE("");

    private String destination;

    WebSocketEventName(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return this.destination;
    }
}
