package com.jose.castsocialconnector.instagram;

import com.jose.castsocialconnector.main.MainActivity;
import com.jose.castsocialconnector.xml.XmlContact;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dmunoz on 15-09-15.
 *
 */
public class InstagramUtils {

    public static String processCaptionText(String text) {
        String result = text;
        Matcher matcher = Pattern.compile("#\\s*(\\w+)").matcher(text);
        ArrayList<Integer[]> toDelete = new ArrayList<>();
        while (matcher.find()) {
            Integer[] tmpArray = new Integer[]{matcher.start(), matcher.end()};
            toDelete.add(0, tmpArray);
        }
        for (Integer[] tmpArray: toDelete) {
            if (tmpArray[0] > 0
                    && result.substring(tmpArray[0] - 1, tmpArray[0]).compareTo(" ") == 0)
                tmpArray[0]--;
            if (tmpArray[0] == 0 && (tmpArray[1] + 1) < result.length())
                tmpArray[1]++;
            result = result.substring(0, tmpArray[0]) + result.substring(tmpArray[1]);
        }
        return result;
    }

    public static String searchUserEmail(String username) {
        String result = "";
        for (XmlContact contact: MainActivity.xmlContacts) {
            if (username.compareTo(contact.getInstagram()) == 0) {
                result = contact.getEmail();
            }
        }
        return result;
    }

    public static String searchUserNickname(String username) {
        String result = "";
        for (XmlContact contact: MainActivity.xmlContacts) {
            if (username.compareTo(contact.getInstagram()) == 0) {
                result = contact.getNickname();
            }
        }
        return result;
    }
}
