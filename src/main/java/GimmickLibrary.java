import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

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

    public static final Gimmick OPEN_NOTEPAD = (targetNode) -> {
        Stage noteStage = new Stage();
        noteStage.setTitle("Smart Notepad");

        MiniNotepad notepadApp = new MiniNotepad(() -> noteStage.close());

        Scene scene = new Scene(notepadApp);
        noteStage.setScene(scene);

        // Posisi Offset sedikit dari window utama
        if (targetNode.getScene() != null && targetNode.getScene().getWindow() != null) {
            double mainX = targetNode.getScene().getWindow().getX();
            double mainY = targetNode.getScene().getWindow().getY();
            noteStage.setX(mainX + 50);
            noteStage.setY(mainY + 50);
        }

        noteStage.show();
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

    public static final Gimmick BLUR = (node) -> {
        javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(0);
        node.setEffect(blur);

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(blur.radiusProperty(), 0)),
            new KeyFrame(Duration.seconds(0.5), new KeyValue(blur.radiusProperty(), 15)), // Buram maksimal
            new KeyFrame(Duration.seconds(1.0), new KeyValue(blur.radiusProperty(), 0))   // Kembali jelas
        );
        timeline.play();
        // Bersihkan efek setelah selesai agar tidak berat
        timeline.setOnFinished(e -> node.setEffect(null));
    };

    // 2. EFEK WOBBLE (GOYANG PUSING)
    public static final Gimmick WOBBLE = (node) -> {
        // 1. EFEK GOYANG CEPAT (Kepala pusing)
        RotateTransition rt = new RotateTransition(Duration.millis(150), node);
        rt.setFromAngle(-4); // Miring kiri
        rt.setToAngle(4);    // Miring kanan
        rt.setCycleCount(30); // Berlangsung sekitar 4.5 detik
        rt.setAutoReverse(true);

        // 2. EFEK PENGLIHATAN KABUR (Blur in-and-out)
        // Menggunakan GaussianBlur untuk mensimulasikan mata yang sulit fokus
        javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(0);
        node.setEffect(blur);
        
        Timeline blurTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(blur.radiusProperty(), 0)),
            new KeyFrame(Duration.seconds(1.0), new KeyValue(blur.radiusProperty(), 8)), // Menjadi buram
            new KeyFrame(Duration.seconds(2.0), new KeyValue(blur.radiusProperty(), 0))  // Kembali jelas
        );
        blurTimeline.setCycleCount(2); // Ulangi 2 kali siklus buram
        blurTimeline.setAutoReverse(false);

        // 3. EFEK DISORIENTASI (Slow Zoom/Breathing)
        ScaleTransition st = new ScaleTransition(Duration.seconds(2.25), node);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.05);  st.setToY(1.05); // Zoom in sedikit (5%)
        st.setCycleCount(2);
        st.setAutoReverse(true);

        // --- Jalankan Semua Efek ---
        rt.play();
        blurTimeline.play();
        st.play();

        // --- Cleanup (Penting agar UI kembali tajam) ---
        rt.setOnFinished(e -> {
            node.setRotate(0);
            node.setScaleX(1.0);
            node.setScaleY(1.0);
            node.setEffect(null); // Hapus blur sepenuhnya
        });
    };

    // 3. EFEK JELLO (KENYAL)
    public static final Gimmick JELLO = (node) -> {
        ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
        // Pipih melebar (Gepeng)
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.1);   st.setToY(0.9);
        st.setCycleCount(4);
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
        // Kita butuh akses ke method setStyle, jadi cast ke Region
        if (!(node instanceof javafx.scene.layout.Region)) return;
        javafx.scene.layout.Region region = (javafx.scene.layout.Region) node;

        // Simpan style lama agar bisa dikembalikan nanti
        String originalStyle = region.getStyle();

        // 1. ANIMASI WARNA-WARNI (Strobe Light)
        // Kita ubah background color setiap 0.15 detik
        Timeline colorTimeline = new Timeline(
            new KeyFrame(Duration.seconds(0.15), e -> {
                // Generate warna acak yang cerah (Hue 0-360, Saturation 1.0, Brightness 1.0)
                String randomColor = "hsb(" + (Math.random() * 360) + ", 100%, 100%)";
                // Terapkan background baru (pertahankan font style jika ada di root, tapi di MainApp kita aman)
                region.setStyle("-fx-background-color: " + randomColor + ";");
            })
        );
        colorTimeline.setCycleCount(20); // Berlangsung sekitar 3 detik (20 * 0.15)
        
        // 2. ANIMASI DENYUT (Pulsing Scale)
        ScaleTransition st = new ScaleTransition(Duration.seconds(0.3), node);
        st.setByX(0.05); // Membesar 5%
        st.setByY(0.05);
        st.setCycleCount(10); // 5 kali denyut (karena AutoReverse)
        st.setAutoReverse(true);

        // 3. ANIMASI GOYANG (Rocking Rotate)
        RotateTransition rt = new RotateTransition(Duration.seconds(0.15), node);
        rt.setFromAngle(-3); // Miring kiri 3 derajat
        rt.setToAngle(3);    // Miring kanan 3 derajat
        rt.setCycleCount(20);
        rt.setAutoReverse(true);

        // --- JALANKAN SEMUA BERSAMAAN ---
        colorTimeline.play();
        st.play();
        rt.play();

        // PENTING: Kembalikan ke tampilan Putih Bersih setelah pesta selesai
        colorTimeline.setOnFinished(e -> {
            region.setStyle(originalStyle); // Reset background ke Putih
            node.setScaleX(1.0); // Reset ukuran jaga-jaga
            node.setScaleY(1.0);
            node.setRotate(0);   // Reset rotasi
        });
    };

    public static final Gimmick JUMPSCARE = (node) -> {
        try {
            // 1. Load Gambar dari Resources
            // Pastikan nama file sesuai dengan yang Anda simpan di src/main/resources
            String imagePath = "/hantu.png"; 
            
            // Cek apakah file ada agar tidak error
            if (GimmickLibrary.class.getResource(imagePath) == null) {
                System.err.println("File gambar tidak ditemukan: " + imagePath);
                return;
            }

            Image img = new Image(GimmickLibrary.class.getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(img);

            // 2. Atur ukuran gambar memenuhi layar (400x600 sesuai MainApp)
            imageView.setFitWidth(400);
            imageView.setFitHeight(600);
            imageView.setPreserveRatio(false); // Paksa tarik gambar agar full screen

            // 3. Masukkan ke dalam Root (StackPane)
            if (node instanceof Pane) {
                Pane root = (Pane) node;
                root.getChildren().add(imageView); // Tumpuk di paling atas

                // 4. Hapus gambar otomatis setelah 1.5 detik
                Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(1.5), 
                    e -> root.getChildren().remove(imageView)
                ));
                timeline.play();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static final Gimmick OPEN_PAINT = (targetNode) -> {
        Stage paintStage = new Stage();
        paintStage.setTitle("Mini Canvas");

        MiniPaint paintApp = new MiniPaint(() -> paintStage.close());

        Scene scene = new Scene(paintApp);
        paintStage.setScene(scene);

        // Posisi Offset
        if (targetNode.getScene() != null && targetNode.getScene().getWindow() != null) {
            double mainX = targetNode.getScene().getWindow().getX();
            double mainY = targetNode.getScene().getWindow().getY();
            paintStage.setX(mainX + 40);
            paintStage.setY(mainY + 40);
        }

        paintStage.show();
    };

    public static final Gimmick SHOW_BURUNG = (node) -> {
        // Cek apakah node target adalah Pane (container)
        if (!(node instanceof Pane)) return;
        Pane root = (Pane) node;

        try {
            String imagePath = "/burung_kawin.png"; // Nama file di resources
            
            // Cek safety: apakah file ada?
            if (GimmickLibrary.class.getResource(imagePath) == null) {
                System.err.println("ERROR: Gambar " + imagePath + " tidak ditemukan di resources!");
                return;
            }

            // 1. Muat Gambar
            Image img = new Image(GimmickLibrary.class.getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(img);

            // 2. Atur ukuran agar pas di layar 400x600
            imageView.setFitWidth(380);  // Sedikit margin dari lebar 400
            imageView.setPreserveRatio(true); // Jaga proporsi gambar agar tidak gepeng

            // 3. Buat Container Overlay (Latar belakang gelap transparan)
            StackPane overlayContainer = new StackPane(imageView);
            overlayContainer.setStyle("-fx-background-color: rgba(0,0,0,0.8);"); // Hitam transparan 80%
            overlayContainer.setPrefSize(400, 600); // Ukuran full window

            // 4. Event: Klik gambar untuk menutupnya
            overlayContainer.setOnMouseClicked(e -> {
                root.getChildren().remove(overlayContainer);
            });

            // 5. Tampilkan (Tambahkan ke root paling atas)
            root.getChildren().add(overlayContainer);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal memuat gambar burung.");
        }
    };
    
}
