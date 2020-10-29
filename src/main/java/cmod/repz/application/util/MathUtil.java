package cmod.repz.application.util;

import java.net.InetAddress;

public class MathUtil {
    public static double percent(int x, int y){
        return (x * 100.0 / y);
    }

    public static long ipToLong(InetAddress ip) {
        byte[] octets = ip.getAddress();
        long result = 0;
        for (byte octet : octets) {
            result <<= 8;
            result |= octet & 0xff;
        }
        return result;
    }
}
