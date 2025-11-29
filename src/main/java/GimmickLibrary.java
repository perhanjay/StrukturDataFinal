import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
    
}
