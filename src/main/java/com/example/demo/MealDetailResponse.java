package com.example.demo;
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
        private String strMeal;
        private String strInstructions;
        private String strMealThumb;

        public String getStrMeal() {
            return strMeal;
        }

        public void setStrMeal(String strMeal) {
            this.strMeal = strMeal;
        }

        public String getStrInstructions() {
            return strInstructions;
        }

        public void setStrInstructions(String strInstructions) {
            this.strInstructions = strInstructions;
        }

        public String getStrMealThumb() {
            return strMealThumb;
        }

        public void setStrMealThumb(String strMealThumb) {
            this.strMealThumb = strMealThumb;
        }
    }
}

