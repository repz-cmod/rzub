package cmod.repz.application.util;

public class GameUtil {
    public static String cleanColors(String input){
        int index = 0;
        do {
            index = input.indexOf("^");
            input = setCharAt(input, index, "");
            input = setCharAt(input, index, "");
        }while (index != -1);

        return input;
    }

    private static String setCharAt(String input, int index, String c){
        if(index > input.length() - 1) return input;
        return input.substring(0, index) + c + input.substring(index + 1);
    }
}
