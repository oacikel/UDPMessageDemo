package com.example.udpmessagedemo;

public class Repository {
    //Singleton Structure
    private static Repository instance;
    private byte[] incomingMessage = new byte[10];
    private byte[] outgoingMessage;
    private int homePort, destinationPort;
    private String localAddress, destinationAddress;
    private boolean isServer;

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public byte[] getIncomingMessage() {
        return incomingMessage;
    }

    public void setIncomingMessage(byte[] incomingMessage) {
        this.incomingMessage = incomingMessage;
    }

    public byte[] getOutgoingMessage() {
        return outgoingMessage;
    }

    public void setOutgoingMessage(byte[] outgoingMessage) {
        this.outgoingMessage = outgoingMessage;
    }

    public int getHomePort() {
        return homePort;
    }

    public void setHomePort(int homePort) {
        this.homePort = homePort;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }
}
