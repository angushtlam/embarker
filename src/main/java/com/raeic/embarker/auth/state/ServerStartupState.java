package com.raeic.embarker.auth.state;

public class ServerStartupState {
    public static ServerStartupState instance = new ServerStartupState();

    boolean isReady = false;
    String message = "The server is starting up...";

    private ServerStartupState() {}

    public boolean isReady() {
        return this.isReady;
    }

    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
