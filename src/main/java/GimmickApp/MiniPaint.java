package GimmickApp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class MiniPaint extends VBox {

    private Canvas canvas;
    private GraphicsContext gc;
    private Runnable onCloseRequest;

    // FONT STACK (Konsisten)
    private static final String FONT_SERIF = "-fx-font-family: 'Times New Roman', 'Georgia', serif;";
    private static final String FONT_SANS  = "-fx-font-family: 'Helvetica', 'Arial', sans-serif;";

    public MiniPaint(Runnable onCloseRequest) {
        this.onCloseRequest = onCloseRequest;

        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(15);
        this.setPadding(new Insets(20));
        // Background Putih
        this.setStyle("-fx-background-color: white;");
        this.setPrefSize(500, 650); // Ukuran sedikit lebih lebar untuk menggambar

        initUI();
    }

    private void initUI() {
        // --- Header ---
        Label titleLabel = new Label("Mini Canvas");
        titleLabel.setStyle(FONT_SERIF + "-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: black;");

        Label subtitleLabel = new Label("Draw your imagination");
        subtitleLabel.setStyle(FONT_SANS + "-fx-font-size: 14px; -fx-text-fill: #999;");

        // --- Canvas Area ---
        VBox canvasContainer = new VBox();
        // Border tipis abu-abu agar batas kanvas terlihat
        canvasContainer.setStyle("-fx-border-color: #E5E5EA; -fx-border-width: 1; -fx-background-color: white;");
        
        canvas = new Canvas(460, 450);
        gc = canvas.getGraphicsContext2D();
        initDrawLogic(); // Siapkan logika menggambar
        
        canvasContainer.getChildren().add(canvas);

        // --- Toolbar ---
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER);
        toolbar.setPadding(new Insets(10, 0, 0, 0));

        // Tombol Pen (Hitam)
        Button btnPen = new Button("Pen");
        btnPen.setStyle(
            FONT_SANS + 
            "-fx-background-color: black; -fx-text-fill: white;" + 
            "-fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 8 20; -fx-font-weight: bold;"
        );
        btnPen.setOnAction(e -> gc.setStroke(Color.BLACK));

        // Tombol Eraser (Putih dengan Border)
        Button btnEraser = new Button("Eraser");
        btnEraser.setStyle(
            FONT_SANS + 
            "-fx-background-color: white; -fx-text-fill: black;" + 
            "-fx-border-color: black; -fx-border-radius: 20;" +
            "-fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 7 19; -fx-font-weight: bold;"
        );
        btnEraser.setOnAction(e -> gc.setStroke(Color.WHITE));

        // Slider Ukuran Kuas
        Label sizeLabel = new Label("Size:");
        sizeLabel.setStyle(FONT_SANS + "-fx-font-size: 12px;");
        
        Slider sizeSlider = new Slider(1, 20, 3);
        sizeSlider.setPrefWidth(100);
        sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            gc.setLineWidth(newVal.doubleValue());
        });

        // Tombol Clear (Merah Minimalis)
        Button btnClear = new Button("Clear");
        btnClear.setStyle(
            FONT_SANS + 
            "-fx-background-color: white; -fx-text-fill: #FF3B30;" + 
            "-fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 12px;"
        );
        btnClear.setOnAction(e -> clearCanvas());

        toolbar.getChildren().addAll(btnPen, btnEraser, sizeLabel, sizeSlider, btnClear);

        // --- Footer (Close) ---
        Button btnClose = new Button("Close Gallery");
        btnClose.setStyle(FONT_SANS + "-fx-background-color: transparent; -fx-text-fill: #999; -fx-font-weight: bold; -fx-cursor: hand;");
        btnClose.setOnAction(e -> { if (onCloseRequest != null) onCloseRequest.run(); });

        this.getChildren().addAll(titleLabel, subtitleLabel, canvasContainer, toolbar, btnClose);
    }

    private void initDrawLogic() {
        // Setup Awal
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.setLineCap(StrokeLineCap.ROUND); // Ujung kuas bulat
        
        clearCanvas(); // Isi background putih

        // Event Listener Mouse
        canvas.setOnMousePressed(e -> {
            gc.beginPath();
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.setOnMouseDragged(e -> {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });
    }

    private void clearCanvas() {
        // Timpa seluruh kanvas dengan kotak putih
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}