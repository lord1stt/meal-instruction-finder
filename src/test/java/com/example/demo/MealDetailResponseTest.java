package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MealDetailResponseTest {

    @Test
    public void testMealDetailResponseConstructors() {
        // Test default constructor
        MealDetailResponse response1 = new MealDetailResponse();
        assertNotNull(response1.getMeals());
        assertEquals(0, response1.getMeals().size());

        // Test constructor with meals
        List<MealDetailResponse.MealDetail> meals = new ArrayList<>();
        MealDetailResponse.MealDetail meal = new MealDetailResponse.MealDetail();
        meal.setStrMeal("Spaghetti");
        meals.add(meal);

        MealDetailResponse response2 = new MealDetailResponse(meals);
        assertEquals(meals, response2.getMeals());
        assertEquals(1, response2.getMeals().size());
        assertEquals("Spaghetti", response2.getMeals().get(0).getStrMeal());
    }

    @Test
    public void testMealDetailGettersAndSetters() {
        // Given
        MealDetailResponse.MealDetail mealDetail = new MealDetailResponse.MealDetail();

        // When
        mealDetail.setMealId(1);
        mealDetail.setStrMeal("Lasagna");
        mealDetail.setStrInstructions("How to cook Lasagna");
        mealDetail.setMealImageUrl("https://example.com/lasagna.jpg");
        mealDetail.setFavorite(true);
        String today = LocalDate.now().toString();

        // Then
        assertEquals(1, mealDetail.getMealId());
        assertEquals("Lasagna", mealDetail.getStrMeal());
        assertEquals("How to cook Lasagna", mealDetail.getStrInstructions());
        assertEquals("https://example.com/lasagna.jpg", mealDetail.getMealImageUrl());
        assertTrue(mealDetail.isFavorite());
        assertEquals(today, mealDetail.getAddFavDate());

        // Test setting favorite to false
        mealDetail.setFavorite(false);
        assertFalse(mealDetail.isFavorite());
        assertNull(mealDetail.getAddFavDate());

        // Test setting date directly
        String customDate = "2025-05-01";
        mealDetail.setAddFavDate(customDate);
        assertEquals(customDate, mealDetail.getAddFavDate());
    }
}