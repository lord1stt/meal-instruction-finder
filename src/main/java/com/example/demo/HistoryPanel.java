package com.example.demo;

import javax.swing.*;
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

    public HistoryPanel() {
        setTitle("Tarif Geçmişi");
        setSize(1200, 500);
        setLocationRelativeTo(null);

        // Tablo modeli oluştur
        String[] columnNames = {"Yemek Adı", "Tarif", "Favorilerde", "Fav Eklenme Tarihi", "İşlemler"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Sadece işlemler sütunu düzenlenebilir
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

        // İşlemler sütunu için buton renderer ve editor
        historyTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        historyTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(historyTable);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Yenile");
        refreshButton.addActionListener(e -> loadMealHistory());

        JButton getPdfButton = new JButton("Favorileri PDF'e Dönüştür.");
        getPdfButton.addActionListener(e -> convertToPdf());

        JTextField emailTextField = new JTextField(30);


        JButton scheduleEmailButton = new JButton("E-posta gönder");
        scheduleEmailButton.addActionListener(e -> scheduleEmail());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(getPdfButton);
        buttonPanel.add(emailTextField);
        buttonPanel.add(scheduleEmailButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        loadMealHistory();
    }

    private void loadMealHistory() {
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
                        meal.isFavorite() ? "Favorilerden Çıkar" : "Favorilere Ekle"
                });
            }

            historyTable.repaint();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Tarif geçmişi yüklenirken hata oluştu: " + e.getMessage(),
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void convertToPdf(){
        try {
            List<MealDetailResponse.MealDetail> favorites = GetFavorites.getFavorites();

            if (favorites.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Hiç favori yemek bulunamadı.", "Uyarı", JOptionPane.INFORMATION_MESSAGE);
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
                document.add(new com.itextpdf.layout.element.Paragraph("--------------------------------------------------"));
            }

            document.close();
            JOptionPane.showMessageDialog(this, "PDF başarıyla oluşturuldu: " + dest);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "PDF oluşturulurken hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }

    }
    private void scheduleEmail(){
//        String to = "yazikkafana5252@gmail.com";  // Alıcının e-posta adresi


        String to =  JOptionPane.showInputDialog(null,
                "Email adresinizi girin: ",
                "Bilgi",
                JOptionPane.INFORMATION_MESSAGE);
        String from = "furkantoparlak060@gmail.com";      // Senin Gmail adresin

        final String username = "furkantoparlak060@gmail.com";  // Gmail kullanıcı adın
        final String password = "zako uron nghz vttq";     // Gmail uygulama şifren

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

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "E-posta gönderilemedi: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
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

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
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
                    // Mevcut yemek detaylarını al
                    MealDetailResponse response = LoadFromJSON.load();
                    List<MealDetailResponse.MealDetail> meals = response.getMeals();

                    if (currentRow >= 0 && currentRow < meals.size()) {
                        MealDetailResponse.MealDetail selectedMeal = meals.get(currentRow);

                        // Favori durumunu tersine çevir
                        boolean newFavoriteStatus = !selectedMeal.isFavorite();
                        selectedMeal.setFavorite(newFavoriteStatus);

                        // Değişiklikleri kaydet
                        SavetoJSON.save(response, "meals.json");

                        // Tabloyu güncelle
                        loadMealHistory();

                        JOptionPane.showMessageDialog(button,
                                selectedMeal.getStrMeal() +
                                        (newFavoriteStatus ? " favorilere eklendi." : " favorilerden çıkarıldı."));
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(button,
                            "İşlem sırasında hata oluştu: " + ex.getMessage(),
                            "Hata", JOptionPane.ERROR_MESSAGE);
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
    }
}