package com.example.recipeapp;

import com.fasterxml.jackson.databind.ObjectMapper; //serialize/deserialize işlemleri için. JSON ile nesneler arası dönüşüm için
import com.fasterxml.jackson.databind.SerializationFeature; //Tarihleri sayıya çevirmek yerine ISO 8601 biçiminde (yyyy-MM-dd) yazmamızı sağlar.
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Java 8’in LocalDate gibi tarih-zaman sınıflarının Jackson ile uyumlu şekilde çalışmasını sağlar.

import java.io.File; //Dosya işlemleri için
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors; // Java Stream API'sindeki verileri toplamak için kullanılır (örneğin filtrelenmiş veriyi listeye çevirmek).

public class GetFavorites {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) //LocalDate gibi tarih tiplerinin düzgün işlendiğinden emin olur.
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); //Tarihlerin sayı olarak değil, düzgün formatla ("2025-05-09" gibi) yazılmasını sağlar.

    private static final String INPUT_FILE_PATH = "meals.json";
    public static List<MealDetailResponse.MealDetail> getFavorites() throws IOException {
        try {
            MealDetailResponse allMeals = mapper.readValue(new File(INPUT_FILE_PATH), MealDetailResponse.class); // meals.json dosyasından tüm yemekleri oku
        // Liste olarak geri dön
        return allMeals.getMeals().stream() //getMeals() ile yemeklerin listesi alınır. stream() ile bu liste üzerinde işlem başlatılır.
                .filter(MealDetailResponse.MealDetail::isFavorite) //isFavorite() true olanlar seçilir.
                .collect(Collectors.toList()); //Filtrelenmiş sonuçları yeni bir listeye toplar.
        } catch (IOException e) {
            return List.of(); // Eğer dosya oluşturulmamışsa boş liste döner
        }
    }
}
