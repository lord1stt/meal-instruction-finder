package com.example.demo;
import java.time.LocalDate;
import java.util.List;

public class MealDetailResponse {
    private List<MealDetail> meals;

    public List<MealDetail> getMeals() {
        return meals;
    }

    public void setMeals(List<MealDetail> meals) {
        this.meals = meals;
    }

    public static class MealDetail {
        private int mealId;
        private String mealNameStr;
        private String mealInstructions;
        private String mealImageUrl;
        private boolean isFavorite;
        private String addFavDate;

        public int getMealId() {
            return mealId;
        }

        public void setMealId(int mealId) {
            this.mealId = mealId;
        }

        public String getStrMeal() {
            return mealNameStr;
        }

        public void setStrMeal(String mealNameStr) {
            this.mealNameStr = mealNameStr;
        }

        public String getStrInstructions() {
            return mealInstructions;
        }

        public void setStrInstructions(String mealInstructions) {
            this.mealInstructions = mealInstructions;
        }

        public String getMealImageUrl() {
            return mealImageUrl;
        }

        public void setMealImageUrl(String mealImageUrl) {
            this.mealImageUrl = mealImageUrl;
        }

        public boolean isFavorite() {
            return isFavorite;
        }

        public void setFavorite(boolean favorite) {
            isFavorite = favorite;
            if (favorite) {
                this.addFavDate = LocalDate.now().toString();
            } else {
                this.addFavDate = null;
            }
        }

        public String getAddFavDate() {
            return addFavDate;
        }

        public void setAddFavDate(String addFavDate) {
            this.addFavDate = addFavDate;
        }
    }
}