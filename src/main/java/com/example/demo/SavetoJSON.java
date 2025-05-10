package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;

public class SavetoJSON {
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    public static void save(MealDetailResponse data, String filePath) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), data);
    }
}
