package GimmickApp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MiniNotepad extends VBox {

    private TextArea notesArea;
    private Runnable onCloseRequest;

    // FONT STACK (Konsisten: Clean UI)
    private static final String FONT_SERIF = "-fx-font-family: 'Times New Roman', 'Georgia', serif;";
    private static final String FONT_SANS  = "-fx-font-family: 'Helvetica', 'Arial', sans-serif;";

    public MiniNotepad(Runnable onCloseRequest) {
        this.onCloseRequest = onCloseRequest;

        // Setup Container (Background Putih Bersih)
        this.setAlignment(Pos.TOP_LEFT);
        this.setSpacing(15);
        this.setPadding(new Insets(25));
        this.setStyle("-fx-background-color: white;");
        this.setPrefSize(400, 500);

        initUI();
    }

    private void initUI() {
        // --- Header ---
        Label titleLabel = new Label("Notepad");
        titleLabel.setStyle(FONT_SERIF + "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: black;");
        
        Label subtitleLabel = new Label("Tulis dan simpan ide-idemu.");
        subtitleLabel.setStyle(FONT_SANS + "-fx-font-size: 13px; -fx-text-fill: #999;");

        // --- Text Editor Area ---
        notesArea = new TextArea();
        notesArea.setPromptText("Mulai mengetik...");
        notesArea.setWrapText(true); // Word Wrap aktif
        
        // Styling Editor: Bersih, Abu-abu Soft, Tanpa Border Kasar
        notesArea.setStyle(
            FONT_SANS +
            "-fx-font-size: 14px;" +
            "-fx-control-inner-background: #F2F2F7;" + // Abu-abu iOS Style
            "-fx-text-fill: black;" +
            "-fx-background-radius: 12; -fx-border-radius: 12;" +
            "-fx-border-width: 0; -fx-faint-focus-color: transparent; -fx-focus-color: transparent;"
        );

        // --- Buttons Container ---
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        // 1. Tombol Save (Hitam Solid - Aksi Utama)
        Button btnSave = new Button("Save as .txt");
        btnSave.setStyle(
            FONT_SANS + 
            "-fx-background-color: black; -fx-text-fill: white;" + 
            "-fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 8 20; -fx-font-weight: bold;"
        );
        btnSave.setOnAction(e -> saveFile());

        // 2. Tombol Close (Teks Merah - Aksi Sekunder)
        Button btnClose = new Button("Close");
        btnClose.setStyle(
            FONT_SANS + 
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #FF3B30;" + // Merah Apple
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 14px;"
        );
        btnClose.setOnAction(e -> {
            if (onCloseRequest != null) onCloseRequest.run();
        });

        buttonBox.getChildren().addAll(btnClose, btnSave);

        // --- Layout Assembly ---
        this.getChildren().addAll(titleLabel, subtitleLabel, notesArea, buttonBox);
        
        // Agar area catatan mengisi sisa ruang yang ada
        VBox.setVgrow(notesArea, Priority.ALWAYS);
    }

    // --- LOGIKA MENYIMPAN FILE ---
    private void saveFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Catatan");
        
        // Filter agar hanya bisa simpan .txt
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("catatan_saya.txt");

        // Dapatkan Stage saat ini untuk menampilkan dialog modal
        Stage stage = (Stage) this.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                // Tulis isi TextArea ke file
                Files.writeString(file.toPath(), notesArea.getText());
                System.out.println("Berhasil disimpan ke: " + file.getAbsolutePath());
            } catch (IOException ex) {
                System.err.println("Gagal menyimpan file: " + ex.getMessage());
            }
        }
    }
}