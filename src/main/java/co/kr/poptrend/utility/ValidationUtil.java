package co.kr.poptrend.utility;

import java.util.ArrayList;
import java.util.Map;

public class ValidationUtil {
    public static boolean checkSpace(String word) {
        boolean result = false;
        if(word == null || word.isEmpty()) {
            return true;
        }
        char[] arrCh = word.toCharArray();
        for (char c : arrCh) {
            if (Character.isWhitespace(c)) {
                return true;
            }
        }
        if (word.chars().allMatch(Character::isWhitespace)) {
            return true;
        }
        return result;
    }

    public static boolean isContainKeyAndValue(Map<String, Object> data, ArrayList<String> keys) {
        boolean result = true;
        for (String s : keys) {
            if (!data.containsKey(s)) {
                return false;
            }
            if (data.get(s) == null) {
                return false;
            }
        }
        return result;
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str) || str.trim().length() == 0;
    }
}
