package com.redhat.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.entities.Acronym;
import com.redhat.entities.Explanation;
import com.redhat.persistence.YamlDal;

import java.util.Set;

public class YamlAcrobotInjector {
    private YamlDal dal = new YamlDal();
    String res = "\n~~~ Found acronym in YAML read-only database~~~\n";

    public String injectYamlAcronyms(JsonNode eventJson, String originalMessage) {
        String requestedAcronym = eventJson.get("message").get("argumentText").asText().trim();
        Set<Acronym> yamlAcronyms = dal.getAcronymsByName(requestedAcronym);

        if(yamlAcronyms.isEmpty()) {
            return originalMessage;
        }


        for(Acronym a : yamlAcronyms) {
            for(Explanation e : a.getExplanations()) {
                res += e.getExplanation() + "\n";
            }
        }

        return originalMessage + res;
    }
}
