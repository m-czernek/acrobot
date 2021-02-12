package com.redhat;

import com.redhat.persistence.YamlDal;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class YmlLayerTest {
    private YamlDal dal = new YamlDal();
    // Corner cases
    // Acro is a number
    private static String numberAcro = "82576";
    private static String numberAcroExplanation = "Intel 82576 Gigabit Ethernet Controller";
    // Acro has starts with a lowercase char
    private static String lowerCaseAcro = "dNAT";
    private static String lowerCaseAcroExplanation = "Dynamic Network Address Translation @networking";
    // Acro contains special chars
    private static String specialCharAcro = "SMI-S";
    private static String specialCharAcroExplanation = "Storage Management Initiative Specification";

    @Test
    public void testCornerCases() {
        Assertions.assertThat(dal.getAcronymsByName(numberAcro))
                .hasSize(1)
                .filteredOn(acronym -> acronym.getExplanations().contains(numberAcroExplanation));
        Assertions.assertThat(dal.getAcronymsByName(lowerCaseAcro))
                .hasSize(1)
                .filteredOn(acronym -> acronym.getExplanations().contains(lowerCaseAcroExplanation));
        Assertions.assertThat(dal.getAcronymsByName(specialCharAcro))
                .hasSize(1)
                .filteredOn(acronym -> acronym.getExplanations().contains(specialCharAcroExplanation));
        Assertions.assertThat(dal.getAcronymsByName("NON_EXISTENT")).isEmpty();
    }
}
