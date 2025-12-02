package GimmickApp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MiniNotepad extends VBox {

    private TextArea inputArea;
    private TextArea outputArea;
    private Button btnRun; 
    private Runnable onCloseRequest;

    // FONT STACK
    private static final String FONT_SERIF = "-fx-font-family: 'Times New Roman', 'Georgia', serif;";
    private static final String FONT_SANS  = "-fx-font-family: 'Helvetica', 'Arial', sans-serif;";
    private static final String FONT_CODE  = "-fx-font-family: 'Consolas', 'Menlo', 'Monaco', monospace;";

    public MiniNotepad(Runnable onCloseRequest) {
        this.onCloseRequest = onCloseRequest;

        // Background Putih Bersih
        this.setAlignment(Pos.TOP_LEFT);
        this.setSpacing(15);
        this.setPadding(new Insets(30));
        this.setStyle("-fx-background-color: white;");
        this.setPrefSize(450, 600);

        initUI();
    }

    private void initUI() {
        // --- Header ---
        Label titleLabel = new Label("Playground");
        titleLabel.setStyle(FONT_SERIF + "-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: black;");
        
        Label subtitleLabel = new Label("Python 3.x Environment");
        subtitleLabel.setStyle(FONT_SANS + "-fx-font-size: 14px; -fx-text-fill: #999;");

        // --- Input Editor ---
        Label inputLabel = new Label("EDITOR");
        inputLabel.setStyle(FONT_SANS + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #999; -fx-letter-spacing: 1px;");
        
        inputArea = new TextArea();
        inputArea.setPromptText("print('Hello World')");
        inputArea.setPrefHeight(200);
        // Style Editor: Abu-abu Soft, Tanpa Border
        inputArea.setStyle(
            FONT_CODE +
            "-fx-font-size: 14px;" +
            "-fx-control-inner-background: #F2F2F7;" + // Abu-abu iOS
            "-fx-text-fill: black;" +
            "-fx-background-radius: 12; -fx-border-radius: 12;" +
            "-fx-border-width: 0; -fx-faint-focus-color: transparent; -fx-focus-color: transparent;" // Hapus garis biru saat diklik
        );

        // --- Output Console ---
        Label outputLabel = new Label("TERMINAL");
        outputLabel.setStyle(FONT_SANS + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #999; -fx-letter-spacing: 1px;");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(150);
        // Style Console: Hitam Pekat (Kontras Tinggi)
        outputArea.setStyle(
            FONT_CODE +
            "-fx-font-size: 13px;" +
            "-fx-control-inner-background: black;" + 
            "-fx-text-fill: white;" + // Teks Putih terminal
            "-fx-background-radius: 12; -fx-border-radius: 12;" +
            "-fx-border-width: 0;"
        );

        // --- Buttons ---
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnClose = new Button("Close");
        btnClose.setStyle(FONT_SANS + "-fx-background-color: transparent; -fx-text-fill: #999; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> { if (onCloseRequest != null) onCloseRequest.run(); });

        // Tombol Run: Hitam Solid
        btnRun = new Button("Run Code");
        btnRun.setStyle(
            FONT_SANS + 
            "-fx-background-color: black;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10 25;" +
            "-fx-font-weight: bold;"
        );
        btnRun.setOnAction(e -> runPythonCode());

        buttonBox.getChildren().addAll(btnClose, btnRun);

        // --- Layout Assembly ---
        this.getChildren().addAll(
            titleLabel, subtitleLabel, 
            inputLabel, inputArea, 
            outputLabel, outputArea, 
            buttonBox
        );
        VBox.setVgrow(inputArea, Priority.ALWAYS); 

        // Shortcut Ctrl+Enter
        KeyCombination runCombo = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
        inputArea.setOnKeyPressed(event -> {
            if (runCombo.match(event)) {
                btnRun.fire();
                event.consume();
            }
        });
    }

    private void runPythonCode() {
        String code = inputArea.getText();
        if (code.trim().isEmpty()) return;

        String originalText = btnRun.getText();
        btnRun.setText("Running...");
        btnRun.setDisable(true);

        new Thread(() -> {
            try {
                ProcessBuilder builder = new ProcessBuilder("python", "-c", "import sys; exec(sys.stdin.read())");
                builder.redirectErrorStream(true);
                Process process = builder.start();

                try (OutputStream os = process.getOutputStream()) {
                    os.write(code.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    output.append("\n[Exit: ").append(exitCode).append("]");
                }

                String finalOutput = output.toString();
                javafx.application.Platform.runLater(() -> {
                    outputArea.setText(finalOutput);
                    btnRun.setText(originalText);
                    btnRun.setDisable(false);
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    outputArea.setText("Error: " + e.getMessage());
                    btnRun.setText(originalText);
                    btnRun.setDisable(false);
                });
            }
        }).start();
    }
}