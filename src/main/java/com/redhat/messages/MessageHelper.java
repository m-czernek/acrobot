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
                resp = updateAcronymOrRemoveExplanation(message, authorEmail);
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

    private String updateAcronymOrRemoveExplanation(String message, String authorEmail) {
        String resp;
        String[] acronymExplanationsArray = splitMessageToSaveAndTrim(message);

        List<Acronym> acronyms = acronymExplanationDal.getAcronymsByName(acronymExplanationsArray[0]);
        if(acronyms.isEmpty()) {
            return "No such acronym found.";
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
            return "no such explanation";
        }

        if(!e.getAuthorEmail().equals(authorEmail)) {
            return "Insufficient privileges";
        }

        if(oldNewExplanation.length == 1) {
            a.getExplanations().remove(e);
            acronymExplanationDal.deleteExplanation(e);
            resp = "Removed explanation";
        } else {
            e.setExplanation(oldNewExplanation[1]);
            resp = "Updated explanation";
        }
        acronymExplanationDal.updateAcronym(a);
        return resp;
    }

    private String saveOrMergeAcronym(String message, String authorEmail) {
        String resp;
        String[] toSave = splitMessageToSaveAndTrim(message);
        List<Acronym> list = acronymExplanationDal.getAcronymsByName(toSave[0]);
        if(list.isEmpty()) {
            saveAcronym(toSave, authorEmail);
            resp = Constants.ACRONYM_SAVED;
        }
        else {
            mergeAcronym(list.get(0), toSave[1], authorEmail);
            resp = Constants.ACRONYM_UPDATED;
        }
        return resp;
    }

    private boolean isMessageValid(String message) {
        return message.contains("=") && (!message.trim().endsWith("="));
    }

    private String[] splitMessageToSaveAndTrim(String message) {
        String[] res = message.split("=", 2);
        trimArray(res);
        return res;
    }

    private void trimArray(String[] array) {
        for(int i = 0; i < array.length; i++) {
            array[i] = array[i].trim();
        }
    }

    private void mergeAcronym(Acronym acronym, String explanation, String authorEmail) {
        Explanation e = new Explanation(explanation);
        e.setAcronym(acronym);
        e.setAuthorEmail(authorEmail);
        acronym.getExplanations().add(e);
        acronymExplanationDal.updateAcronym(acronym);
    }

    private String getAcronymAsString(String message) {
        String resp = "";
        for(Acronym a : acronymExplanationDal.getAcronymsByName(message)) {
            for(Explanation e : a.getExplanations()) {
                resp += e.getExplanation() + "\n";
            }
        }
        if(resp.isEmpty()) {
            resp = "No acronym " + message + " found.";
        }
        return resp;
    }

    private void saveAcronym(String[] toSave, String authorEmail) {
        Explanation e = new Explanation(toSave[1]);
        e.setAuthorEmail(authorEmail);
        Acronym a = new Acronym(toSave[0]);
        e.setAcronym(a);
        Set<Explanation> set = new HashSet<>();
        set.add(e);
        a.setExplanations(set);
        acronymExplanationDal.saveAcronym(a);
    }
}
