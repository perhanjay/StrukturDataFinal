import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MiniCalculator extends VBox {

    private TextField display;
    private double num1 = 0;
    private String operator = "";
    private boolean start = true;
    private Runnable onCloseRequest;

    // Font Stack (Sama dengan MainApp)
    private static final String FONT_SERIF = "-fx-font-family: 'Times New Roman', 'Georgia', serif;";
    private static final String FONT_SANS  = "-fx-font-family: 'Helvetica', 'Arial', sans-serif;";

    public MiniCalculator(Runnable onCloseRequest) {
        this.onCloseRequest = onCloseRequest;

        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(20);
        this.setPadding(new Insets(30));
        
        // Background Putih Bersih
        this.setStyle("-fx-background-color: white;"); 
        this.setPrefSize(320, 500); // Sedikit lebih tinggi agar lega

        initUI();
    }

    private void initUI() {
        // Judul Minimalis
        Label titleLabel = new Label("Calculator");
        titleLabel.setStyle(FONT_SERIF + "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Layar Display (Angka Besar Serif)
        display = new TextField();
        display.setEditable(false);
        display.setAlignment(Pos.BOTTOM_RIGHT); // Angka di bawah kanan
        display.setPrefHeight(100);
        display.setStyle(
            FONT_SERIF +
            "-fx-background-color: transparent;" + // Tanpa kotak background
            "-fx-text-fill: black;" +
            "-fx-font-size: 48px;" + // Sangat besar
            "-fx-font-weight: bold;" + 
            "-fx-padding: 0 10 0 0;"
        );

        GridPane grid = new GridPane();
        grid.setHgap(15); 
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // Baris 1
        addButton(grid, "C", 0, 0, "clear");
        addButton(grid, "/", 1, 0, "op");
        addButton(grid, "*", 2, 0, "op");
        addButton(grid, "-", 3, 0, "op");
        // Baris 2
        addButton(grid, "7", 0, 1, "num");
        addButton(grid, "8", 1, 1, "num");
        addButton(grid, "9", 2, 1, "num");
        addButton(grid, "+", 3, 1, "op");
        // Baris 3
        addButton(grid, "4", 0, 2, "num");
        addButton(grid, "5", 1, 2, "num");
        addButton(grid, "6", 2, 2, "num");
        // Baris 4
        addButton(grid, "1", 0, 3, "num");
        addButton(grid, "2", 1, 3, "num");
        addButton(grid, "3", 2, 3, "num");
        // Baris 5
        addButton(grid, "0", 0, 4, "num"); 
        
        // Tombol Equals (Hitam Pekat)
        Button btnEquals = new Button("=");
        styleButton(btnEquals, "equals");
        btnEquals.setPrefSize(60, 125); // Tinggi menempati 2 baris
        btnEquals.setOnAction(e -> processKey("="));
        grid.add(btnEquals, 3, 2, 1, 3); // Span Row 3

        // Tombol Tutup (Teks Saja)
        Button btnClose = new Button("Close");
        btnClose.setStyle(FONT_SANS + "-fx-background-color: transparent; -fx-text-fill: #999; -fx-cursor: hand; -fx-font-size: 12px;");
        btnClose.setOnAction(e -> { if (onCloseRequest != null) onCloseRequest.run(); });

        this.getChildren().addAll(titleLabel, display, grid, btnClose);
    }

    private void addButton(GridPane grid, String text, int col, int row, String type) {
        Button btn = new Button(text);
        styleButton(btn, type);
        btn.setPrefSize(60, 55);
        btn.setOnAction(e -> processKey(text));
        grid.add(btn, col, row);
    }

    private void styleButton(Button btn, String type) {
        // Base Style
        btn.setStyle(FONT_SANS + "-fx-background-radius: 30; -fx-cursor: hand; -fx-font-size: 18px; -fx-font-weight: bold;");

        switch (type) {
            case "num":
                // Angka: Abu-abu Sangat Muda (Soft)
                btn.setStyle(btn.getStyle() + "-fx-background-color: #F2F2F7; -fx-text-fill: black;");
                // Hover: Sedikit lebih gelap
                btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace("#F2F2F7", "#E5E5EA")));
                btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("#E5E5EA", "#F2F2F7")));
                break;
            case "op":
                // Operator: Hitam (Kontras)
                btn.setStyle(btn.getStyle() + "-fx-background-color: black; -fx-text-fill: white;");
                break;
            case "clear":
                // Clear: Teks Merah Minimalis (Tanpa background mencolok)
                btn.setStyle(btn.getStyle() + "-fx-background-color: white; -fx-text-fill: #FF3B30; -fx-border-color: #FF3B30; -fx-border-radius: 30;");
                break;
            case "equals":
                // Equals: Hitam Besar
                btn.setStyle(btn.getStyle() + "-fx-background-color: black; -fx-text-fill: white;");
                break;
        }
    }
    
    // ... (Logika processKey dan calculate SAMA, tidak perlu diubah) ...
    private void processKey(String key) {
        if ("0123456789".contains(key)) {
            if (start) { display.setText(""); start = false; }
            display.setText(display.getText() + key);
        } else if ("C".equals(key)) {
            display.setText(""); num1 = 0; operator = ""; start = true;
        } else if ("=".equals(key)) {
            if (operator.isEmpty()) return;
            try {
                double num2 = Double.parseDouble(display.getText());
                double result = calculate(num1, num2, operator);
                if(result == (long) result) display.setText(String.format("%d", (long)result));
                else display.setText(String.valueOf(result));
                start = true;
            } catch (NumberFormatException ex) { display.setText("Error"); start = true; }
        } else {
            if (!display.getText().isEmpty()) {
                num1 = Double.parseDouble(display.getText());
                operator = key;
                display.setText("");
            }
        }
    }
    private double calculate(double n1, double n2, String op) {
        switch (op) { case "+": return n1 + n2; case "-": return n1 - n2; case "*": return n1 * n2; case "/": return n2 == 0 ? 0 : n1 / n2; default: return 0; }
    }
}