package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SaveToJSONTest {

    @TempDir
    Path tempDir;

    @Test
    public void testSaveToJSON() throws IOException {
        // Given
        File tempFile = tempDir.resolve("test_meals.json").toFile();
        MealDetailResponse response = new MealDetailResponse();
        List<MealDetailResponse.MealDetail> meals = new ArrayList<>();
        MealDetailResponse.MealDetail meal = new MealDetailResponse.MealDetail();
        meal.setStrMeal("Pasta");
        meal.setStrInstructions("Boil water, add pasta");
        meal.setFavorite(true);
        meals.add(meal);
        response.setMeals(meals);

        // When
        SavetoJSON.save(response, tempFile.getAbsolutePath());

        // Then
        assertTrue(tempFile.exists());
        String content = new String(Files.readAllBytes(tempFile.toPath()));
        assertTrue(content.contains("Pasta"));
        assertTrue(content.contains("Boil water, add pasta"));
        assertTrue(content.contains("true"));
    }
}