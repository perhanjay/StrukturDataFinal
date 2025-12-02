package GimmickApp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class MiniCalculator extends VBox {

    private TextField display;
    private double num1 = 0;
    private String operator = "";
    private boolean start = true;
    private Runnable onCloseRequest;

    // Font Stack
    private static final String FONT_STYLE = "-fx-font-family: 'SF Pro Display', 'San Francisco', 'Segoe UI', sans-serif;";

    public MiniCalculator(Runnable onCloseRequest) {
        this.onCloseRequest = onCloseRequest;

        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        
        // Apply Font here
        this.setStyle("-fx-background-color: #F5F5F7;" + FONT_STYLE); 
        this.setPrefSize(320, 450);

        initUI();
    }

    private void initUI() {
        Label titleLabel = new Label("Mini Calc");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #8E8E93;");

        display = new TextField();
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setPrefHeight(80);
        // Style font size langsung di CSS
        display.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-text-fill: #1C1C1E;" +
            "-fx-font-size: 36px; -fx-font-weight: bold;" + 
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);"
        );

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
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
        
        Button btnEquals = new Button("=");
        styleButton(btnEquals, "equals");
        btnEquals.setPrefSize(60, 115);
        btnEquals.setOnAction(e -> processKey("="));
        grid.add(btnEquals, 3, 2, 1, 3);

        Button btnClose = new Button("Close App");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: #FF3B30; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 14px;");
        btnClose.setOnAction(e -> { if (onCloseRequest != null) onCloseRequest.run(); });

        this.getChildren().addAll(titleLabel, display, grid, btnClose);
    }

    private void addButton(GridPane grid, String text, int col, int row, String type) {
        Button btn = new Button(text);
        styleButton(btn, type);
        btn.setPrefSize(60, 50);
        btn.setOnAction(e -> processKey(text));
        grid.add(btn, col, row);
    }

    private void styleButton(Button btn, String type) {
        // Gunakan CSS untuk font size, bukan Font object
        btn.setStyle("-fx-background-radius: 12; -fx-cursor: hand; -fx-font-size: 18px; -fx-font-weight: bold;");

        switch (type) {
            case "num":
                btn.setStyle(btn.getStyle() + "-fx-background-color: white; -fx-text-fill: #1C1C1E; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);");
                break;
            case "op":
                btn.setStyle(btn.getStyle() + "-fx-background-color: #E5E5EA; -fx-text-fill: #1C1C1E;");
                break;
            case "clear":
                btn.setStyle(btn.getStyle() + "-fx-background-color: #FFECEC; -fx-text-fill: #FF3B30;");
                break;
            case "equals":
                btn.setStyle(btn.getStyle() + "-fx-background-color: linear-gradient(to bottom right, #007AFF 0%, #00C6FF 100%); -fx-text-fill: white;");
                break;
        }
    }
    
    // ... (Metode processKey dan calculate SAMA SEPERTI SEBELUMNYA, tidak perlu diubah) ...
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