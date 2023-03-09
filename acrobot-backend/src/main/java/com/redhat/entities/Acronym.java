package com.redhat.entities;

import javax.persistence.*;


import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "acronym")
public class Acronym extends PanacheEntity {
    public String acronym;

    @OneToMany(mappedBy = "acronym", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<Explanation> explanations;

    public Acronym() {}

    public Acronym(String acronym) {
        this.acronym = acronym;
    }

    @Override
    public String toString() {
        return "Acronym{" +
                "id=" + id +
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
