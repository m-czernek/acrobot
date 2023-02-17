package com.redhat.messages;

import com.redhat.persistence.CounterDal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdministrativeMessageHelper {

    private static final Pattern NUM_PATTERN = Pattern.compile("\\d+");

    public static String handleAdminMessage(String msg) {
        CounterDal dal = new CounterDal();
        msg = msg.toLowerCase();

        if(msg.contains("this month")) {
            return String.format("There were *%d* records this month", dal.getNumMessagesThisMonth());
        }

        if(msg.contains("range")) {
            Matcher m = NUM_PATTERN.matcher(msg);
            if(!m.find()) {
                return "Number not found; provide month in a numeric format";
            }
            return String.format("There were *%d* records in the selected range.",
                    dal.getNumMessagesBetweenRange(Integer.parseInt(m.group(0))));
        }

        return "Unknown command.";
    }
}
