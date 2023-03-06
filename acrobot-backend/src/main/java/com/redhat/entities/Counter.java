package com.redhat.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "counter")
public class Counter extends PanacheEntity {
    @Column(name = "timestamp", nullable = false)
    public Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    @Column(name = "userEmail", nullable = false)
    public String userEmail;

    @Column(name = "message", nullable = false)
    public String message;

    public Counter() {}

    public Counter(String user, String msg) {
        this.userEmail = user;
        this.message = msg;
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
