package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryResponseTest {

    @Test
    public void testCategoryResponseGettersAndSetters() {
        // Given
        CategoryResponse response = new CategoryResponse();
        List<Category> categories = new ArrayList<>();
        Category category = new Category();
        category.setIdCategory("1");
        category.setStrCategory("Breakfast");
        categories.add(category);

        // When
        response.setCategories(categories);

        // Then
        assertEquals(categories, response.getCategories());
        assertEquals(1, response.getCategories().size());
        assertEquals("Breakfast", response.getCategories().get(0).getStrCategory());
    }
}
