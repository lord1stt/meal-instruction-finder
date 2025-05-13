package com.example.demo;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.CommandLineRunner;
import org.w3c.dom.css.RGBColor;
import org.yaml.snakeyaml.scanner.Constant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

@SpringBootApplication
public class DemoApplication {
	public static final String LG_ENG = "en";
	public static final String LG_TR = "tr";
	public static String translateText(String text, String source, String target) {
		System.out.println("--translateText() fonksiyonu çalıştı--");
		String url = "https://libretranslate.de/translate";

		RestTemplate restTemplate = new RestTemplate();

		// JSON verisini Map ile hazırla
		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("q", text);
		requestBody.put("source", source);
		requestBody.put("target", target);
		requestBody.put("format", "text");
		System.out.println("Json verisi map ile hazırlandı.");
		// Header ayarları
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		System.out.println("Header ayarları yapıldı.");

		// Request oluştur
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
		System.out.println("Request oluşturuldu.");

		// POST işlemi
		ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
		System.out.println("Post işlemi yapıldı");

		return response.getBody();
	}
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
		frame.setLocationRelativeTo(null); // this method display the JFrame to center position of a screen
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
		JLabel label1 = new JLabel("bir kategori seçin:  ".toUpperCase());
		JLabel label2 = new JLabel("bir yemek seçin:  ".toUpperCase());
		label1.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		label2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		JComboBox<String> cBoxMeals = new JComboBox<>();
		cBoxMeals.setMinimumSize(new Dimension(150,cBoxMeals.getHeight()));
		JButton getMealDetailButton = new JButton("Yemek tarifini getir.");
		// JTextPane for meal recipe
		JTextPane tarifTextPane = new JTextPane();
		tarifTextPane.setEditable(false); // Only for displaying
		tarifTextPane.setPreferredSize(new Dimension(750,350)); // Boyutu ayarladık
		tarifTextPane.setFont(new Font("Verdana", Font.BOLD, 15));
		tarifTextPane.setBackground(Color.lightGray);
		JScrollPane tarifScrollPane = new JScrollPane(tarifTextPane); // Add scroll support
		JButton translateButton = new JButton("Tarifi türkçeye çevir.");
//		translateButton.setMargin(new Insets(0,400,0,0));
		// ROW1 ADDES
		row1.add(label1);
		row1.add(cBoxCategories);
		row1.add(getMealsButton);
		// ROW2 ADDES
		row2.add(label2);
		row2.add(cBoxMeals);
		row2.add(getMealDetailButton);
		row2.add(translateButton);
//		System.out.println("getMealDetailButton Location:" + getMealDetailButton.getLocation());
//		System.out.println("Translate Button Location:" + translateButton.getLocation());
		// ROW3 ADDES
		row3.add(tarifScrollPane);
		final String[] mealInstruction = new String[1];

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
				cBoxMeals.removeAllItems(); // Önceki yemekleri temizle
				for(MealResponse.Meal m : meals){
					cBoxMeals.addItem(m.getStrMeal());
				}
				JOptionPane.showMessageDialog(null,
						cBoxCategories.getSelectedItem().toString()+ " kategorisi yemekleri getirildi.",
						"Bilgi",JOptionPane.INFORMATION_MESSAGE);
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
					int mealIndex = cBoxMeals.getSelectedIndex(); // meal id'si getirilecek.
					String mealName = cBoxMeals.getSelectedItem().toString();
					StringBuilder sbMealName = new StringBuilder(mealName);
					for (int i = 0; i < sbMealName.length(); i++) {
						if (sbMealName.charAt(i) == ' ') {
							sbMealName.setCharAt(i, '&');
						}
					}
					String mealDetailBaseUrl = "https://www.themealdb.com/api/json/v1/1/search.php?s=";
					String mealDetailUrl = "https://www.themealdb.com/api/json/v1/1/search.php?s="
							+ URLEncoder.encode(mealName, StandardCharsets.UTF_8);
					System.out.println("Meal detail url: " +mealDetailUrl);

					MealDetailResponse mealDetailResponse =
							restTemplate.getForObject(mealDetailUrl, MealDetailResponse.class);
					MealDetailResponse.MealDetail mealDetail = mealDetailResponse.getMeals().get(0);
					mealInstruction[0] = mealDetail.getStrInstructions();
					tarifTextPane.setText(mealDetail.getStrMeal() +
							" Tarifi:\n" + mealInstruction[0]);
					saveMealDetail(mealDetail); // Seçilen yemek JSON'a yazılıyor
				}
			}
		});
		// translate button click event
		translateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(tarifTextPane.getText() == null || tarifTextPane.getText().trim() == "" ||
						mealInstruction[0] == null){
					JOptionPane.showMessageDialog(null,
							"Tarif getirilmedi.",
							"Hata",JOptionPane.ERROR_MESSAGE);
				}
				else{
					String translatedText = translateText(mealInstruction[0], LG_ENG, LG_TR);
					tarifTextPane.setText(translatedText);
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

		// Tarif Geçmişi butonu için ActionListener eklendi
		historyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HistoryPanel historyPanel = new HistoryPanel();
				historyPanel.setVisible(true);
			}
		});

//		frame.getContentPane().add(Box.createVerticalStrut(100)); // Üstünden biraz boşluk
//		frame.getContentPane().add(bottomPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
//		System.out.println("Kategoriler:");
//		for (int i = 0; i < categories.size(); i++) {
//			System.out.println((i + 1) + ". " + categories.get(i).getStrCategory());
//		}
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
	// Tarif görüntülendiğinde meals.json dosyasına bilgileri kaydetme
	private static void saveMealDetail(MealDetailResponse.MealDetail mealDetail) {
		try {
			ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
			File file = new File("meals.json");
			MealDetailResponse response = LoadFromJSON.load(); // Mevcut verileri yükle
			List<MealDetailResponse.MealDetail> meals = response.getMeals();
			meals.removeIf(m -> m.getStrMeal().equalsIgnoreCase(mealDetail.getStrMeal())); // Aynı isimde yemek varsa sil
			meals.add(mealDetail); // Yeni yemeği ekle
			response.setMeals(meals); // Güncellenmiş veriyi dosyaya yaz
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, response);
			System.out.println("Meal Detail kaydedildi/güncellendi: " + mealDetail.getStrMeal());
		} catch (IOException e) {
			// deneme
			System.out.println("JSON dosyasına yazılırken bir hata oluştu: " + e.getMessage());
			e.printStackTrace();
		}
	}
}