package com.redhat.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "counter")
@NamedQueries({
        @NamedQuery(name = "getRecordsThisMonth", query = "SELECT c.* FROM counter AS c WHERE MONTH(c.timestamp) = MONTH(NOW()) AND YEAR(c.timestamp) = YEAR(NOW())"),
        @NamedQuery(name = "getRecordsRange", query = "SELECT c.* FROM counter AS c WHERE c.timestamp >= NOW()-interval ?1 month")
})
public class Counter {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    @Column(name = "userEmail", nullable = false)
    private String userEmail;

    @Column(name = "message", nullable = false)
    private String message;

    public Counter() {
    }

    public Counter(String user, String msg) {
        this.userEmail = user;
        this.message = msg;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Counter counter = (Counter) o;
        return id.equals(counter.id) && timestamp.equals(counter.timestamp) && userEmail.equals(counter.userEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, userEmail);
    }

    @Override
    public String toString() {
        return "Counter{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", user='" + userEmail + '\'' +
                '}';
    }
}
