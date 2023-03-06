package com.redhat.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class Explanation {
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
        return Objects.equals(explanation, that.explanation) &&
                Objects.equals(acronym, that.acronym);
    }

    @Override
    public int hashCode() {
        return Objects.hash(explanation, acronym);
    }
}
