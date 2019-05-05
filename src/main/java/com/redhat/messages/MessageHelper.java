package com.redhat.messages;

import com.redhat.entities.Acronym;
import com.redhat.entities.Explanation;
import com.redhat.persistence.AcronymDAL;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageHelper {

    private AcronymDAL acronymDal = new AcronymDAL();

    public String handleMessageAction(String message) {
        String resp;
        message = message.trim();

        if(message.startsWith("!")) {
            message = message.substring(1);
            String[] toSave = message.split("=");
            List<Acronym> list = acronymDal.getAcronymsByName(toSave[0]);
            if(list.isEmpty()) {
                saveAcronym(message);
                resp = "Thank you, I have saved your acronym.";
            }
            else {
                mergeAcronym(list.get(0),toSave[1]);
                resp = "Thank you, I have updated the acronym.";
            }

        } else if (message.equals("help")) {
            resp = "You are interacting with Acrobot. To get an acronym, simply tag me or DM me with the acronym. \n";
            resp += "To insert a new acronym, tag me or DM me with the format of '!acronym=explanation'";
        } else {
            resp = getAcronymAsString(message);
        }
        return resp;
    }

    private void mergeAcronym(Acronym acronym, String s) {
        Explanation e = new Explanation(s);
        e.setAcronym(acronym);
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

    private void saveAcronym(String message) {
        String[] toSave = message.split("=");
        Explanation e = new Explanation(toSave[1]);
        Acronym a = new Acronym(toSave[0]);
        e.setAcronym(a);
        Set<Explanation> set = new HashSet<>();
        set.add(e);
        a.setExplanations(set);
        acronymDal.saveAcronym(a);
    }
}
