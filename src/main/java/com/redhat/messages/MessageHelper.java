package com.redhat.messages;

import com.fasterxml.jackson.databind.JsonNode;
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
        String message = eventJson.get("message").get("text").asText().trim();
        String authorEmail = eventJson.get("user").get("email").asText();

        message = removeMentionFromMessage(message);
        if(message.startsWith("!")) {
            message = message.substring(1);

            // Validate message
            if(!message.contains("=")) {
                return "Please enter the acronym in format of !$acronym=$explanation to save an explanation," +
                        ", or only $acronym to get an explanation";
            }

            String[] toSave = splitMessageToSaveAndTrim(message);
            List<Acronym> list = acronymDal.getAcronymsByName(toSave[0]);
            if(list.isEmpty()) {
                saveAcronym(toSave, authorEmail);
                resp = "Thank you, I have saved your acronym.";
            }
            else {
                mergeAcronym(list.get(0), toSave[1], authorEmail);
                resp = "Thank you, I have updated the acronym.";
            }

        } else if (message.equals("help")) {
            resp = "You are interacting with Acrobot. To get an acronym, simply tag me or DM me with the acronym. \n";
            resp += "To insert a new acronym, tag me or DM me with the format of '!acronym=explanation'. \n";
            resp += "Acrobot is the original idea of Pavel Tisnovsky, and is available on IRC. \n" +
                    "Marek Czernek has implemented me, the Google Chat Acrobot.";
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

    private String removeMentionFromMessage(String message) {
        // Whatever the name of the bot is, the message might start with @name, e.g. @acrobot $msg
        // because we care only about $msg, we split the string on the space that comes after the @mention
        if(message.startsWith("@")) {
            String[] messageArray = message.split(" ", 2);
            if(messageArray.length == 2) {
                return messageArray[1].trim();
            }
        }
        return message.trim();
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
