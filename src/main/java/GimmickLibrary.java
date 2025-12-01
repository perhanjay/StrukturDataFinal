import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class GimmickLibrary {
    public static final Gimmick ROTATE = (node) -> {
        RotateTransition rt = new RotateTransition(Duration.seconds(1), node);
        rt.setByAngle(360);
        rt.play();
    };

    public static final Gimmick SHAKE = (node) -> {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setByX(10);
        tt.setCycleCount(10);
        tt.setAutoReverse(true);
        tt.play();
    };

    public static final Gimmick FADE = (node) -> {
        FadeTransition ft = new FadeTransition(Duration.seconds(2), node);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        ft.play();
    };

    public static final Gimmick OPEN_CALCULATOR = (targetNode) -> {
        Stage calcStage = new Stage();
        calcStage.setTitle("Kalkulator Mini");

        // Callback: Tutup stage saat tombol close ditekan
        MiniCalculator calcApp = new MiniCalculator(() -> calcStage.close());

        Scene scene = new Scene(calcApp);
        calcStage.setScene(scene);

        // Posisikan window di dekat aplikasi utama (sedikit offset)
        if (targetNode.getScene() != null && targetNode.getScene().getWindow() != null) {
            double mainX = targetNode.getScene().getWindow().getX();
            double mainY = targetNode.getScene().getWindow().getY();
            calcStage.setX(mainX + 60);
            calcStage.setY(mainY + 60);
        }

        calcStage.show();
    };

    public static final Gimmick PULSE = (node) -> {
        ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
        st.setByX(0.2); // Membesar 20%
        st.setByY(0.2);
        st.setCycleCount(6); // 3 kali denyut (karena autoReverse)
        st.setAutoReverse(true);
        st.play();
    };

    // 2. EFEK CERMIN 3D (Rotate Y-Axis)
    public static final Gimmick FLIP = (node) -> {
        RotateTransition rt = new RotateTransition(Duration.seconds(1), node);
        rt.setAxis(Rotate.Y_AXIS); // Putar pada sumbu Y (Horizontal Flip)
        rt.setByAngle(360);        // Putar penuh kembali ke awal
        rt.play();
    };

    // 3. EFEK DISCO / WARNA-WARNI (ColorAdjust)
    public static final Gimmick DISCO = (node) -> {
        ColorAdjust colorAdjust = new ColorAdjust();
        node.setEffect(colorAdjust);

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.hueProperty(), -1)),
            new KeyFrame(Duration.seconds(0.5), new KeyValue(colorAdjust.hueProperty(), 1))
        );
        timeline.setCycleCount(6);
        timeline.setAutoReverse(true);
        // Reset efek setelah selesai agar warna kembali normal
        timeline.setOnFinished(e -> node.setEffect(null)); 
        timeline.play();
    };

    // 4. EFEK LONCAT (Translate Y)
    public static final Gimmick BOUNCE = (node) -> {
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), node);
        tt.setByY(-100); // Loncat ke atas 100px
        tt.setCycleCount(4); // 2 kali loncat
        tt.setAutoReverse(true);
        // Efek memantul yang realistis
        tt.setInterpolator(Interpolator.EASE_OUT); 
        tt.play();
    };
    
}
