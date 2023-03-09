package com.redhat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Explanation {

    public Long id;

    public String explanation;
    @JsonIgnore
    public Acronym acronym;

    public boolean isChecked = false;
    public String authorEmail;

    public Explanation() {}

    public Explanation(String explanation, String authorEmail) {
        this.explanation = explanation;
        this.authorEmail = authorEmail;
    }

    @Override
    public String toString() {
        return "Explanation{" +
                ", explanation='" + explanation + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Explanation that = (Explanation) o;
        return explanation.equals(that.explanation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(explanation);
    }
}
