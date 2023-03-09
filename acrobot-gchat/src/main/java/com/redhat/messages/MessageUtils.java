package com.redhat.messages;

public class MessageUtils {
    public String[] splitMessageToSaveAndTrim(String message) {
        return trimArray(message.split("=", 2));
    }

    public String[] trimArray(String[] array) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i].trim();
        }
        return array;
    }

    public boolean isMessageValid(String message) {
        return message.contains("=") && (!message.trim().endsWith("="));
    }
}
