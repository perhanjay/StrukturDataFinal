import javafx.animation.PauseTransition; // Import wajib untuk timer
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration; // Untuk mengatur durasi 3 detik
import java.util.List;

public class MainApp extends Application {

    // --- FONT STACK (Fixed UI) ---
    private static final String FONT_SERIF = "-fx-font-family: 'Times New Roman', 'Georgia', serif;";
    private static final String FONT_SANS  = "-fx-font-family: 'Helvetica', 'Arial', sans-serif;";

    // Komponen Global
    private TextField searchField;
    private Label wordLabel, tagLabel, defHeaderLabel, definitionLabel;
    private VBox suggestionBox; 
    private ScrollPane suggestionScroll; 
    private RBHashMap dictionary;
    private StackPane root;
    private VBox mainLayout; 
    
    // --- FITUR BARU: Auto Search Timer ---
    private PauseTransition searchDebounce; 

    @Override
    public void start(Stage stage) {
        // 1. Backend Init
        dictionary = new RBHashMap(100);
        DataLoader.loadFromJSON(dictionary);

        // --- INISIALISASI TIMER (3 DETIK) ---
        searchDebounce = new PauseTransition(Duration.seconds(1.5));
        // Apa yang terjadi setelah 3 detik diam? Jalankan pencarian!
        searchDebounce.setOnFinished(e -> {
            String text = searchField.getText();
            if (!text.trim().isEmpty()) {
                performSearch(text);
            }
        });

        Label titleLabel = new Label("Dictionary");
        titleLabel.setStyle(FONT_SERIF + "-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: black;");

        searchField = new TextField();
        searchField.setPromptText("Search here...");
        searchField.setStyle(
            FONT_SANS +
            "-fx-background-color: #F2F2F7;" + 
            "-fx-text-fill: black;" +
            "-fx-prompt-text-fill: #999999;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 12 15;" +
            "-fx-font-size: 14px;"
        );
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // Tombol Search (Tetap ada sebagai opsi manual)
        Button searchButton = new Button("Go");
        searchButton.setDefaultButton(true);
        searchButton.setStyle(
            FONT_SANS + "-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12; -fx-cursor: hand; -fx-padding: 12 20;"
        );
        searchButton.setOnAction(e -> performSearch(searchField.getText()));

        HBox searchContainer = new HBox(10, searchField, searchButton);
        searchContainer.setAlignment(Pos.CENTER);

        // --- LAYER 1: DETAIL DEFINISI ---
        wordLabel = new Label();
        wordLabel.setWrapText(true);
        wordLabel.setStyle(FONT_SERIF + "-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: black;");

        tagLabel = new Label("noun"); 
        tagLabel.setVisible(false);
        tagLabel.setStyle(FONT_SANS + "-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: black; -fx-padding: 4 10; -fx-background-radius: 15;");

        defHeaderLabel = new Label("DEFINITION");
        defHeaderLabel.setVisible(false);
        defHeaderLabel.setStyle(FONT_SANS + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #888888; -fx-letter-spacing: 1px;");

        definitionLabel = new Label("");
        definitionLabel.setWrapText(true);
        definitionLabel.setStyle(FONT_SANS + "-fx-font-size: 16px; -fx-text-fill: #333333; -fx-line-spacing: 4px;");

        VBox resultContainer = new VBox(10, wordLabel, tagLabel, new Region(), defHeaderLabel, definitionLabel);
        resultContainer.setAlignment(Pos.TOP_LEFT);
        resultContainer.setPadding(new Insets(20, 0, 0, 0));

        // --- LAYER 2: SUGGESTION LIST ---
        suggestionBox = new VBox(5);
        suggestionBox.setStyle("-fx-background-color: white;");
        
        suggestionScroll = new ScrollPane(suggestionBox);
        suggestionScroll.setFitToWidth(true);
        suggestionScroll.setStyle("-fx-background-color: white; -fx-background: white; -fx-border-color: transparent;");
        suggestionScroll.setVisible(false);
        suggestionScroll.setMaxHeight(300);
        
        StackPane contentStack = new StackPane(resultContainer, suggestionScroll);
        contentStack.setAlignment(Pos.TOP_LEFT);

        // --- LAYOUT UTAMA ---
        mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(40, 25, 40, 25));
        mainLayout.setAlignment(Pos.TOP_LEFT);
        mainLayout.getChildren().addAll(titleLabel, searchContainer, contentStack);

        root = new StackPane(mainLayout);
        root.setStyle("-fx-background-color: white;");

        // --- LOGIKA LIVE SEARCH + AUTO EXECUTE ---
        searchField.textProperty().addListener((obs, oldText, newText) -> {
    
            // 1. SELALU STOP Timer lama setiap ada ketikan baru
            searchDebounce.stop();

            // 2. CEK KEKOSONGAN (FIX UTAMA)
            // Jika kolom kosong, jangan tunggu detik-detikan. Langsung sembunyikan semua.
            if (newText == null || newText.trim().isEmpty()) {
                suggestionScroll.setVisible(false); // Tutup saran
                
                // Ambil container hasil (cara aman ambil referensi VBox hasil)
                VBox resultBox = (VBox) ((StackPane) mainLayout.getChildren().get(2)).getChildren().get(0);
                resultBox.setVisible(false); // Langsung sembunyikan hasil ("ghost" hilang)
                
                return; // STOP di sini. Jangan lanjut start timer.
            }

            // 3. Jika tidak kosong, baru jalankan timer debounce untuk pencarian otomatis
            searchDebounce.playFromStart();

            // 4. Logika Saran (Suggestion List) tetap jalan
            List<Suggestion> suggestions = dictionary.getSuggestions(newText.toLowerCase(), 8);
            if (suggestions.isEmpty()) {
                suggestionScroll.setVisible(false);
            } else {
                populateSuggestions(suggestions);
                suggestionScroll.setVisible(true);
                // Sembunyikan hasil detail saat user sedang mengetik (biar fokus ke saran)
                VBox resultBox = (VBox) ((StackPane) mainLayout.getChildren().get(2)).getChildren().get(0);
                resultBox.setVisible(false); 
            }
        });

        Scene scene = new Scene(root, 400, 650);
        stage.setTitle("Dictionary");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void populateSuggestions(List<Suggestion> suggestions) {
        suggestionBox.getChildren().clear();

        for (Suggestion item : suggestions) {
            HBox card = new HBox();
            card.setPadding(new Insets(15));
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: white; -fx-border-color: #E5E5EA; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");

            Label wordText = new Label(item.word());
            wordText.setStyle(FONT_SERIF + "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");
            
            Label defSnippet = new Label("  -  " + item.definition());
            defSnippet.setStyle(FONT_SANS + "-fx-font-size: 12px; -fx-text-fill: #8E8E93;");
            if (defSnippet.getText().length() > 25) {
                defSnippet.setText(defSnippet.getText().substring(0, 25) + "...");
            }

            card.getChildren().addAll(wordText, defSnippet);

            card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-background-color: #F2F2F7;")); 
            card.setOnMouseExited(e -> card.setStyle(card.getStyle() + "-fx-background-color: white;"));

            card.setOnMouseClicked(e -> {
                searchField.setText(item.word()); 
                performSearch(item.word());       
            });

            suggestionBox.getChildren().add(card);
        }
    }

    private void performSearch(String query) {
        // PENTING: Matikan timer jika pencarian sudah dieksekusi (manual atau otomatis)
        searchDebounce.stop();

        query = query.toLowerCase().trim();
        suggestionScroll.setVisible(false); 
        
        root.setRotate(0); root.setTranslateX(0); root.setOpacity(1); root.setEffect(null);
        root.getChildren().removeIf(node -> node != mainLayout);

        SearchResult hasil = dictionary.get(query);

        VBox resultContainer = (VBox) ((StackPane) mainLayout.getChildren().get(2)).getChildren().get(0);
        resultContainer.setVisible(true);

        if (hasil != null) {
            wordLabel.setText(query);
            tagLabel.setVisible(true);
            defHeaderLabel.setVisible(true);
            definitionLabel.setText(hasil.definition);
            definitionLabel.setStyle(definitionLabel.getStyle().replace("#E02020", "#333333")); 

            if (hasil.gimmick != null) hasil.gimmick.execute(root);
        } else {
            wordLabel.setText("?");
            tagLabel.setVisible(false);
            defHeaderLabel.setVisible(false);
            definitionLabel.setText("No definition found for \"" + query + "\"");
            definitionLabel.setStyle(definitionLabel.getStyle() + "-fx-text-fill: #E02020;");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}