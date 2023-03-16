package com.redhat.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.constants.Constants;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class MessageController {

    @Inject
    AcrobotResource acrobotResource;

    public String handleMessageAction(JsonNode eventJson) {
        if (eventJson.get("message").get("argumentText") == null) {
            // Acrobot was added via mention to a room, and has no argument text
            return Constants.ADDED_RESPONSE;
        }

        String message = eventJson.get("message").get("argumentText").asText().trim();
        String authorEmail = eventJson.get("user").get("email").asText();
        MessageUtils utils = new MessageUtils();

        if(utils.isMessageTooLong(message)) {
            return Constants.MESSAGE_TOO_LONG;
        }

        if (message.equalsIgnoreCase("help")) {
            return Constants.HELP_TEXT;
        }

        if (!message.startsWith("!")) {
            return acrobotResource.getAcronymAsString(message);
        }

        // remove the "!" char
        message = message.substring(1);
        if (!utils.isMessageValid(message)) {
            return Constants.INCORRECT_FORMAT_FOR_SAVING_ACRONYM;
        }

        if (message.contains("=>")) {
            return acrobotResource.updateExplanations(message, authorEmail);
        } else {
            return acrobotResource.saveOrMergeAcronym(message, authorEmail);
        }
    }
}
