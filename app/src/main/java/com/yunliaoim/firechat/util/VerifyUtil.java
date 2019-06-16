package com.yunliaoim.firechat.util;

public class VerifyUtil {

//    public static boolean isIP(String ipStr) {
//        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
//        Pattern pattern = Pattern.compile(ip);
//        Matcher matcher = pattern.matcher(ipStr);
//        return matcher.matches();
//    }

    public static boolean checkEmpty(String s) {
        if (s == null) {
            return true;
        }

        return "".equals(s);
    }

    public static boolean checkIP(String s) {
        if (s == null) {
            return false;
        }

        return s.matches("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))");
    }

    public static boolean checkPort(String s) {
        if (s == null) {
            return false;
        }

        return s.matches("^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-6][0-5][0-5][0-3][0-5]$)");
    }

}
