package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.CommandLineRunner;
import org.w3c.dom.css.RGBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class DemoApplication {
	public static void GUI(){
		//		SpringApplication.run(DemoApplication.class, args);
		// Creating instance of JFrame

	}
	public static void main(String[] args) {
//		GUI();
		RestTemplate restTemplate = new RestTemplate();


		String url = "https://www.themealdb.com/api/json/v1/1/categories.php";


		CategoryResponse categoryResponse = restTemplate.getForObject(url, CategoryResponse.class);
		List<Category> categories = categoryResponse.getCategories();
		// GUI BAŞLANGIÇ
		JFrame frame = new JFrame();
		frame.setSize(900, 500);
		JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
//		row3.setBackground(Color.gray);
		frame.setLocation(400,200);

		JComboBox<String> cBoxCategories = new JComboBox<>();
		int i = 0;
		for(Category c : categories){
			cBoxCategories.addItem(c.getStrCategory());
		}

		JButton getMealsButton = new JButton("Yemekleri getir.");
		JTextField newTextField = new JTextField(20);
		JLabel label1 = new JLabel("bir kategori seçin:  ");
		JLabel label2 = new JLabel("bir yemek seçin:  ");
		label1.setFont(new Font("Verdana", Font.PLAIN, 18));
		label2.setFont(new Font("Verdana", Font.PLAIN, 18));
		JComboBox<String> cBoxMeals = new JComboBox<>();
		JButton getMealDetailButton = new JButton("Yemek tarifini getir.");
		// JTextPane for meal recipe
		JTextPane tarifTextPane = new JTextPane();
		tarifTextPane.setEditable(false); // Only for displaying
		tarifTextPane.setPreferredSize(new Dimension(750,400)); // Boyutu ayarladık
		tarifTextPane.setFont(new Font("Verdana", Font.BOLD, 15));
		tarifTextPane.setBackground(Color.lightGray);
		JScrollPane tarifScrollPane = new JScrollPane(tarifTextPane); // Add scroll support
		// ROW1 ADDES
		row1.add(label1);
		row1.add(cBoxCategories);
		row1.add(getMealsButton);
		// ROW2 ADDES
		row2.add(label2);
		row2.add(cBoxMeals);
		row2.add(getMealDetailButton);
		// ROW3 ADDES
		row3.add(tarifScrollPane);
		//seçilen kategori yemeklerini getir butonuna tıklandığında.
		getMealsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				JOptionPane.showMessageDialog(null,
//						newTextField.getText()+" yemek tarifi getiriliyor..",
//						"Bilgi",JOptionPane.INFORMATION_MESSAGE);
				String foodUrl = "https://www.themealdb.com/api/json/v1/1/filter.php?c="
						+ cBoxCategories.getSelectedItem().toString();
		        MealResponse mealResponse = restTemplate.getForObject(foodUrl, MealResponse.class);
				List<MealResponse.Meal> meals = mealResponse.getMeals();
				for(MealResponse.Meal m : meals){
					cBoxMeals.addItem(m.getStrMeal());
				}
			}
		});


		getMealDetailButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(cBoxMeals.getSelectedItem() == null){
					JOptionPane.showMessageDialog(null,
							"Yemek seçilmedi",
							"Hata",JOptionPane.ERROR_MESSAGE);
				}
				else{
					JOptionPane.showMessageDialog(null,
							"Seçilen yemek:" + cBoxMeals.getSelectedItem().toString() +
									"\nYemek tarifi getiriliyor..",
							"Bilgi",JOptionPane.INFORMATION_MESSAGE);
					int mealId = cBoxMeals.getSelectedIndex(); // meal id'si getirilecek.
					String mealName = cBoxMeals.getSelectedItem().toString();
					StringBuilder sbMealName = new StringBuilder(mealName);
					for (int i = 0; i < sbMealName.length(); i++) {
						if (sbMealName.charAt(i) == ' ') {
							sbMealName.setCharAt(i, '&');
						}
					}
					String mealDetailBaseUrl = "https://www.themealdb.com/api/json/v1/1/search.php?s=";
					String mealDetailUrl = mealDetailBaseUrl + sbMealName.toString();
					System.out.println("Meal detail url: " +mealDetailUrl);
					MealDetailResponse mealDetailResponse =
							restTemplate.getForObject(mealDetailUrl, MealDetailResponse.class);
					MealDetailResponse.MealDetail mealDetail = mealDetailResponse.getMeals().get(0);
					tarifTextPane.setText(mealDetail.getStrMeal() +
							" Tarifi:\n" + mealDetail.getStrInstructions());
				}
			}
		});
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
//		cBoxCategories.setPreferredSize(new Dimension(150, 25));
//		cBoxMeals.setPreferredSize(new Dimension(150, 25));
//		getMealsButton.setPreferredSize(new Dimension(140, 25));
//		getMealDetailButton.setPreferredSize(new Dimension(140, 25));

		row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
		row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
//		row1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//		row2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		frame.getContentPane().add(row1);
		frame.getContentPane().add(Box.createVerticalStrut(5)); // 5 piksel boşluk
		frame.getContentPane().add(row2);
		frame.getContentPane().add(Box.createVerticalStrut(5)); // 5 piksel boşluk
		frame.getContentPane().add(row3);
		JButton historyButton = new JButton("Tarif Geçmişi");
//		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		row3.add(historyButton);
//		frame.getContentPane().add(Box.createVerticalStrut(100)); // Üstünden biraz boşluk
//		frame.getContentPane().add(bottomPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
//		System.out.println("Kategoriler:");
//		for (int i = 0; i < categories.size(); i++) {
//			System.out.println((i + 1) + ". " + categories.get(i).getStrCategory());
//		}
//
//
//		Scanner sc = new Scanner(System.in);
//		System.out.print("Bir kategori seçin (1 - " + categories.size() + "): ");
//		int categoryChoice = sc.nextInt();
//
//
//		String selectedCategory = categories.get(categoryChoice - 1).getStrCategory();
//		System.out.println("Seçilen Kategori: " + selectedCategory);
//
//		String foodUrl = "https://www.themealdb.com/api/json/v1/1/filter.php?c=" + selectedCategory;
//		MealResponse mealResponse = restTemplate.getForObject(foodUrl, MealResponse.class);
//		List<MealResponse.Meal> meals = mealResponse.getMeals();
//
//		System.out.println("Yemekler:");
//		for (int i = 0; i < meals.size(); i++) {
//			System.out.println((i + 1) + ". " + meals.get(i).getStrMeal());
//		}
//
//		System.out.print("Bir yemek seçin (1 - " + meals.size() + "): ");
//		int mealChoice = sc.nextInt();
//
//		String selectedMealId = meals.get(mealChoice - 1).getIdMeal();
//		System.out.println("Seçilen Yemek: " + meals.get(mealChoice - 1).getStrMeal());
//
//		String mealDetailUrl = "https://www.themealdb.com/api/json/v1/1/lookup.php?i=" + selectedMealId;
//		MealDetailResponse mealDetailResponse = restTemplate.getForObject(mealDetailUrl, MealDetailResponse.class);
//		MealDetailResponse.MealDetail mealDetail = mealDetailResponse.getMeals().get(0);
//
//		System.out.println("\nTarif: " + mealDetail.getStrInstructions());
	}


}
