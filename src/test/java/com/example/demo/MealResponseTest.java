package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class MealResponseTest {

    @Test
    public void testMealResponseGettersAndSetters() {
        // Given
        MealResponse response = new MealResponse();
        List<MealResponse.Meal> meals = new ArrayList<>();
        MealResponse.Meal meal = new MealResponse.Meal();
        meal.setIdMeal("1");
        meal.setStrMeal("Spaghetti");
        meal.setStrMealThumb("https://example.com/spaghetti.jpg");
        meals.add(meal);

        // When
        response.setMeals(meals);

        // Then
        assertEquals(meals, response.getMeals());
        assertEquals(1, response.getMeals().size());
        assertEquals("Spaghetti", response.getMeals().get(0).getStrMeal());
        assertEquals("1", response.getMeals().get(0).getIdMeal());
        assertEquals("https://example.com/spaghetti.jpg", response.getMeals().get(0).getStrMealThumb());
    }

    @Test
    public void testMealClass() {
        // Given
        MealResponse.Meal meal = new MealResponse.Meal();

        // When
        meal.setIdMeal("2");
        meal.setStrMeal("Pizza");
        meal.setStrMealThumb("https://example.com/pizza.jpg");

        // Then
        assertEquals("2", meal.getIdMeal());
        assertEquals("Pizza", meal.getStrMeal());
        assertEquals("https://example.com/pizza.jpg", meal.getStrMealThumb());
    }
}
