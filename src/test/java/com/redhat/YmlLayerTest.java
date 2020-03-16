package com.redhat;

import com.redhat.persistence.YamlDal;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class YmlLayerTest {

    YamlDal dal = new YamlDal();
    // Corner cases
    // Acro is a number
    String numberAcro = "82576";
    // Acro has starts with a lowercase char
    String lowerCaseAcro = "dNAT";
    // Acro contains special chars
    String specialCharAcro = "SMI-S";

    @Test
    public void testCornerCases() {
        Assertions.assertThat(dal.getAcronymsByName(numberAcro)).hasSize(1);
        Assertions.assertThat(dal.getAcronymsByName(lowerCaseAcro)).hasSize(1);
        Assertions.assertThat(dal.getAcronymsByName(specialCharAcro)).hasSize(1);
        Assertions.assertThat(dal.getAcronymsByName("NON_EXISTENT")).isEmpty();
    }
}
