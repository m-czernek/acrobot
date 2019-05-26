package com.redhat;

import com.redhat.entities.Acronym;
import com.redhat.entities.Explanation;
import com.redhat.persistence.AcronymExplanationDal;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.util.HashSet;
import java.util.Set;

public class DataAccessLayerTest {
    private AcronymExplanationDal dal = new AcronymExplanationDal();
    private static final String ACRONYM_LOWERCASE_NAME = "foo";
    private static final String ACRONYM_UPPERCASE_NAME = "FOO";
    private static final String UPDATE_ACRONYM = "baz";



    @ClassRule
    public static final EnvironmentVariables environmentVariables = new EnvironmentVariables()
            .set("JDBC_URL", "jdbc:h2:mem:test")
            .set("JDBC_USER","sa")
            .set("JDBC_PASSWORD","")
            .set("PU","test");

    @Test
    public void getNonExistentAcronymTest() {
        Assertions.assertThat(dal.getAcronymsByName("non-existent")).isEmpty();
    }

    @Test
    public void caseInsensitiveAcronymTest() {
        dal.saveAcronym(new Acronym(ACRONYM_LOWERCASE_NAME));
        Assertions.assertThat(dal.getAcronymsByName(ACRONYM_UPPERCASE_NAME).get(0)).isNotNull();
    }

    @Test
    public void updateTest() {
        Acronym acronym = new Acronym(UPDATE_ACRONYM);
        dal.saveAcronym(acronym);
        setAcronymExplanation(acronym);
        dal.updateAcronym(acronym);
        acronym = dal.getAcronymsByName(UPDATE_ACRONYM).get(0);
        Assertions.assertThat(acronym.getExplanations().size()).isEqualTo(1);
    }

    private void setAcronymExplanation(Acronym acronym) {
        Set<Explanation> explanationSet = new HashSet<>();
        Explanation e = new Explanation("baz");
        explanationSet.add(e);
        e.setAcronym(acronym);
        acronym.setExplanations(explanationSet);
    }

}
