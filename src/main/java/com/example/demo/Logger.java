package com.example.demo;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String LOG_FILE_PATH = "log.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(String message) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String logEntry = "[" + timestamp + "] " + message;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            bw.write(logEntry);
            bw.newLine();
            System.out.println("Log kaydedildi: " + message);
        } catch (IOException e) {
            System.err.println("Log dosyasına yazma hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void logCategorySelected(String categoryName) {
        log("Kategori seçildi: " + categoryName);
    }
    public static void logMealSelected(String mealName) {
        log("Yemek seçildi: " + mealName);
    }
    public static void logRecipeRetrieved(String mealName) {
        log("Tarif getirildi: " + mealName);
    }
    public static void logRecipeTranslated(String mealName, String sourceLang, String targetLang) {
        log("Tarif çevirildi: " + mealName + " (" + sourceLang + " -> " + targetLang + ")");
    }
    public static void logMealAddedToFavorites(String mealName) {
        log("Favorilere eklendi: " + mealName);
    }
    public static void logMealRemovedFromFavorites(String mealName) {
        log("Favorilerden çıkarıldı: " + mealName);
    }
    public static void logPdfCreated(String filePath) {
        log("PDF oluşturuldu: " + filePath);
    }
    public static void logEmailSent(String recipient) {
        log("E-posta gönderildi: " + recipient);
    }
    public static void logError(String errorMessage) {
        log("HATA: " + errorMessage);
    }
}
