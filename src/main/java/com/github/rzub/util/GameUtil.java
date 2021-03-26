package com.github.rzub.util;

public class GameUtil {
    public static String cleanColors(String input){
        int index = 0;
        while ((index = input.indexOf("^")) != -1){
            input = setCharAt(input, index, "");
            input = setCharAt(input, index, "");
        }
        return input;
    }

    private static String setCharAt(String input, int index, String c){
        if(index > input.length() - 1) return input;
        return input.substring(0, index) + c + input.substring(index + 1);
    }
}
