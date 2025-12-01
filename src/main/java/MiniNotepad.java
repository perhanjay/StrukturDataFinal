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
    private Button btnRun; // Perlu akses global untuk trigger
    private Runnable onCloseRequest;

    // 1. FONT STACK (Konsisten dengan MainApp)
    private static final String FONT_STYLE = "-fx-font-family: 'SF Pro Display', 'San Francisco', 'Segoe UI', sans-serif;";
    // Font khusus coding (Monospace)
    private static final String CODE_FONT = "-fx-font-family: 'Consolas', 'Menlo', 'Monaco', monospace;";

    public MiniNotepad(Runnable onCloseRequest) {
        this.onCloseRequest = onCloseRequest;

        // 2. Setup Container (Background Abu-abu Apple Style)
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(15);
        this.setPadding(new Insets(25));
        this.setStyle("-fx-background-color: #F5F5F7;" + FONT_STYLE);
        this.setPrefSize(450, 600);

        initUI();
    }

    private void initUI() {
        // --- Header ---
        Label titleLabel = new Label("Python Playground");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1C1C1E;");
        
        Label subtitleLabel = new Label("Tulis kode Python, tekan Ctrl + Enter untuk Run");
        subtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #8E8E93;");

        // --- Input Editor ---
        Label inputLabel = new Label("Code Editor");
        inputLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #8E8E93;");
        inputLabel.setMaxWidth(Double.MAX_VALUE); // Align left
        
        inputArea = new TextArea();
        inputArea.setPromptText("Contoh:\nprint('Halo Dunia')\nprint(10 + 5)");
        inputArea.setPrefHeight(200);
        // Styling Editor: Putih, Rounded, Shadow Halus (Mirip Result Card MainApp)
        inputArea.setStyle(
            CODE_FONT +
            "-fx-font-size: 14px;" +
            "-fx-control-inner-background: white;" + 
            "-fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #E5E5EA;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);"
        );

        // --- Output Console ---
        Label outputLabel = new Label("Console Output");
        outputLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #8E8E93;");
        outputLabel.setMaxWidth(Double.MAX_VALUE);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(150);
        // Styling Console: Gelap, Teks Hijau Terminal
        outputArea.setStyle(
            CODE_FONT +
            "-fx-font-size: 13px;" +
            "-fx-control-inner-background: #1C1C1E;" + 
            "-fx-text-fill: #30D158;" + 
            "-fx-background-radius: 12; -fx-border-radius: 12;"
        );

        // --- Buttons ---
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        // Tombol Close (Red Text)
        Button btnClose = new Button("Close");
        btnClose.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #FF3B30;" + // Merah Apple
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 14px;"
        );
        btnClose.setOnAction(e -> {
            if (onCloseRequest != null) onCloseRequest.run();
        });

        // Tombol Run (Gradient Blue - SAMA dengan MainApp & Calculator)
        btnRun = new Button("▶ Run Code");
        String normalStyle = 
            "-fx-background-color: linear-gradient(to right, #007AFF 0%, #00C6FF 100%);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10 25;" +
            "-fx-font-size: 14px; -fx-font-weight: bold;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,122,255,0.3), 10, 0, 0, 5);";
            
        String hoverStyle = 
            "-fx-background-color: linear-gradient(to right, #0056b3 0%, #007AFF 100%);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 20;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10 25;" +
            "-fx-font-size: 14px; -fx-font-weight: bold;";

        btnRun.setStyle(normalStyle);
        btnRun.setOnMouseEntered(e -> btnRun.setStyle(hoverStyle));
        btnRun.setOnMouseExited(e -> btnRun.setStyle(normalStyle));
        btnRun.setOnAction(e -> runPythonCode());

        buttonBox.getChildren().addAll(btnClose, btnRun);

        // --- Layout Assembly ---
        this.getChildren().addAll(
            titleLabel, subtitleLabel, 
            inputLabel, inputArea, 
            outputLabel, outputArea, 
            buttonBox
        );
        VBox.setVgrow(inputArea, Priority.ALWAYS); // Agar editor mengisi ruang kosong

        // 3. FITUR UX: Shortcut Ctrl+Enter untuk Run
        KeyCombination runCombo = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
        inputArea.setOnKeyPressed(event -> {
            if (runCombo.match(event)) {
                btnRun.fire(); // Tekan tombol Run secara programatis
                event.consume(); // Cegah Enter membuat baris baru
            }
        });
    }

    private void runPythonCode() {
        String code = inputArea.getText();
        if (code.trim().isEmpty()) return;

        // Feedback Visual saat loading (Tombol jadi abu-abu sebentar)
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
                    output.append("\n[Process exited with code ").append(exitCode).append("]");
                }

                String finalOutput = output.toString();
                javafx.application.Platform.runLater(() -> {
                    outputArea.setText(finalOutput);
                    btnRun.setText(originalText);
                    btnRun.setDisable(false);
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    outputArea.setText("Error: Gagal menjalankan Python.\n" + e.getMessage());
                    btnRun.setText(originalText);
                    btnRun.setDisable(false);
                });
            }
        }).start();
    }
}