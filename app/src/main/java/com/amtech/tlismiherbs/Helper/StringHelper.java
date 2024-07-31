package com.sellacha.tlismiherbs.Helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {

    private static Pattern pattern;
    private static Matcher matcher;
    public static final String SPACE = " ";
    public static final String EMPTY = "";
    public static final String LF = "\n";
    public static final String CR = "\r";
    public static final int INDEX_NOT_FOUND = -1;
    private static final int PAD_LIMIT = 8192;

    private static final String PASSWORD_REGEX = "^(?=.*\\d)(?=.*[A-Z])(?=.*[A-Z]).{6,}$";
    //    private static final String USERNAME_REGEX = "[a-zA-Z][a-zA-Z0-9]";
    private static final String USERNAME_REGEX = "[a-zA-Z0-9]+";


    public static boolean validateEmail(String emailStr)
    {
        return !StringHelper.isEmpty(emailStr) && Patterns.EMAIL_ADDRESS.matcher(emailStr).matches();
    }

    public static String format(int value)
    {
        return String.format(Locale.getDefault(), "%d", value);
    }

    public static String format(long value)
    {
        return String.format(Locale.getDefault(), "%d", value);
    }

    public static String format(double value)
    {
        return String.format(Locale.getDefault(), "%.2f", value);
    }

    public static String formatPrice(String currencySymbol, double value)
    {
        if (!StringHelper.isEmpty(currencySymbol))
        {
            if (value != 0)
            {
                DecimalFormat mFormat = new DecimalFormat("#,###,###.00");
                return currencySymbol + mFormat.format(value);
            } else
            {
                return currencySymbol + String.format(Locale.getDefault(), "%.2f", value);
            }
        } else
        {
            return String.format(Locale.getDefault(), "%.2f", value);
        }
    }

    public static String getRandomString(int sizeOfRandomString)
    {
        String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static String uniqueId()
    {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    public static String trimString(String value)
    {
        return value == null ? "" : value.trim();
    }

    public static boolean isEmpty(String value)
    {
        return value == null || value.trim().equals("");
    }

    public static boolean validateLength(String value, int length)
    {
        return value != null && value.trim().length() < length;
    }

    public static int convertToInt(String value)
    {
        if (value == null || value.trim().equals(""))
            return 0;

        try
        {
            return Integer.parseInt(value);
        } catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    public static float convertToFloat(String value)
    {
        if (value == null || value.trim().equals(""))
            return 0f;

        try
        {
            return Float.parseFloat(value);
        } catch (Exception e)
        {
            e.printStackTrace();
            return 0f;
        }
    }

    public static double convertToDouble(String value)
    {
        if (value == null || value.trim().equals(""))
            return 0.00;

        try
        {
            return Double.parseDouble(value);
        } catch (Exception e)
        {
            e.printStackTrace();
            return 0.00;
        }
    }

    /**
     * Convert byte array to hex string
     *
     * @param bytes toConvert
     * @return hexValue
     */
    public static String bytesToHex(byte[] bytes)
    {
        StringBuilder sbuf = new StringBuilder();
        for (byte aByte : bytes)
        {
            int intVal = aByte & 0xff;
            if (intVal < 0x10) sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * Get utf8 byte array.
     *
     * @param str which to be converted
     * @return array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str)
    {
        try
        {
            return str.getBytes("UTF-8");
        } catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     *
     * @param filename which to be converted to string
     * @return String value of File
     * @throws IOException if error occurs
     */
    public static String loadFileAsString(String filename) throws IOException
    {
        final int BUFLEN = 1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8 = false;
            int read, count = 0;
            while ((read = is.read(bytes)) != -1)
            {
                if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF)
                {
                    isUTF8 = true;
                    baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
                } else
                {
                    baos.write(bytes, 0, read);
                }
                count += read;
            }
            return isUTF8 ? new String(baos.toByteArray(), "UTF-8") : new String(baos.toByteArray());
        } finally
        {
            try
            {
                is.close();
            } catch (Exception ignored)
            {
            }
        }
    }

    public static String formatAddress(String address, String city, String state, String zip)
    {
        String addressStr = StringHelper.isEmpty(address) ? "" : address;
        String cityStr = StringHelper.isEmpty(city) ? "" : city;
        String stateStr = StringHelper.isEmpty(state) ? "" : state;
        String zipStr = StringHelper.isEmpty(zip) ? "" : zip;

        String formatAddress = addressStr;
        if (!StringHelper.isEmpty(cityStr))
            formatAddress += ", " + cityStr;

        if (!StringHelper.isEmpty(stateStr))
            formatAddress += ", " + stateStr;

        if (!StringHelper.isEmpty(zipStr))
            formatAddress += ", " + zipStr;

        return formatAddress;
    }

    public static String[] splitWith(String value, String regex, int limit)
    {
        if (limit == -1)
            return value.split(regex);

        String[] array = new String[limit];
        String[] temp = value.split(regex, limit);
        for (int i = 0; i < limit; i++)
        {
            if (i < temp.length)
                array[i] = temp[i];
            else
                array[i] = "";
        }
        return array;
    }

//	public static String formatAddressNewLine(String address, String city, String state, String zip)
//	{
//		String addressStr = StringHelper.isEmpty(address) ? "" : address;
//		String cityStr = StringHelper.isEmpty(city) ? "" : city;
//		String stateStr = StringHelper.isEmpty(state) ? "" : state;
//		String zipStr = StringHelper.isEmpty(zip) ? "" : zip;
//
//		String formatAddress = addressStr;
//		if (!StringHelper.isEmpty(cityStr))
//			formatAddress += "\n" + cityStr;
//
//		if (!StringHelper.isEmpty(stateStr))
//			formatAddress += ", " + stateStr;
//
//		if (!StringHelper.isEmpty(zipStr))
//			formatAddress += ", " + zipStr;
//
//		return formatAddress;
//	}

    public static String formatFileSize(long size)
    {
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * Validate password with regular expression
     *
     * @param password password for validation
     * @return true valid password, false invalid password
     */
    public static boolean validateCapitalLetter(String password)
    {
        Log.v("regexMatch-->", Pattern.matches(PASSWORD_REGEX, password) + "");
        return Pattern.matches(PASSWORD_REGEX, password);
    }

    public static boolean validateUserName(String username)
    {
        Log.v("regexMatch-->", Pattern.matches(USERNAME_REGEX, username) + "");
        return Pattern.matches(USERNAME_REGEX, username);
    }

    public static String getDeviceId(Context context)
    {
        return android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    public static String getInitial(String text)
    {
        if (StringHelper.isEmpty(text))
            return "";

        return text.substring(0, 1);
    }

    public static String formatPhoneNumber(String countryCode, String phoneNumber)
    {
        String val = phoneNumber.toString();
        String a = "";
        String b = "";
        String c = "";
        if (val.length() >= 3)
        {
            a = val.substring(0, 3);
        } else
        {
            a = val.substring(0, val.length());
        }
        if (val.length() >= 6)
        {
            b = val.substring(3, 6);
            c = val.substring(6, val.length());
        } else if (val.length() > 3)
        {
            b = val.substring(3, val.length());
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(countryCode);
        stringBuilder.append(" ");
        if (a.length() > 0)
        {
            stringBuilder.append(a);
            if (a.length() == 3)
            {
                stringBuilder.append("-");
            }
        }
        if (b.length() > 0)
        {
            stringBuilder.append(b);
            if (b.length() == 3)
            {
                stringBuilder.append("-");
            }
        }
        if (c.length() > 0)
        {
            stringBuilder.append(c);
        }

        return stringBuilder.toString();
    }

    public static String convertStringArrayListToString(ArrayList<String> list) {

        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (String s : list) {
            sb.append(delim);
            sb.append(s);
            delim = ",";
        }
        return sb.toString();
    }

    // Convert Strings into ArrayList
    public static ArrayList<String> convertStringToStringArrayList(String string) {

        return new ArrayList<>(Arrays.asList(string
                .split(",")));
    }

    // Convert Strings into ArrayList
    public static ArrayList<Integer> convertStringToIntegerArrayList(String string) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        String[] splitString = string.split(",");
        for (String s : splitString) {
            arrayList.add(Integer.parseInt(s));
        }
        return arrayList;

    }

    public static String truncateLastCharacterFromString(String value, int charToTruncate) {
        if (TextUtils.isEmpty(value))
            return value;
        return value.substring(0, value.length() - charToTruncate);
    }

    public static String extractNumberFromString(String value) {
        return value.replaceAll("\\D+", "");
    }

    public static String[] splitString(String value, String delimiter) {
        if (!TextUtils.isEmpty(value)) {
            if (value.contains(delimiter)) {
                return value.split(delimiter);
            }
        }
        return new String[]{value};
    }

    public static int getCurrentSelectedItemFromStringArray(String[] listItems, String currentItem) {
        for (int i = 0; i < listItems.length; i++) {
            if (listItems[i].equalsIgnoreCase(currentItem)) {
                return i;
            }
        }

        return -1;
    }

    public static Long[] toPrimitives(long... objects) {

        Long[] primitives = new Long[objects.length];
        for (int i = 0; i < objects.length; i++)
            primitives[i] = objects[i];

        return primitives;
    }

    public static String convertFirstLetterCaps(String value) {
        if (TextUtils.isEmpty(value))
            return value;
        try {
            value = value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
            return value;
        } catch (Exception ignored) {
            return value;
        }

    }

    public static String readJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        final char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }

        return new StringBuilder(strLen)
                .append(Character.toTitleCase(firstChar))
                .append(str.substring(1))
                .toString();
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String setPrefixString(String prefixStr, String mainStr) {
        return String.format("%s %s",prefixStr , mainStr);
    }
    public static String formatDouble(double d){
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(d);
    }

    public static String roundDouble(double d){
        return String.valueOf(Math.round(d));
    }
    public static String formatDouble2(double d){
        DecimalFormat format = new DecimalFormat("0.0");
        return format.format(d);
    }
    public static int getIntFromString(String s){
        return Integer.parseInt(s.replaceAll("[\\D]", ""));
    }

    public static String setProdudtPrice(String price){
        if (isEmpty(price))
            return "0.0";
        return String.valueOf(new DecimalFormat("0.00").format(Double.parseDouble(price)));
        //formatDouble(Double.parseDouble(price));

    }

    public static String getDiscountValue(String percentage,String total){
        double per=Double.parseDouble(percentage);
        double price=Double.parseDouble(total);
        return formatDouble(price-per*price/100);
    }
    public static String getVatDiscount(String percentage,String total){
        double per=Double.parseDouble(percentage);
        double price=Double.parseDouble(total);
        return String.valueOf(price-per*price/100);
    }
    public static String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

}
