package com.redhat.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "explanation")
public class Explanation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String explanation;

    @ManyToOne
    @JoinColumn(name="acronym_id")
    private Acronym acronym;

    // A flag for manual checks of explanations
    // isChecked = false means the explanation requires manual review
    @Column(nullable = false, name = "is_checked", columnDefinition = "bit default 0")
    private boolean isChecked = false;

    private String authorEmail;

    public Explanation() {
    }

    public Explanation(String explanation) {
        this.explanation = explanation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Acronym getAcronym() {
        return acronym;
    }

    public void setAcronym(Acronym acronym) {
        this.acronym = acronym;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    @Override
    public String toString() {
        return "Explanation{" +
                "id=" + id +
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
