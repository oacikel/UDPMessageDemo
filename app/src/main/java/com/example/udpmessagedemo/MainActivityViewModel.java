package com.example.udpmessagedemo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

public class MainActivityViewModel {
    public byte[] getByteArray(int num) {
        List<Integer> digits = new ArrayList<Integer>();
        collectDigits(num, digits);
        Integer[] intArray = digits.toArray(new Integer[]{});
        return convertIntArrayToByteArray(intArray);
    }

    private void collectDigits(int num, List<Integer> digits) {
        if (num / 10 > 0) {
            collectDigits(num / 10, digits);
        }
        digits.add(num % 10);
    }

    public String getLocalIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress();
                    }

                }

            }

        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

    private byte[] convertIntArrayToByteArray(Integer[] intArray) {
        byte[] byteArray = new byte[intArray.length];

        for (int i = 0; i < intArray.length; i++) {
            byteArray[i] = intArray[i].byteValue();
        }
        return byteArray;
    }

    public String generateRandomInput() {
        int m = (int) Math.pow(10, 10 - 1);
        int input= m + new Random().nextInt(9 * m);
        return String.valueOf(input);
    }
}
