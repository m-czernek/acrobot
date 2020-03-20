package com.redhat.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.constants.Constants;
import com.redhat.constants.MessageType;

public class MessageTypeHelper {

    public static MessageType determineMessageAction(JsonNode eventJson) {
        final JsonNode msgNode = eventJson.get("message").get("argumentText");

        if(msgNode == null) {
            // Acrobot was added via mention to a room, and has no argument text
            return MessageType.ADDED_TO_ROOM;
        }

        String message = msgNode.asText();

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

    private static boolean isMessageValid(String message) {
        return message.contains("=") && (!message.trim().endsWith("="));
    }
}
