package com.redhat.entities;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "acronym")
@NamedQueries(
        @NamedQuery(name = "findAcronymByName", query = "SELECT a FROM Acronym a WHERE a.acronym = ?1")
)
public class Acronym implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String acronym;

    @OneToMany(mappedBy = "acronym", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Explanation> explanations;

    public Acronym() {

    }

    public Acronym(String acronym) {
        this.acronym = acronym;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public Set<Explanation> getExplanations() {
        return explanations;
    }

    public void setExplanations(Set<Explanation> explanations) {
        this.explanations = explanations;
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
        return Objects.equals(acronym, acronym1.acronym);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acronym);
    }
}
