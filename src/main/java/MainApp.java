
import javafx.application.Application;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        RBHashMap dictionary = new RBHashMap(100);

        // Panggil DataLoader untuk mengisi kamus secara otomatis
        DataLoader.loadFromJSON(dictionary);

        TextField searchField = new TextField();

        Button searchButton = new Button("Cari Kata");

        Label resultLabel = new Label("Hasil pencarian...");
        resultLabel.setWrapText(true);
        resultLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox contentLayout = new VBox(15);
        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.getChildren().addAll(searchField, searchButton, resultLabel);

        StackPane root = new StackPane(contentLayout);
        root.setStyle("-fx-background-color: white; -fx-padding: 20;");


        // --- 3. LOGIKA PENGHUBUNG (EVENT LISTENER) ---
        searchButton.setOnAction(e -> {
            String query = searchField.getText().toLowerCase().trim();

            root.setRotate(0);
            root.setTranslateX(0);
            root.setOpacity(1);

            SearchResult hasil = dictionary.get(query);

            if (hasil != null) {
                resultLabel.setText(hasil.definition);

                if(hasil.gimmick != null){
                    hasil.gimmick.execute(root);
                }

            } else {
                resultLabel.setText("Kata tidak ditemukan");
            }
        });

        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("Kamus Sakti Inggris -> Indonesia");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}