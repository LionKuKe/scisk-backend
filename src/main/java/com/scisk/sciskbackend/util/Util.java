package com.scisk.sciskbackend.util;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class Util {

    public static String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
    public static enum PASSWOR_STATE {INVALID, WEAK, STRONG};
    public static int OTP_LENGTH = 6;
    public static long OTP_DURATION = 10;

    public static Map<String, String> COLLECTION_NAME_COLLECTION_CODE_MAP = new HashMap<>();
    static {
        COLLECTION_NAME_COLLECTION_CODE_MAP.put(GlobalParams.RECORD_COLLECTION_NAME, "SK");
    }

    /**
     * Teste l'adresse email envoyée en paramètre et retourne true si
     * elle est correcte et false dans le cas contraire
     * @param email L'adresse email à tester
     * @return true si l'adresse est correct et false dans le cas contraire
     */
    public static boolean isEmailCorrect(String email) {
        if (Objects.isNull(email)) {
            return false;
        }
        return email.matches(EMAIL_REGEX);
    }

    /**
     * Evalue et retourne l'état du mot de passe envoyé en paramètre
     * @param password le mot de passe à évaluer
     * @return L'état du mot de passe : INVALID, WEAK, STRONG
     *
     */
    public static String getPasswordState(String password) {
        if (Objects.isNull(password)) {
            return PASSWOR_STATE.INVALID.name();
        }

        int passwordLength=8, upChars=0, lowChars=0;
        int special=0, digits=0;
        char ch;

        int total = password.length();
        if(total < passwordLength) {
            return PASSWOR_STATE.INVALID.name();
        } else {
            for(int i=0; i<total; i++) {
                ch = password.charAt(i);
                if(Character.isUpperCase(ch))
                    upChars = 1;
                else if(Character.isLowerCase(ch))
                    lowChars = 1;
                else if(Character.isDigit(ch))
                    digits = 1;
                else
                    special = 1;
            }
        }
        if(upChars==1 && lowChars==1 && digits==1 && special==1)
            return PASSWOR_STATE.STRONG.name();
        else
            return PASSWOR_STATE.WEAK.name();
    }

    public static Long convertStringToLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception exc) {
            return null;
        }
    }

    public static Double convertStringToDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception exc) {
            return null;
        }
    }

    public static <T> T[] removeElementInArray(T[] arr, int index){
        for (int i = index + 1 ; i < arr.length ; i++) {
            arr[i-1] = arr[i];
        }
        return (T[]) Arrays.stream(arr)
                .limit(arr.length - 1)
                .map(t -> (T) t).toArray();
    }

    public static <T> T[] removeElementsInArray(T[] arr, List<Integer> indexes){
        for (Integer index : indexes) {
            for (int i = index + 1 ; i < arr.length ; i++) {
                arr[i-1] = arr[i];
            }
        }
        return (T[]) Arrays.stream(arr).limit(arr.length - indexes.size()).map(t -> (T) t).toArray();
    }

    public static int getYearFromInstant(Instant instant) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        Calendar cal1 = GregorianCalendar.from(zdt);

        return cal1.get(Calendar.YEAR);
    }

    public static int getMonthFromInstant(Instant instant) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        Calendar cal1 = GregorianCalendar.from(zdt);

        return cal1.get(Calendar.MONTH);
    }

    public static int getDayOfMonthFromInstant(Instant instant) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        Calendar cal1 = GregorianCalendar.from(zdt);

        return cal1.get(Calendar.DAY_OF_MONTH);
    }

    public static Instant getFirstDayOfMonth(int year, Month month) {
        var firstDay = LocalDateTime.of(year, month, 1, 0, 0, 0);
        return firstDay.atZone(ZoneId.systemDefault()).toInstant();
    }

    public static Instant getLastDayOfMonth(int year, Month month) {
        var firstDay = LocalDateTime.of(year, month, 1, 0, 0, 0);
        var yearMonth = YearMonth.from(firstDay);
        var lastDay = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return lastDay.atZone(ZoneId.systemDefault()).toInstant();
    }

    public static String addZerosInFrontOfValue(Long value, Integer totalNumberOfChars) {
        if (Objects.isNull(value) || Objects.isNull(totalNumberOfChars) || totalNumberOfChars <= 0) {
            throw new NumberFormatException();
        }
        if (value.toString().length() > totalNumberOfChars)
            return value.toString();
        else {
            return addZeroInFrontOfString(value.toString(), totalNumberOfChars);
        }
    }

    public static String addZeroInFrontOfString(String value, Integer totalNumberOfChars) {
        if (value.length() == totalNumberOfChars) {
            return value;
        } else {
            return addZeroInFrontOfString("0" + value, totalNumberOfChars);
        }
    }

    public static <T> T testListNullAndGetFirstItem(List<T> list) {
        return (Objects.isNull(list) || list.isEmpty()) ? null : list.get(0);
    }
}
