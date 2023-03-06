package com.redhat.entities;

import java.util.Objects;
import java.util.Set;

public class Acronym {

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
        return Objects.equals(acronym, acronym1.acronym);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acronym);
    }
}
