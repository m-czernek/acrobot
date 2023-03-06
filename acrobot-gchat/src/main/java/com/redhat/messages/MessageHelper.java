package com.redhat.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.client.AcrobotBE;
import com.redhat.constants.Constants;
import com.redhat.entities.Acronym;
import com.redhat.entities.Explanation;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;

public class MessageHelper {

    @Inject
    @RestClient
    AcrobotBE acrobotBE;

    public String handleMessageAction(JsonNode eventJson) {
        if(eventJson.get("message").get("argumentText") == null) {
            // Acrobot was added via mention to a room, and has no argument text
            return Constants.ADDED_RESPONSE;
        }

        String message = eventJson.get("message").get("argumentText").asText().trim();
        String authorEmail = eventJson.get("user").get("email").asText();
        String resp;

        if(message.startsWith("!")) {
            message = message.substring(1);

            if (!isMessageValid(message)) {
                return Constants.INCORRECT_FORMAT_FOR_SAVING_ACRONYM;
            }

            if(message.contains("=>")) {
                resp = updateOrRemoveExplanation(message, authorEmail);
            } else {
                resp = saveOrMergeAcronym(message, authorEmail);
            }
        } else if (message.equals("help")) {
            resp = Constants.HELP_TEXT;
        } else {
            resp = getAcronymAsString(message);
        }
        return resp;
    }

    private String updateOrRemoveExplanation(String message, String authorEmail) {
        String resp;
        String[] acronymExplanationsArray = splitMessageToSaveAndTrim(message);

        List<Acronym> acronyms = acrobotBE.getByName(acronymExplanationsArray[0]);
        if(acronyms.isEmpty()) {
            return Constants.ACRONYM_NOT_FOUND;
        }

        String[] oldNewExplanation = acronymExplanationsArray[1].split("=>");
        trimArray(oldNewExplanation);

        Acronym a = acronyms.get(0);
        Explanation e = a.explanations
                .stream()
                .filter(explanation -> explanation.explanation.equals(oldNewExplanation[0]))
                .findFirst()
                .orElse(null);

        if(e == null) {
            return Constants.EXPLANATION_NOT_FOUND;
        }

        if(!e.authorEmail.equals(authorEmail)) {
            return Constants.INSUFFICIENT_PRIVILEGES;
        }

        if(oldNewExplanation.length == 1) {
            // Delete explanation
            a.explanations.remove(e);
            acrobotBE.update(a);
            resp = Constants.EXPLANATION_REMOVED;
        } else {
            // Update explanation
            e.explanation = oldNewExplanation[1];
            acrobotBE.update(a);
            resp = Constants.ACRONYM_UPDATED;
        }
        return resp;
    }

    private String saveOrMergeAcronym(String message, String authorEmail) {
        String resp;
        String[] toSave = splitMessageToSaveAndTrim(message);

        List<Acronym> list = acrobotBE.getByName(toSave[0]);
        if(list.isEmpty()) {
            resp = saveAcronym(toSave, authorEmail);
        }
        else {
            resp = mergeAcronymExplanations(list.get(0), toSave[1], authorEmail);
        }
        return resp;
    }

    private String getAcronymAsString(String message) {
        String resp = "";
        for(Acronym a : acrobotBE.getByName(message)) {
            for(Explanation e : a.explanations) {
                resp += e.explanation + "\n";
            }
        }
        if(resp.isEmpty()) {
            resp = Constants.ACRONYM_NOT_FOUND;
        }
        return resp;
    }

    private String saveAcronym(String[] toSave, String authorEmail) {
        Explanation e = new Explanation(toSave[1], authorEmail);
        Acronym a = new Acronym(toSave[0]);
        e.acronym = a;
        a.explanations = new HashSet<>(List.of(e));
        acrobotBE.save(a);
        return Constants.ACRONYM_SAVED;
    }

    private String mergeAcronymExplanations(Acronym acronym, String explanation, String authorEmail) {
        Explanation e = new Explanation(explanation, authorEmail);
        e.acronym = acronym;
        acronym.explanations.add(e);
        acrobotBE.update(acronym);
        return Constants.ACRONYM_UPDATED;
    }

    private String[] splitMessageToSaveAndTrim(String message) {
        return trimArray(message.split("=", 2));
    }

    private String[] trimArray(String[] array) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i].trim();
        }
        return array;
    }

    private boolean isMessageValid(String message) {
        return message.contains("=") && (!message.trim().endsWith("="));
    }
}
