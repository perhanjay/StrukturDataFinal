import GimmickApp.MiniCalculator;
import GimmickApp.MiniNotepad;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class MainApp extends Application {

    // DEFINISI FONT STACK (San Francisco -> Segoe UI -> Sans Serif)
    private static final String FONT_STYLE = "-fx-font-family: 'SF Pro Display', 'San Francisco', 'Segoe UI', sans-serif;";

    private RBTree engTree;
    private RBTree idnTree;

    private boolean isIndoToEng = true;

    @Override
    public void start(Stage stage) {
        // 1. Inisialisasi Dua Pohon
        engTree = new RBTree();
        idnTree = new RBTree();

        // 2. Populate Data (Mengisi kedua pohon sekaligus)
        populateData();

        // --- UI COMPONENTS ---
        Label titleLabel = new Label("Kamus Sakti");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        // Label Mode Bahasa
        Label modeLabel = new Label("Mode: Indonesia -> Inggris");
        modeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #007AFF; -fx-font-weight: bold;");

        TextField searchField = new TextField();
        searchField.setPromptText("Masukkan kata Indonesia...");
        searchField.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12; -fx-font-size: 14px;");

        // Tombol Switch Bahasa
        Button switchButton = new Button("Ganti Bahasa (⇄)");
        switchButton.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #333; -fx-background-radius: 15; -fx-cursor: hand;");
        switchButton.setOnAction(e -> {
            isIndoToEng = !isIndoToEng; // Toggle status
            if (isIndoToEng) {
                modeLabel.setText("Mode: Indonesia -> Inggris");
                searchField.setPromptText("Masukkan kata Indonesia...");
            } else {
                modeLabel.setText("Mode: English -> Indonesia");
                searchField.setPromptText("Enter English word...");
            }
        });

        Button searchButton = new Button("Cari / Search");
        searchButton.setDefaultButton(true);
        searchButton.setMaxWidth(Double.MAX_VALUE);
        searchButton.setStyle("-fx-background-color: linear-gradient(to right, #007AFF 0%, #00C6FF 100%); -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 12 20;");

        // Result Card
        Label resultTitle = new Label("Hasil:");
        resultTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #aaaaaa;");

        Label resultLabel = new Label("...");
        resultLabel.setWrapText(true);
        resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #444444;");

        // Label Tambahan untuk Terjemahan Balik
        Label transTitle = new Label("Terjemahan / Translation:");
        transTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #aaaaaa; -fx-padding: 10 0 0 0;");

        Label transLabel = new Label("-");
        transLabel.setWrapText(true);
        transLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555; -fx-font-style: italic;");

        VBox resultCard = new VBox(5, resultTitle, resultLabel, transTitle, transLabel);
        resultCard.setPadding(new Insets(20));
        resultCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 5);");
        VBox.setVgrow(resultCard, Priority.ALWAYS);

        VBox contentLayout = new VBox(15);
        contentLayout.setAlignment(Pos.TOP_CENTER);
        contentLayout.setPadding(new Insets(40, 30, 40, 30));
        contentLayout.getChildren().addAll(titleLabel, modeLabel, switchButton, searchField, searchButton, resultCard);

        StackPane root = new StackPane(contentLayout);
        root.setStyle("-fx-background-color: #F5F5F7;" + FONT_STYLE);

        // --- EVENT LISTENER ---
        searchButton.setOnAction(e -> {
            String query = searchField.getText().toLowerCase().trim();

            // RESET ANIMASI
            root.setRotate(0); root.setTranslateX(0); root.setOpacity(1); root.setEffect(null);

            // LOGIKA PEMILIHAN POHON
            SearchResult hasil;
            if (isIndoToEng) {
                // Cari di pohon Indonesia
                hasil = idnTree.get(query);
            } else {
                // Cari di pohon Inggris
                hasil = engTree.get(query);
            }

            if (hasil != null) {
                resultLabel.setText(hasil.definition);
                // Tampilkan info terjemahan (Foreign Key + Foreign Def)
                transLabel.setText(hasil.foreignKey + ": " + hasil.foreignDefinition);

                resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
                if(hasil.gimmick != null) hasil.gimmick.execute(root);
            } else {
                resultLabel.setText("Tidak ditemukan / Not Found: " + query);
                transLabel.setText("-");
                resultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #FF3B30;");
            }
        });

        Scene scene = new Scene(root, 400, 650);
        stage.setTitle("Kamus Dua Arah");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void addDictionaryEntry(String idKey, String idDesc, String engKey, String engDesc, Gimmick gimmick){
        idnTree.insert(idKey, idDesc, engKey, engDesc, gimmick);
        engTree.insert(engKey, engDesc, idKey, idDesc, gimmick);
    }

    public void populateData(){

        addDictionaryEntry("tong", "balok besar kayu berbentuk silinder", "barrel", "a barrell with", (node) -> {
            RotateTransition rt = new RotateTransition(Duration.seconds(1), node);
            rt.setByAngle(360);
            rt.play();
        });

        addDictionaryEntry("tong", "Wadah besar berbentuk silinder.",
                "barrel", "Large cylindrical container.", (node) -> {
                    RotateTransition rt = new RotateTransition(Duration.seconds(1), node);
                    rt.setByAngle(360);
                    rt.play();
                });

        addDictionaryEntry("gempa", "Guncangan tiba-tiba pada permukaan bumi.",
                "earthquake", "Sudden shaking of the ground.", (node) -> {
                    TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
                    tt.setByX(10);
                    tt.setCycleCount(10);
                    tt.setAutoReverse(true);
                    tt.play();
                });

        addDictionaryEntry("python", "Bahasa pemrograman populer (buka editor).",
                "python", "Popular programming language (opens editor).", (node) -> {
                    try {
                        Stage noteStage = new Stage();
                        noteStage.setTitle("Python Editor");
                        MiniNotepad notepadApp = new MiniNotepad(() -> noteStage.close());
                        Scene scene = new Scene(notepadApp);
                        noteStage.setScene(scene);
                        noteStage.show();
                    } catch (Exception e) { System.out.println("Error: Class MiniNotepad hilang."); }
                });

        addDictionaryEntry("catatan", "Tulisan kecil untuk diingat.",
                "notepad", "Small application to write notes.", (node) -> {
                    try {
                        Stage noteStage = new Stage();
                        noteStage.setTitle("Notepad");
                        MiniNotepad notepadApp = new MiniNotepad(() -> noteStage.close());
                        Scene scene = new Scene(notepadApp);
                        noteStage.setScene(scene);
                        noteStage.show();
                    } catch (Exception e) { System.out.println("Error: Class MiniNotepad hilang."); }
                });

        addDictionaryEntry("hilang", "Tidak tahu arah atau lenyap.",
                "lost", "Don't know where to go or vanished.", (node) -> {
                    FadeTransition ft = new FadeTransition(Duration.seconds(2), node);
                    ft.setFromValue(1.0);
                    ft.setToValue(0.0);
                    ft.setAutoReverse(true);
                    ft.setCycleCount(2);
                    ft.play();
                });

        addDictionaryEntry("kalkulator", "Alat untuk menghitung angka.",
                "calculator", "Tool for calculating numbers.", (node) -> {
                    try {
                        Stage calcStage = new Stage();
                        calcStage.setTitle("Calculator");
                        MiniCalculator calcApp = new MiniCalculator(() -> calcStage.close());
                        Scene scene = new Scene(calcApp);
                        calcStage.setScene(scene);
                        calcStage.show();
                    } catch (Exception e) { System.out.println("Error: Class MiniCalculator hilang."); }
                });

        addDictionaryEntry("jantung", "Organ tubuh yang memompa darah.",
                "heart", "Organ that pumps blood.", (node) -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
                    st.setByX(0.2); st.setByY(0.2);
                    st.setCycleCount(6);
                    st.setAutoReverse(true);
                    st.play();
                });

        addDictionaryEntry("pesta", "Perayaan meriah.",
                "party", "A social gathering for celebration.", (node) -> {
                    ColorAdjust colorAdjust = new ColorAdjust();
                    node.setEffect(colorAdjust);
                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.ZERO, new KeyValue(colorAdjust.hueProperty(), -1)),
                            new KeyFrame(Duration.seconds(0.5), new KeyValue(colorAdjust.hueProperty(), 1))
                    );
                    timeline.setCycleCount(6);
                    timeline.setAutoReverse(true);
                    timeline.setOnFinished(e -> node.setEffect(null));
                    timeline.play();
                });

        addDictionaryEntry("cermin", "Permukaan yang memantulkan bayangan.",
                "mirror", "Surface that reflects images.", (node) -> {
                    RotateTransition rt = new RotateTransition(Duration.seconds(1), node);
                    rt.setAxis(Rotate.Y_AXIS);
                    rt.setByAngle(360);
                    rt.play();
                });

        addDictionaryEntry("kanguru", "Hewan berkantung yang suka melompat.",
                "kangaroo", "Marsupial known for jumping.", (node) -> {
                    TranslateTransition tt = new TranslateTransition(Duration.millis(500), node);
                    tt.setByY(-100);
                    tt.setCycleCount(4);
                    tt.setAutoReverse(true);
                    tt.setInterpolator(Interpolator.EASE_OUT);
                    tt.play();
                });

        addDictionaryEntry("buram", "Tidak jelas terlihat.",
                "blur", "Unclear or indistinct vision.", (node) -> {
                    GaussianBlur blur = new GaussianBlur(0);
                    node.setEffect(blur);
                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.ZERO, new KeyValue(blur.radiusProperty(), 0)),
                            new KeyFrame(Duration.seconds(0.5), new KeyValue(blur.radiusProperty(), 15)),
                            new KeyFrame(Duration.seconds(1.0), new KeyValue(blur.radiusProperty(), 0))
                    );
                    timeline.play();
                    timeline.setOnFinished(e -> node.setEffect(null));
                });

        addDictionaryEntry("pusing", "Sensasi berputar di kepala.",
                "dizzy", "Spinning sensation or loss of balance.", (node) -> {
                    RotateTransition rt = new RotateTransition(Duration.millis(100), node);
                    rt.setFromAngle(-5); rt.setToAngle(5);
                    rt.setCycleCount(10);
                    rt.setAutoReverse(true);
                    rt.play();
                });

        addDictionaryEntry("lendir", "Cairan kental dan lengket.",
                "slime", "Sticky, viscous liquid.", (node) -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
                    st.setFromX(1.0); st.setFromY(1.0);
                    st.setToX(1.1);   st.setToY(0.9);
                    st.setCycleCount(4);
                    st.setAutoReverse(true);
                    st.play();
                });

        addDictionaryEntry("hantu", "Makhluk halus menyeramkan.",
                "ghost", "Spooky spirit.", (node) -> {
                    try {
                        // Pastikan file /hantu.png ada di folder resources
                        String imagePath = "/hantu.png";
                        if (getClass().getResource(imagePath) != null) {
                            Image img = new Image(getClass().getResourceAsStream(imagePath));
                            ImageView imageView = new ImageView(img);
                            imageView.setFitWidth(400); imageView.setFitHeight(600);
                            imageView.setPreserveRatio(false);
                            if (node instanceof Pane) {
                                Pane rootPane = (Pane) node;
                                rootPane.getChildren().add(imageView);
                                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> rootPane.getChildren().remove(imageView)));
                                timeline.play();
                            }
                        }
                    } catch (Exception e) { /* Ignore if image missing */ }
                });

        addDictionaryEntry("burung", "Hewan bersayap yang bisa terbang.",
                "bird", "Winged animal capable of flight.", (node) -> {
                    TranslateTransition tt = new TranslateTransition(Duration.seconds(1), node);
                    tt.setByY(-200); tt.setByX(50);
                    tt.setAutoReverse(true);
                    tt.setCycleCount(2);
                    tt.play();
                });

        addDictionaryEntry("gravitasi", "Gaya tarik bumi.",
                "gravity", "Force that pulls objects down.", (node) -> {
                    if (node instanceof Pane) {
                        Pane container = (Pane) node;
                        for (javafx.scene.Node child : container.getChildren()) {
                            double originalY = child.getLayoutY() + child.getTranslateY();
                            Timeline timeline = new Timeline(
                                    new KeyFrame(Duration.ZERO, new KeyValue(child.translateYProperty(), originalY)),
                                    new KeyFrame(Duration.seconds(1.0), new KeyValue(child.translateYProperty(), 1000.0, Interpolator.EASE_IN)),
                                    new KeyFrame(Duration.seconds(1.5)),
                                    new KeyFrame(Duration.seconds(2.0), new KeyValue(child.translateYProperty(), originalY, Interpolator.EASE_OUT))
                            );
                            timeline.play();
                        }
                    }
                });

        addDictionaryEntry("apel", "Buah berwarna merah yang manis.", "apple", "Sweet red fruit.", null);
        addDictionaryEntry("buku", "Kumpulan kertas tertulis yang dijilid.", "book", "Bound papers used for reading.", null);
        addDictionaryEntry("kursi", "Perabot untuk duduk.", "chair", "Furniture for sitting.", null);
        addDictionaryEntry("meja", "Perabot dengan permukaan datar.", "table", "Furniture with a flat top.", null);
        addDictionaryEntry("rumah", "Bangunan tempat tinggal.", "house", "Building for human habitation.", null);
        addDictionaryEntry("mobil", "Kendaraan bermotor roda empat.", "car", "Four-wheeled motor vehicle.", null);
        addDictionaryEntry("anjing", "Hewan peliharaan setia.", "dog", "Loyal domestic animal.", null);
        addDictionaryEntry("kucing", "Hewan berbulu yang suka mengeong.", "cat", "Small furry domestic animal.", null);
        addDictionaryEntry("air", "Cairan bening sumber kehidupan.", "water", "Clear liquid essential for life.", null);
        addDictionaryEntry("api", "Panas dan cahaya dari pembakaran.", "fire", "Heat and light from combustion.", null);
        addDictionaryEntry("matahari", "Bintang pusat tata surya.", "sun", "Star at the center of the solar system.", null);
        addDictionaryEntry("bulan", "Satelit alami bumi.", "moon", "Natural satellite of the earth.", null);
        addDictionaryEntry("bintang", "Bola gas bercahaya di angkasa.", "star", "Luminous sphere of plasma.", null);
        addDictionaryEntry("pohon", "Tumbuhan berkayu besar.", "tree", "Large woody plant.", null);
        addDictionaryEntry("bunga", "Bagian tumbuhan yang indah.", "flower", "The reproductive part of a plant.", null);
        addDictionaryEntry("pintu", "Akses masuk ruangan.", "door", "Movable barrier for entry.", null);
        addDictionaryEntry("jendela", "Lubang angin berkaca.", "window", "Opening in a wall for light/air.", null);
        addDictionaryEntry("jalan", "Jalur lalu lintas.", "street", "Public road in a city.", null);
        addDictionaryEntry("kota", "Permukiman besar.", "city", "Large human settlement.", null);
        addDictionaryEntry("negara", "Wilayah dengan pemerintahan sendiri.", "country", "Nation with its own government.", null);
        addDictionaryEntry("waktu", "Masa atau durasi.", "time", "Duration measured in hours/minutes.", null);
        addDictionaryEntry("hari", "Periode 24 jam.", "day", "Period of 24 hours.", null);
        addDictionaryEntry("malam", "Waktu gelap setelah matahari terbenam.", "night", "Dark time after sunset.", null);
        addDictionaryEntry("makanan", "Sesuatu yang dimakan.", "food", "Substance consumed for nutrition.", null);
        addDictionaryEntry("minuman", "Cairan yang diminum.", "drink", "Liquid for consumption.", null);
        addDictionaryEntry("pena", "Alat tulis bertinta.", "pen", "Tool for writing with ink.", null);
        addDictionaryEntry("kertas", "Bahan untuk menulis.", "paper", "Material for writing or printing.", null);
        addDictionaryEntry("uang", "Alat tukar resmi.", "money", "Medium of exchange.", null);
        addDictionaryEntry("teman", "Kawan dekat.", "friend", "Person you know and like.", null);
        addDictionaryEntry("keluarga", "Orang terdekat atau sedarah.", "family", "Group of related people.", null);
        addDictionaryEntry("kerja", "Melakukan sesuatu untuk hasil.", "work", "Activity done for a purpose.", null);
        addDictionaryEntry("sekolah", "Tempat belajar.", "school", "Institution for educating students.", null);
        addDictionaryEntry("guru", "Pengajar di sekolah.", "teacher", "Person who teaches.", null);
        addDictionaryEntry("murid", "Pelajar di sekolah.", "student", "Person who is studying.", null);
        addDictionaryEntry("musik", "Seni suara.", "music", "Art of sound in time.", null);
        addDictionaryEntry("film", "Gambar bergerak.", "movie", "Motion picture.", null);
        addDictionaryEntry("main", "Melakukan aktivitas hiburan.", "game", "Activity for amusement.", null);
        addDictionaryEntry("cinta", "Perasaan kasih sayang.", "love", "Deep affection.", null);
        addDictionaryEntry("senang", "Perasaan gembira.", "happy", "Feeling or showing pleasure.", null);
        addDictionaryEntry("sedih", "Perasaan duka.", "sad", "Feeling sorrow or unhappiness.", null);
        addDictionaryEntry("besar", "Ukuran di atas rata-rata.", "big", "Of considerable size.", null);
        addDictionaryEntry("kecil", "Ukuran di bawah rata-rata.", "small", "Not large in size.", null);
        addDictionaryEntry("bagus", "Kualitas baik.", "good", "Of high quality.", null);
        addDictionaryEntry("buruk", "Kualitas jelek.", "bad", "Of poor quality.", null);
        addDictionaryEntry("lari", "Bergerak cepat.", "run", "Move at a speed faster than a walk.", null);
        addDictionaryEntry("jalan", "Bergerak santai.", "walk", "Move at a regular pace.", null);
        addDictionaryEntry("tidur", "Istirahat memejamkan mata.", "sleep", "State of rest.", null);
        addDictionaryEntry("bangun", "Sadar dari tidur.", "wake", "Emerge from sleep.", null);
        addDictionaryEntry("buka", "Tidak tertutup.", "open", "Allowing access.", null);
        addDictionaryEntry("tutup", "Tidak terbuka.", "close", "Block access.", null);
    }
}