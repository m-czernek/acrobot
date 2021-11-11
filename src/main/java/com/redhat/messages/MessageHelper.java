package com.redhat.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.constants.Constants;
import com.redhat.entities.Acronym;
import com.redhat.entities.Explanation;
import com.redhat.persistence.AcronymExplanationDal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageHelper {

    private AcronymExplanationDal acronymExplanationDal = new AcronymExplanationDal();

    public String handleMessageAction(JsonNode eventJson) {
        String resp;
        String message;
        String authorEmail = eventJson.get("user").get("email").asText();

        try {
            message = eventJson.get("message").get("argumentText").asText().trim();
        } catch (NullPointerException e) {
            // Acrobot was added via mention to a room, and has no argument text
            return Constants.ADDED_RESPONSE;
        }

        if(message.startsWith(Constants.SUDO_PASSWORD)) {
            return AdministrativeMessageHelper.handleAdminMessage(message);
        }

        if(message.startsWith("!")) {
            message = message.substring(1);

            // Validate message
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

        List<Acronym> acronyms = acronymExplanationDal.getAcronymsByName(acronymExplanationsArray[0]);
        if(acronyms.isEmpty()) {
            return Constants.ACRONYM_NOT_FOUND;
        }

        String[] oldNewExplanation = acronymExplanationsArray[1].split("=>");
        trimArray(oldNewExplanation);

        Acronym a = acronyms.get(0);
        Explanation e = a.getExplanations()
                .stream()
                .filter(explanation -> explanation.getExplanation().equals(oldNewExplanation[0]))
                .findFirst()
                .orElse(null);

        if(e == null) {
            return Constants.EXPLANATION_NOT_FOUND;
        }

        if(oldNewExplanation.length == 1) {
            a.getExplanations().remove(e);
            resp = acronymExplanationDal.deleteExplanation(e);
        } else {
            e.setExplanation(oldNewExplanation[1]);
            resp = acronymExplanationDal.updateAcronym(a);
        }
        return resp;
    }

    private String saveOrMergeAcronym(String message, String authorEmail) {
        String resp;
        String[] toSave = splitMessageToSaveAndTrim(message);
        List<Acronym> list = acronymExplanationDal.getAcronymsByName(toSave[0]);
        if(list.isEmpty()) {
            resp = saveAcronym(toSave, authorEmail);
        }
        else {
            resp = mergeAcronym(list.get(0), toSave[1], authorEmail);
        }
        return resp;
    }

    private boolean isMessageValid(String message) {
        return message.contains("=") && (!message.trim().endsWith("="));
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

    private String mergeAcronym(Acronym acronym, String explanation, String authorEmail) {
        Explanation e = new Explanation(explanation);
        e.setAcronym(acronym);
        e.setAuthorEmail(authorEmail);
        acronym.getExplanations().add(e);
        return acronymExplanationDal.updateAcronym(acronym);
    }

    private String getAcronymAsString(String message) {
        String resp = "";
        for(Acronym a : acronymExplanationDal.getAcronymsByName(message)) {
            for(Explanation e : a.getExplanations()) {
                resp += e.getExplanation() + "\n";
            }
        }
        if(resp.isEmpty()) {
            resp = Constants.ACRONYM_NOT_FOUND;
        }
        return resp;
    }

    private String saveAcronym(String[] toSave, String authorEmail) {
        Explanation e = new Explanation(toSave[1]);
        e.setAuthorEmail(authorEmail);
        Acronym a = new Acronym(toSave[0]);
        e.setAcronym(a);
        Set<Explanation> set = new HashSet<>();
        set.add(e);
        a.setExplanations(set);
        return acronymExplanationDal.saveAcronym(a);
    }
}
