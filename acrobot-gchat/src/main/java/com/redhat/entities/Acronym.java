package com.redhat.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Acronym {

    public Long id;

    public String acronym;
    public Set<Explanation> explanations;

    public Acronym() {}

    public Acronym(String acronym) {
        this.acronym = acronym;
    }

    @Override
    public String toString() {
        return "Acronym{" +
                ", acronym='" + acronym + '\'' +
                ", explanations=" + explanations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Acronym acronym1 = (Acronym) o;
        return acronym.equals(acronym1.acronym) &&
          explanations.equals(acronym1.explanations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acronym, explanations);
    }
}
