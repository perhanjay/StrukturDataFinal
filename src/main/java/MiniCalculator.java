import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

// Ini adalah "Aplikasi" Kalkulator mini
public class MiniCalculator extends VBox {

    private TextField display;
    private double num1 = 0;
    private String operator = "";
    private boolean start = true;

    // Callback untuk menutup diri sendiri (agar MainApp bisa menghapusnya)
    private Runnable onCloseRequest;

    public MiniCalculator(Runnable onCloseRequest) {
        this.onCloseRequest = onCloseRequest;

        // Styling Panel Kalkulator
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        this.setStyle("-fx-background-color: #333; -fx-padding: 20; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");
        this.setMaxSize(250, 350); // Ukuran fix

        initUI();
    }

    private void initUI() {
        // Layar Display
        display = new TextField();
        display.setFont(Font.font(20));
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setPrefHeight(50);

        // Tombol Close
        Button btnClose = new Button("Tutup Aplikasi");
        btnClose.setStyle("-fx-background-color: #ff5555; -fx-text-fill: white;");
        btnClose.setMaxWidth(Double.MAX_VALUE);
        btnClose.setOnAction(e -> {
            if (onCloseRequest != null) onCloseRequest.run();
        });

        // Grid Angka
        GridPane grid = new GridPane();
        grid.setHgap(5); grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);

        String[] keys = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "C", "0", "=", "+"
        };

        int row = 0;
        int col = 0;

        for (String key : keys) {
            Button btn = new Button(key);
            btn.setPrefSize(50, 50);
            btn.setStyle("-fx-font-size: 16px; -fx-base: #ddd;");
            btn.setOnAction(e -> processKey(key));

            grid.add(btn, col, row);
            col++;
            if (col == 4) { col = 0; row++; }
        }

        this.getChildren().addAll(display, grid, btnClose);
    }

    // Logika Sederhana Kalkulator
    private void processKey(String key) {
        if ("0123456789".contains(key)) {
            if (start) { display.setText(""); start = false; }
            display.setText(display.getText() + key);
        } else if ("C".equals(key)) {
            display.setText(""); num1 = 0; operator = ""; start = true;
        } else if ("=".equals(key)) {
            if (operator.isEmpty()) return;
            double num2 = Double.parseDouble(display.getText());
            double result = calculate(num1, num2, operator);
            display.setText(String.valueOf(result));
            start = true;
        } else {
            if (!display.getText().isEmpty()) {
                num1 = Double.parseDouble(display.getText());
                operator = key;
                display.setText("");
            }
        }
    }

    private double calculate(double n1, double n2, String op) {
        switch (op) {
            case "+": return n1 + n2;
            case "-": return n1 - n2;
            case "*": return n1 * n2;
            case "/": return n2 == 0 ? 0 : n1 / n2;
            default: return 0;
        }
    }
}
