package com.redhat.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.constants.Constants;
import com.redhat.entities.Acronym;
import com.redhat.entities.Explanation;
import com.redhat.persistence.AcronymDAL;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageHelper {

    private AcronymDAL acronymDal = new AcronymDAL();

    public String handleMessageAction(JsonNode eventJson) {
        String resp;
        String message = eventJson.get("message").get("argumentText").asText().trim();
        String authorEmail = eventJson.get("user").get("email").asText();

        if(message.startsWith(Constants.SUDO_PASSWORD)) {
            return AdministrativeMessageHelper.handleAdminMessage(message);
        }

        if(message.startsWith("!")) {
            message = message.substring(1);

            // Validate message
            if(!message.contains("=")) {
                return Constants.INCORRECT_FORMAT_FOR_SAVING_ACRONYM;
            }

            String[] toSave = splitMessageToSaveAndTrim(message);
            List<Acronym> list = acronymDal.getAcronymsByName(toSave[0]);
            if(list.isEmpty()) {
                saveAcronym(toSave, authorEmail);
                resp = Constants.ACRONYM_SAVED;
            }
            else {
                mergeAcronym(list.get(0), toSave[1], authorEmail);
                resp = Constants.ACRONYM_UPDATED;
            }

        } else if (message.equals("help")) {
            resp = Constants.HELP_TEXT;
        } else {
            resp = getAcronymAsString(message);
        }
        return resp;
    }

    private String[] splitMessageToSaveAndTrim(String message) {
        String[] res = message.split("=", 2);
        res[0] = res[0].trim();
        res[1] = res[1].trim();
        return res;
    }

    private void mergeAcronym(Acronym acronym, String explanation, String authorEmail) {
        Explanation e = new Explanation(explanation);
        e.setAcronym(acronym);
        e.setAuthorEmail(authorEmail);
        acronym.getExplanations().add(e);
        acronymDal.updateAcronym(acronym);
    }

    private String getAcronymAsString(String message) {
        String resp = "";
        for(Acronym a : acronymDal.getAcronymsByName(message)) {
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
        acronymDal.saveAcronym(a);
    }
}
