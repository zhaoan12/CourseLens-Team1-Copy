package com.courselens.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.List;

@Converter
public class HistoricalDataConverter implements AttributeConverter<List<HistoricalDataPoint>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public String convertToDatabaseColumn(List<HistoricalDataPoint> historicalData) {
        if (historicalData == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(historicalData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not convert historical data to JSON", e);
        }
    }

    @Override
    public List<HistoricalDataPoint> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<HistoricalDataPoint>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Could not convert JSON to historical data", e);
        }
    }
}
