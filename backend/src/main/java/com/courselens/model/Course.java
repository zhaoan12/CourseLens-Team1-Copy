package com.courselens.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String code;

    @Column(length = 1024)
    private String description;

    private int currentEnrollment;
    private int capacity;

    @Convert(converter = HistoricalDataConverter.class)
    @Column(columnDefinition = "jsonb")
    private List<HistoricalDataPoint> historicalData;

    public Course() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCurrentEnrollment() {
        return currentEnrollment;
    }

    public void setCurrentEnrollment(int currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<HistoricalDataPoint> getHistoricalData() {
        return historicalData;
    }

    public void setHistoricalData(List<HistoricalDataPoint> historicalData) {
        this.historicalData = historicalData;
    }
}
