package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.ArrayList;

import java.io.File;
import java.io.IOException;

public class LoadFromJSON {
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String INPUT_FILE_PATH = "meals.json";

    public static MealDetailResponse load() throws IOException {
        try {
            return mapper.readValue(new File(INPUT_FILE_PATH), MealDetailResponse.class);
        } catch (IOException e) {
            return new MealDetailResponse(new ArrayList<>()); // Boş liste döndür
        }
    }
}
