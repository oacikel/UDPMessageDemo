package com.example.udpmessagedemo;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class UDPMessageHelper extends Thread {
    private static String LOG_TAG = ("OCUL-UDPMessageHelper");
    private Repository repository = Repository.getInstance();
    private MainActivity mainActivity;
    private DatagramPacket packetReceived, packetSent;
    private DatagramSocket socket;
    private byte[] incomingMessage, outgoingMessage, previousIncomingMessage, previousOutgoingMessage;
    private int homePort, destinationPort;
    private String localAddress, destinationAddress;
    private TextView textViewIncomingMessage;


    public UDPMessageHelper(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        updateUDPData();
    }

    @Override
    public void run() {
        updateUDPData();
        if (Repository.getInstance().isServer()) {
            packetSent = new DatagramPacket(outgoingMessage, outgoingMessage.length);
            disconnectFromSocket();
        } else {
            packetReceived = new DatagramPacket(incomingMessage, incomingMessage.length);
            disconnectFromSocket();
        }
        while (true) {
            updateUDPData();
            if (socket == null) {
                Log.i(LOG_TAG, "Socket is null. Creating new socket");
                createSocket();
            } else if (socket.isConnected()) {
                if (Repository.getInstance().isServer()) {
                    if (outgoingMessage != null) {
                        sendData();
                    } else {
                        Log.w(LOG_TAG, "Outgoing Message has not yet been set!");
                        packetSent = new DatagramPacket(outgoingMessage, outgoingMessage.length);
                    }
                } else {
                    if (incomingMessage != null) {
                        receiveData();
                    } else {
                        Log.w(LOG_TAG, "Incoming Message has not yet been set!");
                        packetReceived = new DatagramPacket(incomingMessage, incomingMessage.length);
                    }
                }
            }
        }
    }

    private void createSocket() {
        try {
            socket = new DatagramSocket(homePort, InetAddress.getByName(localAddress));

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in creating socket: " + e.getMessage());
            return;
        }
        try {
            socket.setReuseAddress(true);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Can't set address reusable: " + e.getMessage());
        }
        try {
            socket.connect(InetAddress.getByName(destinationAddress), destinationPort);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Can't connect socket: " + e.getMessage());
        }
        try {
            socket.setSoTimeout(5000);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Can't set timeout: " + e.getMessage());
        }
        try {
            socket.setBroadcast(false);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Can't set broadcast: " + e.getMessage());
        }
    }

    private void sendData() {
        try {
            if (isChanged(previousOutgoingMessage, repository.getOutgoingMessage())) {
                packetSent.setData(outgoingMessage);
                socket.send(packetSent);
                Log.i(LOG_TAG, "Sent data is: " + Arrays.toString(packetSent.getData()));
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in sending packet: " + e.getMessage());
            socket.disconnect();
            socket.close();
            socket = null;
        }
    }

    private void receiveData() {
        try {
            socket.receive(packetReceived);
            repository.setIncomingMessage(packetReceived.getData());

            if (isChanged(previousIncomingMessage, repository.getIncomingMessage())) {
                Log.i(LOG_TAG, "Received data is: " + Arrays.toString(packetReceived.getData()));
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.getTextViewIncomingMessage().setText(Arrays.toString(packetReceived.getData()));
                    }
                });

            }


        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in receiving packet: " + e.getMessage());
            socket.disconnect();
            socket.close();
            socket = null;
        }
    }

    public void updateUDPData() {
        if (Repository.getInstance().isServer()) {
            if (Repository.getInstance().getOutgoingMessage() != null) {
                outgoingMessage = Repository.getInstance().getOutgoingMessage();
                previousOutgoingMessage = new byte[outgoingMessage.length];
            } else {
                Log.w(LOG_TAG, "Outgoing Message is not yet set. Setting up an empty array as placeholder.");
                outgoingMessage = new byte[10];
                Repository.getInstance().setOutgoingMessage(outgoingMessage);
                previousOutgoingMessage = new byte[outgoingMessage.length];
            }
        } else {
            if (Repository.getInstance().getIncomingMessage() != null) {
                incomingMessage = Repository.getInstance().getIncomingMessage();
                previousIncomingMessage = new byte[incomingMessage.length];
            } else {
                Log.w(LOG_TAG, "No message received yet");
            }
        }

        if (Repository.getInstance().getHomePort() != 0) {
            homePort = Repository.getInstance().getHomePort();
        } else {
            Log.w(LOG_TAG, "Home Port is not yet set. (It is 0)");
        }
        if (Repository.getInstance().getDestinationPort() != 0) {
            destinationPort = Repository.getInstance().getDestinationPort();
        } else {
            Log.w(LOG_TAG, "Destination Port is not yet set. (It is 0)");
        }
        if (Repository.getInstance().getLocalAddress() != null) {
            localAddress = Repository.getInstance().getLocalAddress();
        } else {
            Log.w(LOG_TAG, "Local adress is not yet set");
        }
        if (Repository.getInstance().getDestinationAddress() != null) {
            destinationAddress = Repository.getInstance().getDestinationAddress();
        } else {
            Log.w(LOG_TAG, "Destination adress is not yet set");
        }
    }

    private void disconnectFromSocket() {
        if (socket != null) {
            socket.disconnect();
            socket.close();
        } else {
            Log.e(LOG_TAG, "Socket is null cannot disconnect.");
        }

    }

    private void makeEqual(byte[] a1, byte[] a2) {
        if (a1.length == a2.length) {
            for (int i = 0; i < a1.length; i++) {
                a1[i] = a2[i];
            }
        }
    }

    public boolean isChanged(byte[] beforeArray, byte[] afterArray) {
        boolean isDifferent = false;
        if (Arrays.toString(beforeArray).equals(Arrays.toString(afterArray))) {
            isDifferent = false;
        } else if (!Arrays.toString(beforeArray).equals(Arrays.toString(afterArray))) {
            makeEqual(beforeArray, afterArray);
            isDifferent = true;
        }
        return isDifferent;
    }
}
