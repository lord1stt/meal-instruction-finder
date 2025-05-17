package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CategoryTest {

    @Test
    public void testCategoryGettersAndSetters() {
        // Given
        Category category = new Category();
        String idCategory = "1";
        String strCategory = "Seafood";
        String strCategoryThumb = "https://example.com/seafood.jpg";
        String strCategoryDescription = "Fish and other seafood";

        // When
        category.setIdCategory(idCategory);
        category.setStrCategory(strCategory);
        category.setStrCategoryThumb(strCategoryThumb);
        category.setStrCategoryDescription(strCategoryDescription);

        // Then
        assertEquals(idCategory, category.getIdCategory());
        assertEquals(strCategory, category.getStrCategory());
        assertEquals(strCategoryThumb, category.getStrCategoryThumb());
        assertEquals(strCategoryDescription, category.getStrCategoryDescription());
    }
}