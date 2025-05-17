package com.example.demo;

import jdk.jfr.Experimental;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.util.Properties;
import java.io.File;

public class HistoryPanel extends JFrame {

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private int selectedRow;
    public HistoryPanel() {
        setTitle("Tarif Geçmişi");
        setSize(1200, 650);
        setLocationRelativeTo(null);

        // Tablo modeli oluştur
        String[] columnNames = {"Yemek Adı", "Tarif", "Favorilerde", "Fav Eklenme Tarihi", "İşlemler"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(400);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(40);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(70);


        // Tarif sütununu düzgün göstermek için cell renderer
        historyTable.getColumnModel().getColumn(1).
                setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                                                                   boolean isSelected, boolean hasFocus, int row, int column) {
                        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        if (c instanceof JLabel) {
                            String text = (String) value;
                            if (text.length() > 100) {
                                text = text.substring(0, 100) + "...";
                            }
                            ((JLabel) c).setText(text);
                        }
                        return c;
                    }
                });
        // JTEXTPANE & JSCROLLPANE
        JTextPane tarifTextPane = new JTextPane();
        tarifTextPane.setEditable(false); // Only for displaying
        tarifTextPane.setPreferredSize(new Dimension(900,380)); // Boyutu ayarladık
        tarifTextPane.setFont(new Font("Verdana", Font.BOLD, 15));
        tarifTextPane.setBackground(Color.lightGray);
        JScrollPane tarifScrollPane = new JScrollPane(tarifTextPane); // Add scroll support

        // İşlemler sütunu için buton renderer ve editor
        historyTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        historyTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), "favorite"));



        // Mevcut ListSelectionListener'ı şu şekilde değiştirin:
        historyTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                // Sadece selection işlemi bittiğinde çalışsın
                if (event.getValueIsAdjusting()) {
                    return;
                }

                try{
                    int selectedRow = historyTable.getSelectedRow();

                    // Geçerli bir satır seçili mi kontrol et
                    if (selectedRow >= 0 && selectedRow < historyTable.getRowCount()) {
                        String selectedMealName = historyTable.getValueAt(selectedRow, 0).toString();
                        String selectedInstruction = historyTable.getValueAt(selectedRow, 1).toString();
                        tarifTextPane.setText("Seçilen Yemek: " + selectedMealName
                                + "\nTarifi: " + selectedInstruction);
                    } else {
                        // Geçersiz seçim durumunda metin alanını temizle
                        tarifTextPane.setText("Bir yemek seçin...");
                    }
                } catch (Exception ex){
                    System.out.println("Selection error: " + ex.getMessage());
                    tarifTextPane.setText("Bir yemek seçin...");
                }
            }
        });


        JScrollPane scrollPane = new JScrollPane(historyTable);
        // ScrollPane için minimum/preferred boyut ayarla
        scrollPane.setPreferredSize(new Dimension(950, 250));

        // Ana panel oluştur
        JPanel mainPanel = new JPanel(new BorderLayout());



        JButton refreshButton = new JButton("Yenile");
        refreshButton.addActionListener(e -> loadMealHistory());

        JButton getPdfButton = new JButton("Favorileri PDF'e Dönüştür.");
        getPdfButton.addActionListener(e -> convertToPdf());

        JButton scheduleEmailButton = new JButton("Favorileri E-posta gönder(Gmail)");
        scheduleEmailButton.addActionListener(e -> scheduleEmail());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(getPdfButton);
        buttonPanel.add(scheduleEmailButton);

        JPanel mealDetailsPanel = new JPanel();



        mainPanel.add(scrollPane,BorderLayout.CENTER);
        mealDetailsPanel.add(tarifScrollPane,BorderLayout.CENTER);

        // Frame'e ana paneli ekle
        getContentPane().add(mainPanel,BorderLayout.NORTH);
        getContentPane().add(mealDetailsPanel,BorderLayout.CENTER);
        getContentPane().add(buttonPanel,BorderLayout.SOUTH);

        loadMealHistory();
    }

    private void loadMealHistory() {
        // Mevcut seçimi koru
        int currentSelection = historyTable.getSelectedRow();

        tableModel.setRowCount(0); // Tabloyu temizle

        try {
            MealDetailResponse response = LoadFromJSON.load();
            List<MealDetailResponse.MealDetail> meals = response.getMeals();

            for (MealDetailResponse.MealDetail meal : meals) {
                String favoriteStatus = meal.isFavorite() ? "Evet" : "Hayır";
                String addDate = meal.getAddFavDate() != null ? meal.getAddFavDate() : "-";

                tableModel.addRow(new Object[]{
                        meal.getStrMeal(),
                        meal.getStrInstructions(),
                        favoriteStatus,
                        addDate,
                        meal.isFavorite() ? "Favorilerden Çıkar" : "Favorilere Ekle",
                        "Sil"
                });
            }

            // Eğer satır mevcutsa seçim yapmaya çalış
            if (tableModel.getRowCount() > 0) {
                // Önceki seçim geçerliyse, onu geri yükle
                if (currentSelection >= 0 && currentSelection < tableModel.getRowCount()) {
                    historyTable.setRowSelectionInterval(currentSelection, currentSelection);
                } else {
                    // Son satır silinmişse veya geçersiz bir seçim varsa, yeni son satırı seç
                    int lastRow = tableModel.getRowCount() - 1;
                    historyTable.setRowSelectionInterval(lastRow, lastRow);
                }
            } else {
                // Hiç satır kalmadığında seçim yapmaya çalışma
                // Bu durumda tarifTextPane'i temizle
                // Bu satırı ListSelectionListener'a bırakabilirsiniz, o zaten kontrol ediyor
            }

            Logger.log("Tarif geçmişi başarıyla yüklendi");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Tarif geçmişi yüklenirken hata oluştu: " + e.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
            Logger.logError("Tarif geçmişi yüklenirken hata: " + e.getMessage());
        }
    }

    private void loadMealHistory(int i){
        // Bu metot kullanılmıyor, gerekirse implement edilebilir
    }

    private void convertToPdf(){
        try {
            List<MealDetailResponse.MealDetail> favorites = GetFavorites.getFavorites();

            if (favorites.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Hiç favori yemek bulunamadı.", "Uyarı", JOptionPane.INFORMATION_MESSAGE);
                Logger.log("PDF oluşturma isteği iptal edildi: Hiç favori yemek bulunamadı");
                return;
            }

            String dest = "favori_tarifler.pdf";
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(dest);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);

            document.add(new com.itextpdf.layout.element.Paragraph("Favori Tarifler").setBold().setFontSize(16));

            for (MealDetailResponse.MealDetail meal : favorites) {
                document.add(new com.itextpdf.layout.element.Paragraph("Yemek Adı: " + meal.getStrMeal()));
                document.add(new com.itextpdf.layout.element.Paragraph("Talimatlar: " + meal.getStrInstructions()));
                document.add(new com.itextpdf.layout.element.Paragraph("Favori Ekleme Tarihi: " + meal.getAddFavDate()));
                document.add(new com.itextpdf.layout.element.Paragraph("----------------------------------------------"));
            }

            document.close();

            JOptionPane.showMessageDialog(this, "PDF başarıyla oluşturuldu: " + dest);
            Logger.logPdfCreated(dest);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "PDF oluşturulurken hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            Logger.logError("PDF oluşturulurken hata: " + e.getMessage());
        }
    }

    private void scheduleEmail(){
        String to;
        boolean gmailAuth = false;

        do {
            to = JOptionPane.showInputDialog(null,
                    "Gmail adresinizi girin: ",
                    "Bilgi",
                    JOptionPane.INFORMATION_MESSAGE);

            if (to == null) {
                // Kullanıcı iptal etti
                return;
            }

            if(!to.contains("@gmail") || !to.contains(".com")){
                JOptionPane.showMessageDialog(null,
                        "Lütfen geçerli bir email adresi girin: ",
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
                Logger.logError("Geçersiz e-posta adresi girişi: " + to);
            }
            else{
                gmailAuth = true;
            }
        }while(!gmailAuth);

        Logger.log("E-posta gönderimi için adres girildi: " + to);

        String from = "furkantoparlak060@gmail.com"; // Senin Gmail adresin
        final String username = "furkantoparlak060@gmail.com"; // Gmail kullanıcı adın
        final String password = "zheo wowf avmg mavv"; // Gmail uygulama şifren

        // SMTP ayarları
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Önce PDF dosyasını oluştur
            convertToPdf();

            // E-posta mesajını oluştur
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Haftalık Favori Tarifler Raporu");

            // Metin kısmı
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Merhaba,\n\nFavori tarifleriniz ektedir.\n\nAfiyet olsun!");

            // PDF dosya eki
            MimeBodyPart attachmentPart = new MimeBodyPart();
            String filename = "favori_tarifler.pdf";
            File file = new File(filename);

            if(!file.exists()){
                JOptionPane.showMessageDialog(this, "PDF dosyası bulunamadı: " + filename);
                Logger.logError("E-posta gönderimi iptal edildi: PDF dosyası bulunamadı - " + filename);
                return;
            }

            DataSource source = new FileDataSource(filename);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(filename);

            // Multipart yapısı (metin + ek)
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            // Maili gönder
            Transport.send(message);

            JOptionPane.showMessageDialog(this, "E-posta başarıyla gönderildi.");
            Logger.logEmailSent(to);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "E-posta gönderilemedi: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            Logger.logError("E-posta gönderimi hatası: " + e.getMessage());
        }
    }

    // Buton renderer sınıfı
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Buton editor sınıfı
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;
        private String buttonType;

        public ButtonEditor(JCheckBox checkBox, String buttonType) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
            this.buttonType = buttonType;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                try {
                    if (buttonType.equals("favorite")) {
                        // Favori durumu değiştir
                        // Mevcut yemek detaylarını al
                        MealDetailResponse response = LoadFromJSON.load();
                        List<MealDetailResponse.MealDetail> meals = response.getMeals();

                        if (currentRow != -1 && currentRow < meals.size()) {
                            MealDetailResponse.MealDetail selectedMeal = meals.get(currentRow);

                            // Favori durumunu tersine çevir
                            boolean newFavoriteStatus = !selectedMeal.isFavorite();
//                            selectedMeal.setFavorite(newFavoriteStatus);
                            response.setMealIsFavorite(currentRow,newFavoriteStatus);
                            // Değişiklikleri kaydet
                            SavetoJSON.save(response, "meals.json");
                            // Tablo modelini doğrudan güncelle
                            tableModel.setValueAt(newFavoriteStatus ? "Evet" : "Hayır", currentRow, 2);
                            tableModel.setValueAt(newFavoriteStatus ? java.time.LocalDate.now().toString() : "-", currentRow, 3);
                            tableModel.setValueAt(newFavoriteStatus ? "Favorilerden Çıkar" : "Favorilere Ekle", currentRow, 4);
                            JOptionPane.showMessageDialog(button,
                                    selectedMeal.getStrMeal() +
                                            (newFavoriteStatus ? " favorilere eklendi." : " favorilerden çıkarıldı."));

                            // Log favori durumu değişikliği
                            if (newFavoriteStatus) {
                                Logger.logMealAddedToFavorites(selectedMeal.getStrMeal());
                            } else {
                                Logger.logMealRemovedFromFavorites(selectedMeal.getStrMeal());
                            }
                        }
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(button,
                            "İşlem sırasında hata oluştu: " + ex.getMessage(),
                            "Hata", JOptionPane.ERROR_MESSAGE);
                    Logger.logError("İşlem sırasında hata: " + ex.getMessage());
                }

            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
        @Override
        public void cancelCellEditing() {
            super.cancelCellEditing();
            isPushed = false;
        }
    }
}