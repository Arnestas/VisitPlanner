package com.VisitPlanner.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table (name = "visits")
public class Visit {

    @Id
    @SequenceGenerator(
            name = "id",
            sequenceName = "id",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "id")
    private Integer id;
    @Column(name = "number")
    private String visitNumber;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "reservation_time")
    private String reservationTime;
    @Column(name = "reserved_time")
    private String reservedTime;

    private Status status;

    private String statusChangeDate;

    public Visit() {
    }

    public Visit(Integer id, String visitNumber, User user, String reservationTime, String reservedTime, Status status, String statusChangeDate) {
        this.id = id;
        this.visitNumber = visitNumber;
        this.user = user;
        this.reservationTime = reservationTime;
        this.reservedTime = reservedTime;
        this.status = status;
        this.statusChangeDate = statusChangeDate;
    }

    public enum Status{
        Waiting,
        Started,
        Finished,
        Canceled
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(String visitNumber) {
        this.visitNumber = visitNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getReservedTime() {
        return reservedTime;
    }

    public void setReservedTime(String reservedTime) {
        this.reservedTime = reservedTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStatusChangeDate() {
        return statusChangeDate;
    }

    public void setStatusChangeDate(String statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }

    @Override
    public String toString() {
        return "Visit{" +
                "id=" + id +
                ", visitNumber='" + visitNumber + '\'' +
                ", specialist=" + user +
                ", reservationTime=" + reservationTime +
                ", reservedTime=" + reservedTime +
                '}';
    }
}
