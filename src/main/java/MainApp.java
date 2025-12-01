import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainApp extends Application {

    // DEFINISI FONT STACK (San Francisco -> Segoe UI -> Sans Serif)
    private static final String FONT_STYLE = "-fx-font-family: 'SF Pro Display', 'San Francisco', 'Segoe UI', sans-serif;";

    @Override
    public void start(Stage stage) {

        RBHashMap dictionary = new RBHashMap(100);
        DataLoader.loadFromJSON(dictionary);

        // --- UI COMPONENTS ---

        // Header Title
        Label titleLabel = new Label("Kamus Sakti");
        // Kita gunakan CSS untuk size & weight agar Font Family mengikuti Root
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        titleLabel.setPadding(new Insets(0, 0, 5, 0));

        // Subtitle
        Label subtitleLabel = new Label("Inggris - Indonesia");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #777777;");

        // Input Field
        TextField searchField = new TextField();
        searchField.setPromptText("Masukkan kata (misal: apel)...");
        // Styling CSS Input
        searchField.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #E0E0E0; -fx-border-radius: 8; -fx-background-radius: 8;" +
            "-fx-padding: 12;" +
            "-fx-font-size: 14px;" // Font family auto-inherit
        );

        // Tombol Cari
        Button searchButton = new Button("Terjemahkan");
        searchButton.setDefaultButton(true);
        searchButton.setMaxWidth(Double.MAX_VALUE);
        
        String normalButtonStyle = 
            "-fx-background-color: linear-gradient(to right, #007AFF 0%, #00C6FF 100%);" + // Warna Biru Apple style
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 12 20;" +
            "-fx-font-size: 14px; -fx-font-weight: bold;";
            
        String hoverButtonStyle = 
            "-fx-background-color: linear-gradient(to right, #0056b3 0%, #007AFF 100%);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 12 20;" +
            "-fx-font-size: 14px; -fx-font-weight: bold;";

        searchButton.setStyle(normalButtonStyle);
        searchButton.setOnMouseEntered(e -> searchButton.setStyle(hoverButtonStyle));
        searchButton.setOnMouseExited(e -> searchButton.setStyle(normalButtonStyle));

        // Result Card
        Label resultTitle = new Label("Definisi:");
        resultTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #aaaaaa;");

        Label resultLabel = new Label("Hasil pencarian akan muncul di sini...");
        resultLabel.setWrapText(true);
        resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #444444;");
        resultLabel.setAlignment(Pos.TOP_LEFT);
        resultLabel.setMaxWidth(Double.MAX_VALUE);
        
        VBox resultCard = new VBox(8, resultTitle, resultLabel);
        resultCard.setPadding(new Insets(20));
        resultCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 5);"
        );
        VBox.setVgrow(resultCard, Priority.ALWAYS);

        // Layout Utama
        VBox contentLayout = new VBox(15);
        contentLayout.setAlignment(Pos.TOP_CENTER);
        contentLayout.setPadding(new Insets(40, 30, 40, 30));
        contentLayout.getChildren().addAll(titleLabel, subtitleLabel, searchField, searchButton, resultCard);

        // Root Pane
        StackPane root = new StackPane(contentLayout);
        
        // PENTING: Terapkan Font Family di sini agar menurun ke semua anak (Inheritance)
        root.setStyle("-fx-background-color: #F5F5F7;" + FONT_STYLE); // Background abu-abu Apple

        // --- EVENT LISTENER ---
        searchButton.setOnAction(e -> {
            String query = searchField.getText().toLowerCase().trim();

            root.setRotate(0);
            root.setTranslateX(0);
            root.setOpacity(1);
            root.setEffect(null);

            SearchResult hasil = dictionary.get(query);

            if (hasil != null) {
                resultLabel.setText(hasil.definition);
                resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;"); // Reset warna
                if(hasil.gimmick != null) hasil.gimmick.execute(root);
            } else {
                resultLabel.setText("Maaf, kata \"" + query + "\" tidak ditemukan.");
                resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FF3B30;"); // Merah Apple
            }
        });

        Scene scene = new Scene(root, 400, 600);
        stage.setTitle("Kamus Sakti");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}