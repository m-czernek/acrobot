package com.redhat.messages;

import com.redhat.persistence.PersistenceManager;

public class AdministrativeMessageHelper {

    public static String handleAdminMessage(String msg) {
        if(msg.toLowerCase().contains("disconnect")) {
            PersistenceManager.INSTANCE.close();
            return "Disconnected from database";
        }

        if(msg.toLowerCase().contains("connect")) {
            PersistenceManager.INSTANCE.init();
            return "Connected to database";
        }

        return "Unknown command. Valid actions: disconnect, connect.";
    }
}
