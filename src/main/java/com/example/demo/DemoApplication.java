package com.example.demo;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import com.deepl.api.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootApplication
public class DemoApplication {
	public static final String LG_ENG = "en";
	public static final String LG_TR = "tr";
	// pushlanacak metin
	public static String translateTextFunction(String text, String sourceLang, String targetLang)  throws Exception {
		Translator translator;
		String authKey = "16b4af4f-4b68-4cde-b469-40799ba88e35:fx";  // Replace with your key
		translator = new com.deepl.api.Translator(authKey);
		TextResult result =
				translator.translateText(text, sourceLang, targetLang);
		String translatedText =result.getText();
		System.out.println("Çevirilen text: " + translatedText); // çevirilen metin
		return translatedText;
	}

	public static void main(String[] args) {
		//VARIABLES FOR USING API
		RestTemplate restTemplate = new RestTemplate();

		//CURRENT MEAL VARIABLES(AS 1 ELEMENT ARRAY)
		final String[] currentMeal = new String[1];
		final String[] mealInstruction = new String[1];

		// GUI SETTINGS

		//FRAME SETTINGS
		JFrame frame = new JFrame();
		frame.setSize(950, 500);
		frame.setLocationRelativeTo(null); // this method display the JFrame to center position of a screen
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);

		//PANELS
		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel categoriesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		categoriesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
		JPanel mealsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		mealsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
//		mealsPanel.setBackground(Color.gray);
		JPanel instructionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		//ADDING PANELS TO CONTAINER
		frame.getContentPane().add(headerPanel);
		frame.getContentPane().add(Box.createVerticalStrut(5)); // 5 piksel boşluk
		frame.getContentPane().add(categoriesPanel);
		frame.getContentPane().add(Box.createVerticalStrut(5)); // 5 piksel boşluk
		frame.getContentPane().add(mealsPanel);
		frame.getContentPane().add(Box.createVerticalStrut(5)); // 5 piksel boşluk
		frame.getContentPane().add(instructionPanel);
		frame.getContentPane().add(Box.createVerticalStrut(5)); // 5 piksel boşluk
		frame.getContentPane().add(buttonsPanel);

		//LABEL & TEXTFIELD
		JTextField newTextField = new JTextField(20);
		JLabel headerLabel = new JLabel("YEMEK TARİFİ UYGULAMASI".toUpperCase());
		headerLabel.setFont(new Font(Font.SANS_SERIF,Font.BOLD,25));
		JLabel label1 = new JLabel("bir kategori seçin:  ".toUpperCase());
		JLabel label2 = new JLabel("bir yemek seçin:  ".toUpperCase());
		label1.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		label2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

		//BUTTONS
		JButton getMealDetailButton = new JButton("Yemek tarifini getir.");
		JButton translateButton = new JButton("Tarifi türkçeye çevir.");
		JButton getMealsButton = new JButton("Yemekleri getir.");
		JButton historyButton = new JButton("Tarif Geçmişini Görüntüle");
		JButton addToFavButton = new JButton("Bu Tarifi Favorilere Ekle");

		// JTEXTPANE & JSCROLLPANE
		JTextPane tarifTextPane = new JTextPane();
		tarifTextPane.setEditable(false); // Only for displaying
		tarifTextPane.setPreferredSize(new Dimension(750,350)); // Boyutu ayarladık
		tarifTextPane.setFont(new Font("Verdana", Font.BOLD, 15));
		tarifTextPane.setBackground(Color.lightGray);
		JScrollPane tarifScrollPane = new JScrollPane(tarifTextPane); // Add scroll support


		//COMBOBOXES
		JComboBox<String> cBoxMeals = new JComboBox<>();
		cBoxMeals.setMaximumSize(new Dimension(100,400));
		JComboBox<String> cBoxCategories = new JComboBox<>();

		// PANEL ADDES
		headerPanel.add(headerLabel);

		categoriesPanel.add(label1);
		categoriesPanel.add(cBoxCategories);
		categoriesPanel.add(getMealsButton);

		mealsPanel.add(label2);
		mealsPanel.add(cBoxMeals);
		mealsPanel.add(getMealDetailButton);
		mealsPanel.add(translateButton);

		instructionPanel.add(tarifScrollPane);

		buttonsPanel.add(historyButton);
		buttonsPanel.add(addToFavButton);


		// GETTING DATA FROM API TO CBOXCATEGORIES
		String categoriesUrl = "https://www.themealdb.com/api/json/v1/1/categories.php";
		CategoryResponse categoryResponse = restTemplate.getForObject(categoriesUrl, CategoryResponse.class);
		List<Category> categories = categoryResponse.getCategories();
		for(Category c : categories){
			cBoxCategories.addItem(c.getStrCategory());
		}
		Logger.log("Kategoriler API'den alındı");

		//HANDLING BUTTON CLICKS

		//Bu Tarifi Favorilere Ekle butonuna tıklandığında
		addToFavButton.addActionListener(e -> {
			try {
				MealDetailResponse response = LoadFromJSON.load();
				List<MealDetailResponse.MealDetail> meals = response.getMeals();
				String selectedMealName = (String) cBoxMeals.getSelectedItem();
				if(selectedMealName == null) {
					JOptionPane.showMessageDialog(frame, "Favorilere eklemek için önce bir yemek seçmelisiniz.");
					Logger.logError("Favorilere eklemek için yemek seçilmedi");
					return;
				}
				boolean alreadyFavorite = false;
				for (MealDetailResponse.MealDetail meal : meals) {
					if (meal.getStrMeal().equals(selectedMealName)) {
						if(meal.isFavorite()) {
							alreadyFavorite = true;
							break;
						} else {
							meal.setFavorite(true);
							meal.setAddFavDate(java.time.LocalDate.now().toString());
							SavetoJSON.save(response, "meals.json");
							Logger.logMealAddedToFavorites(meal.getStrMeal());
							JOptionPane.showMessageDialog(frame, meal.getStrMeal() + " favorilere eklendi.");
							return;
						}
					}
				}
				if(alreadyFavorite) {
					JOptionPane.showMessageDialog(frame, "Bu yemek zaten favorilerde. Favorilerden kaldırmak için tarif geçmişi penceresine gidin.");
					Logger.log("Zaten favorilerde olan yemek: " + selectedMealName);
				} else {
					JOptionPane.showMessageDialog(frame, "Seçilen yemek JSON dosyasında bulunamadı.");
					Logger.logError("Seçilen yemek JSON dosyasında bulunamadı: " + selectedMealName);
				}
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(frame, "Favori eklenirken hata: " + ex.getMessage());
				Logger.logError("Favori eklenirken hata: " + ex.getMessage());
			}
		});

		//seçilen kategori yemeklerini getir butonuna tıklandığında.
		getMealsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedCategory = cBoxCategories.getSelectedItem().toString();
				Logger.logCategorySelected(selectedCategory);

				String foodUrl = "https://www.themealdb.com/api/json/v1/1/filter.php?c="
						+ cBoxCategories.getSelectedItem().toString();
				MealResponse mealResponse = restTemplate.getForObject(foodUrl, MealResponse.class);
				List<MealResponse.Meal> meals = mealResponse.getMeals();
				cBoxMeals.removeAllItems(); // Önceki yemekleri temizle
				for(MealResponse.Meal m : meals){
					cBoxMeals.addItem(m.getStrMeal());
				}
				Logger.log(selectedCategory + " kategorisi için " + meals.size() + " yemek getirildi");

				JOptionPane.showMessageDialog(null,
						cBoxCategories.getSelectedItem().toString()+ " kategorisi yemekleri getirildi.",
						"Bilgi",JOptionPane.INFORMATION_MESSAGE);
			}
		});

		//seçilen yemeğin tarifini getir butonuna tıklandığında.
		getMealDetailButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(cBoxMeals.getSelectedItem() == null){
					JOptionPane.showMessageDialog(null,
							"Yemek seçilmedi",
							"Hata",JOptionPane.ERROR_MESSAGE);
					Logger.logError("Tarif getirilirken yemek seçilmedi");
				}
				else{
					String mealName = cBoxMeals.getSelectedItem().toString();
					Logger.logMealSelected(mealName);

					JOptionPane.showMessageDialog(null,
							"Seçilen yemek:" + cBoxMeals.getSelectedItem().toString() +
									"\nYemek tarifi getiriliyor..",
							"Bilgi",JOptionPane.INFORMATION_MESSAGE);
					int mealIndex = cBoxMeals.getSelectedIndex(); // meal id'si getirilecek.
					StringBuilder sbMealName = new StringBuilder(mealName);
					for (int i = 0; i < sbMealName.length(); i++) {
						if (sbMealName.charAt(i) == ' ') {
							sbMealName.setCharAt(i, '&');
						}
					}
					String mealDetailBaseUrl = "https://www.themealdb.com/api/json/v1/1/search.php?s=";
					String mealDetailUrl = "https://www.themealdb.com/api/json/v1/1/search.php?s="
							+ URLEncoder.encode(mealName, StandardCharsets.UTF_8);
					System.out.println("Meal detail Url: " +mealDetailUrl);

					MealDetailResponse mealDetailResponse =
							restTemplate.getForObject(mealDetailUrl, MealDetailResponse.class);
					MealDetailResponse.MealDetail mealDetail = mealDetailResponse.getMeals().get(0);
					mealInstruction[0] = mealDetail.getStrInstructions();
					currentMeal[0] = mealDetail.getStrMeal();
					tarifTextPane.setText(currentMeal[0]  +
							" Tarifi:\n" + mealInstruction[0]);
					Logger.logRecipeRetrieved(currentMeal[0]);
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
							"Tarif çevirilemedi çünkü geçerli tarif yok.",
							"Hata", JOptionPane.ERROR_MESSAGE);
					Logger.logError("Çeviri yapılamadı - geçerli tarif yok");
				}
				else{
                    String translatedText = null;
                    try {
                        translatedText = translateTextFunction(
								currentMeal[0]  + " Tarifi:\n" + mealInstruction[0],
								LG_ENG, LG_TR);
						tarifTextPane.setText(translatedText);
						Logger.logRecipeTranslated(currentMeal[0], LG_ENG, LG_TR);
                    } catch (Exception ex) {
//                        throw new RuntimeException(ex);
						JOptionPane.showMessageDialog(null,
								ex.getMessage(),
								"Hata", JOptionPane.ERROR_MESSAGE);
						Logger.logError("Çeviri hatası: " + ex.getMessage());
                    }

				}
			}
		});

		// Tarif Geçmişi butonuna tıklandığında
		historyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Logger.log("Tarif geçmişi penceresi açıldı");
				HistoryPanel historyPanel = new HistoryPanel();
				historyPanel.setVisible(true);
			}
		});



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
			Logger.log("Meal Details kaydedildi/güncellendi: " + mealDetail.getStrMeal());
			System.out.println("Meal Detail kaydedildi/güncellendi: " + mealDetail.getStrMeal());
		} catch (IOException e) {
			Logger.logError("JSON dosyasına yazılırken hata: " + e.getMessage());
			System.out.println("JSON dosyasına yazılırken bir hata oluştu: " + e.getMessage());
			e.printStackTrace();
		}
	}
}