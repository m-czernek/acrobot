package com.redhat.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.constants.Constants;
import com.redhat.constants.MessageType;

public class MessageTypeHelper {

    public MessageType determineMessageAction(JsonNode eventJson) {
        String message;
        try {
            message = eventJson.get("message").get("argumentText").asText().trim();
        } catch (NullPointerException e) {
            // Acrobot was added via mention to a room, and has no argument text
            return MessageType.ADDED_TO_ROOM;
        }

        if(message.startsWith(Constants.SUDO_PASSWORD)) {
            return MessageType.SUDO_RESPONSE;
        }

        if(message.startsWith("!")) {
            message = message.substring(1);

            // Validate message
            if (!isMessageValid(message)) {
                return MessageType.INVALID_MESSAGE;
            }

            if(message.contains("=>")) {
                return MessageType.UPDATE_OR_REMOVE;
            } else {
                return MessageType.SAVE_OR_MERGE;
            }
        }

        if (message.equals("help")) {
            return  MessageType.HELP;
        } else {
            return MessageType.GET_ACRONYM;
        }
    }

    private boolean isMessageValid(String message) {
        return message.contains("=") && (!message.trim().endsWith("="));
    }
}
