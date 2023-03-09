package com.redhat.messages;

import com.redhat.client.AcrobotBE;
import com.redhat.constants.Constants;
import com.redhat.entities.Acronym;
import com.redhat.entities.Explanation;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.net.URI;
import java.util.HashSet;
import java.util.List;

public class AcrobotResource {
    public AcrobotBE acrobotBE;
    MessageUtils utils;

    public AcrobotResource() {
        this.acrobotBE = RestClientBuilder.newBuilder()
          .baseUri(URI.create("http://localhost:8080/"))
          .build(AcrobotBE.class);

        this.utils = new MessageUtils();
    }

    public String updateExplanations(String message, String authorEmail) {
        String[] acronymExplanationsArray = utils.splitMessageToSaveAndTrim(message);

        List<Acronym> acronyms = acrobotBE.getByName(acronymExplanationsArray[0]);
        if(acronyms.isEmpty()) {
            return Constants.ACRONYM_NOT_FOUND;
        }

        String[] oldNewExplanation = acronymExplanationsArray[1].split("=>");
        utils.trimArray(oldNewExplanation);

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
        } else {
            // Update explanation
            e.explanation = oldNewExplanation[1];
        }
        acrobotBE.update(a);
        return Constants.ACRONYM_UPDATED;
    }

    public String saveOrMergeAcronym(String message, String authorEmail) {
        String resp;
        // toSave = {acronym, explanation}
        String[] toSave = utils.splitMessageToSaveAndTrim(message);

        List<Acronym> list = acrobotBE.getByName(toSave[0]);
        if(list.isEmpty()) {
            resp = saveAcronym(toSave, authorEmail);
        }
        else {
            resp = mergeAcronymExplanations(list.get(0), toSave[1], authorEmail);
        }
        return resp;
    }

    public String getAcronymAsString(String acronym) {
        String resp = "";
        for(Acronym a : acrobotBE.getByName(acronym)) {
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
}
