import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import GimmickApp.MiniCalculator;
import GimmickApp.MiniNotepad;
import GimmickApp.MiniPaint;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.media.AudioClip;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

// Import tambahan untuk Gimmick (Pastikan GimmickLibrary ada)
import javafx.animation.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;

public class MainApp extends Application {

    // --- 1. STYLE CONSTANTS (Gaya Hitam-Putih) ---
    private static final String FONT_SERIF = "-fx-font-family: 'Times New Roman', 'Georgia', serif;";
    private static final String FONT_SANS  = "-fx-font-family: 'Helvetica', 'Arial', sans-serif;";

    // --- 2. BACKEND COMPONENTS (Sesuai migrate-rbt) ---
    private RBTree engTree;
    private RBTree idnTree;
    private boolean isIndoToEng = true;

    // --- 3. UI COMPONENTS ---
    private TextField searchField;
    private Label wordLabel, tagLabel, defHeaderLabel, definitionLabel;
    private Label transTitle, transLabel; // KHUSUS migrate-rbt (Terjemahan Balik)
    private Label modeLabel;
    private Label titleLabel;
    
    private VBox suggestionBox;
    private ScrollPane suggestionScroll;
    private StackPane root;
    private VBox mainLayout;
    private Button switchBtn; 
    private boolean isDarkTheme = false;
    
    // Timer untuk Auto-Search (Debounce)
    private PauseTransition searchDebounce;
    private AnimationTimer activeTimer = null;

    @Override
    public void start(Stage stage) {
        // A. INISIALISASI BACKEND
        engTree = new RBTree();
        idnTree = new RBTree();
        populateData(); // Mengisi data ke kedua pohon

        // B. SETUP TIMER DEBOUNCE (1.5 Detik)
        searchDebounce = new PauseTransition(Duration.seconds(1.5));
        searchDebounce.setOnFinished(e -> {
            String text = searchField.getText().toLowerCase().trim();
            if (text.isEmpty()) return;

            // 1. Cek dulu: Apakah kata ini BENAR-BENAR ADA di kamus?
            boolean exactMatchFound;
            if (isIndoToEng) {
                exactMatchFound = (idnTree.get(text) != null);
            } else {
                exactMatchFound = (engTree.get(text) != null);
            }

            // 2. Hanya eksekusi pindah layar jika KATA DITEMUKAN
            if (exactMatchFound) {
                performSearch(text);
            } 
            // Else: Jangan lakukan apa-apa. 
            // Biarkan suggestion list tetap terlihat. Jangan tampilkan "Not Found".
        });

        // --- C. MEMBANGUN UI ---

        // 1. HEADER (Judul + Mode Switcher)
        titleLabel = new Label("Dictionary");
        titleLabel.setStyle(FONT_SERIF + "-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Mode Label (Kecil di atas)
        modeLabel = new Label("INDONESIA ⮕ ENGLISH");
        modeLabel.setStyle(FONT_SANS + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #888; -fx-letter-spacing: 1px;");
        
        // Tombol Switch (Minimalis)
        switchBtn = new Button("⇄ Switch");
        switchBtn.setStyle(
            FONT_SANS + 
            "-fx-background-color: transparent; -fx-text-fill: black;" + 
            "-fx-font-weight: bold; -fx-cursor: hand;" +
            "-fx-border-color: #E5E5EA; -fx-border-radius: 15; -fx-padding: 5 10;"
        );
        switchBtn.setOnAction(e -> {
            isIndoToEng = !isIndoToEng;
            modeLabel.setText(isIndoToEng ? "INDONESIA ⮕ ENGLISH" : "ENGLISH ⮕ INDONESIA");
            searchField.setPromptText(isIndoToEng ? "Ketik kata Indonesia..." : "Type English word...");
            searchField.clear(); // Reset input saat ganti bahasa
        });

        HBox headerBox = new HBox(15, modeLabel, switchBtn);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        // 2. SEARCH BAR (Tanpa Tombol Go)
        searchField = new TextField();
        searchField.setPromptText("Ketik kata Indonesia...");
        searchField.setStyle(
            FONT_SANS +
            "-fx-background-color: #F2F2F7;" + // Abu-abu iOS
            "-fx-text-fill: black;" +
            "-fx-prompt-text-fill: #999999;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 12 15;" +
            "-fx-font-size: 14px;"
        );
        
        // --- LOGIKA LISTENER (Gabungan UI Roomchat + Backend RBT) ---
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            searchDebounce.stop(); // Reset timer setiap ngetik

            // Jika kosong -> Sembunyikan semua
            if (newText == null || newText.trim().isEmpty()) {
                suggestionScroll.setVisible(false);
                getResultsContainer().setVisible(false);
                return;
            }

            searchDebounce.playFromStart(); // Mulai timer lagi

            // Ambil saran dari Tree yang AKTIF
            List<Suggestion> suggestions = new ArrayList<>();
            if (isIndoToEng) {
                // Pastikan RBTree.java sudah punya method collectSuggestions!
                idnTree.collectSuggestions(newText.toLowerCase(), suggestions, 6);
            } else {
                engTree.collectSuggestions(newText.toLowerCase(), suggestions, 6);
            }

            // Tampilkan Saran
            if (suggestions.isEmpty()) {
                suggestionScroll.setVisible(false);
            } else {
                populateSuggestions(suggestions);
                suggestionScroll.setVisible(true);
                getResultsContainer().setVisible(false); // Sembunyikan detail saat mengetik
            }
        });

        // 3. LAYER HASIL (DETAIL DEFINISI)
        wordLabel = new Label();
        wordLabel.setWrapText(true);
        wordLabel.setStyle(FONT_SERIF + "-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: black;");

        tagLabel = new Label("noun");
        tagLabel.setVisible(false);
        tagLabel.setStyle(FONT_SANS + "-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: black; -fx-padding: 4 10; -fx-background-radius: 15;");

        defHeaderLabel = new Label("DEFINITION");
        defHeaderLabel.setVisible(false);
        defHeaderLabel.setStyle(FONT_SANS + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #888888; -fx-letter-spacing: 1px;");

        definitionLabel = new Label("");
        definitionLabel.setWrapText(true);
        definitionLabel.setStyle(FONT_SANS + "-fx-font-size: 16px; -fx-text-fill: #333333; -fx-line-spacing: 4px;");

        // --- KOMPONEN BARU DARI MIGRATE-RBT (Terjemahan Balik) ---
        transTitle = new Label("TRANSLATION");
        transTitle.setVisible(false);
        transTitle.setStyle(FONT_SANS + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #888888; -fx-letter-spacing: 1px; -fx-padding: 15 0 5 0;");
        
        transLabel = new Label("");
        transLabel.setWrapText(true);
        transLabel.setStyle(FONT_SERIF + "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #555; -fx-font-style: italic;");

        VBox resultContainer = new VBox(10, wordLabel, tagLabel, new Region(), defHeaderLabel, definitionLabel, transTitle, transLabel);
        resultContainer.setAlignment(Pos.TOP_LEFT);
        resultContainer.setPadding(new Insets(20, 0, 0, 0));
        resultContainer.setVisible(false); // Sembunyi awal

        // 4. LAYER SARAN (SUGGESTION LIST)
        suggestionBox = new VBox(0);
        suggestionBox.setStyle("-fx-background-color: white;");
        
        suggestionScroll = new ScrollPane(suggestionBox);
        suggestionScroll.setFitToWidth(true);
        suggestionScroll.setStyle("-fx-background-color: white; -fx-background: white; -fx-border-color: transparent;");
        suggestionScroll.setVisible(false);
        suggestionScroll.setMaxHeight(300);

        // Tumpuk Hasil dan Saran
        StackPane contentStack = new StackPane(resultContainer, suggestionScroll);
        contentStack.setAlignment(Pos.TOP_LEFT);

        // 5. LAYOUT UTAMA
        mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(40, 25, 40, 25));
        mainLayout.setAlignment(Pos.TOP_LEFT);
        mainLayout.getChildren().addAll(titleLabel, headerBox, searchField, contentStack);

        root = new StackPane(mainLayout);
        root.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(root, 400, 650);
        stage.setTitle("Kamus Sakti");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    // --- HELPER METHODS ---

    // Mengambil container hasil dari StackPane (Agar kode lebih bersih)
    private VBox getResultsContainer() {
        return (VBox) ((StackPane) mainLayout.getChildren().get(3)).getChildren().get(0);
    }

    private void populateSuggestions(List<Suggestion> suggestions) {
        suggestionBox.getChildren().clear();
        
        // Tentukan warna berdasarkan tema SAAT INI
        String cardBg = isDarkTheme ? "#1E1E1E" : "white";
        String hoverBg = isDarkTheme ? "#333333" : "#F2F2F7";
        String textColor = isDarkTheme ? "white" : "black";
        String borderColor = isDarkTheme ? "#333333" : "#E5E5EA";

        for (Suggestion item : suggestions) {
            HBox card = new HBox();
            card.setPadding(new Insets(15));
            card.setAlignment(Pos.CENTER_LEFT);
            
            // Gunakan variabel warna dinamis di sini
            card.setStyle("-fx-background-color: " + cardBg + "; -fx-border-color: " + borderColor + "; -fx-border-width: 0 0 1 0; -fx-cursor: hand;");

            Label wordText = new Label(item.word());
            wordText.setStyle(FONT_SERIF + "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
            
            Label defSnippet = new Label("  -  " + item.definition());
            defSnippet.setStyle(FONT_SANS + "-fx-font-size: 12px; -fx-text-fill: #8E8E93;");
            if (defSnippet.getText().length() > 25) defSnippet.setText(defSnippet.getText().substring(0, 25) + "...");

            card.getChildren().addAll(wordText, defSnippet);

            // Update efek Hover juga
            card.setOnMouseEntered(e -> card.setStyle(card.getStyle().replace(cardBg, hoverBg))); 
            card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace(hoverBg, cardBg)));

            card.setOnMouseClicked(e -> {
                searchField.setText(item.word()); 
                performSearch(item.word());       
            });
            suggestionBox.getChildren().add(card);
        }
    }

    private void performSearch(String query) {
        searchDebounce.stop();

        if (activeTimer != null) {
            activeTimer.stop();
            activeTimer = null; // Reset
        }

        query = query.toLowerCase().trim();
        suggestionScroll.setVisible(false);
        
        // Reset Efek & Gimmick
        root.setRotate(0); root.setTranslateX(0); root.setOpacity(1); root.setEffect(null);
        root.getChildren().removeIf(node -> node != mainLayout);

        // LOGIKA BACKEND UTAMA (Pilih Tree)
        SearchResult hasil;
        if (isIndoToEng) {
            hasil = idnTree.get(query);
        } else {
            hasil = engTree.get(query);
        }

        VBox resultBox = getResultsContainer();
        resultBox.setVisible(true);

        if (hasil != null) {
            wordLabel.setText(query);
            tagLabel.setVisible(true);
            defHeaderLabel.setVisible(true);
            definitionLabel.setText(hasil.definition);
            definitionLabel.setStyle(definitionLabel.getStyle().replace("#E02020", "#333333")); // Reset merah

            // Tampilkan Info Terjemahan (Fitur migrate-rbt)
            transTitle.setVisible(true);
            transLabel.setText(hasil.foreignKey + " : " + hasil.foreignDefinition);

            // Eksekusi Gimmick
            if (hasil.gimmick != null) hasil.gimmick.execute(root);
        } else {
            wordLabel.setText("?");
            tagLabel.setVisible(false);
            defHeaderLabel.setVisible(false);
            transTitle.setVisible(false);
            transLabel.setText("");
            definitionLabel.setText("No definition found for \"" + query + "\"");
            definitionLabel.setStyle(definitionLabel.getStyle() + "-fx-text-fill: #E02020;"); // Merah
        }
    }

    // --- POPULATE DATA (PENTING: Insert ke Dua Pohon) ---
    public void addDictionaryEntry(String idKey, String idDesc, String engKey, String engDesc, Gimmick gimmick){
        idnTree.insert(idKey, idDesc, engKey, engDesc, gimmick);
        engTree.insert(engKey, engDesc, idKey, idDesc, gimmick);
    }

    public void populateData() {
        // ==========================================
        // KELOMPOK 1: ANIMASI VISUAL (GERAK & EFEK)
        // ==========================================

        // 1. ROTATE (Barrel/Tong)
        Gimmick rotateGimmick = (node) -> {
            RotateTransition rt = new RotateTransition(Duration.seconds(1), node);
            rt.setByAngle(360);
            rt.play();
        };
        addDictionaryEntry("tong", "Wadah besar silinder.", "barrel", "Large cylindrical container.", rotateGimmick);

        // 2. SHAKE (Earthquake/Gempa)
        Gimmick shakeGimmick = (node) -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
            tt.setByX(10);
            tt.setCycleCount(10);
            tt.setAutoReverse(true);
            tt.play();
        };
        addDictionaryEntry("gempa", "Guncangan bumi.", "earthquake", "Sudden shaking of ground.", shakeGimmick);

        // 3. FADE (Lost/Hilang)
        Gimmick fadeGimmick = (node) -> {
            FadeTransition ft = new FadeTransition(Duration.seconds(2), node);
            ft.setFromValue(1.0); ft.setToValue(0.0);
            ft.setAutoReverse(true); ft.setCycleCount(2);
            ft.play();
        };
        addDictionaryEntry("hilang", "Lenyap entah kemana.", "lost", "Vanished.", fadeGimmick);

        // 4. PULSE (Heart/Jantung)
        Gimmick pulseGimmick = (node) -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
            st.setByX(0.2); st.setByY(0.2);
            st.setCycleCount(6); st.setAutoReverse(true);
            st.play();
        };
        addDictionaryEntry("jantung", "Organ pemompa darah.", "heart", "Organ pumping blood.", pulseGimmick);

        // 5. FLIP (Mirror/Cermin)
        Gimmick flipGimmick = (node) -> {
            RotateTransition rt = new RotateTransition(Duration.seconds(1), node);
            rt.setAxis(Rotate.Y_AXIS);
            rt.setByAngle(360);
            rt.play();
        };
        addDictionaryEntry("cermin", "Pemantul bayangan.", "mirror", "Reflective surface.", flipGimmick);

        // 6. BOUNCE (Kangaroo/Kanguru)
        Gimmick bounceGimmick = (node) -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(500), node);
            tt.setByY(-100);
            tt.setCycleCount(4);
            tt.setAutoReverse(true);
            tt.setInterpolator(Interpolator.EASE_OUT);
            tt.play();
        };
        addDictionaryEntry("kanguru", "Hewan pelompat.", "kangaroo", "Jumping marsupial.", bounceGimmick);

        // 7. DISCO / PARTY (Ultimate Party Mode)
            Gimmick discoGimmick = (node) -> {
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
        addDictionaryEntry("pesta", "Perayaan meriah.", "party", "Celebration.", discoGimmick);

        // 8. DIZZY / PUSING (Wobble + Blur)
        Gimmick dizzyGimmick = (node) -> {
            RotateTransition rt = new RotateTransition(Duration.millis(150), node);
            rt.setFromAngle(-4); rt.setToAngle(4);
            rt.setCycleCount(30); rt.setAutoReverse(true);

            GaussianBlur blur = new GaussianBlur(0);
            node.setEffect(blur);
            Timeline blurTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(blur.radiusProperty(), 0)),
                new KeyFrame(Duration.seconds(1.0), new KeyValue(blur.radiusProperty(), 8)),
                new KeyFrame(Duration.seconds(2.0), new KeyValue(blur.radiusProperty(), 0))
            );
            blurTimeline.setCycleCount(2);

            rt.setOnFinished(e -> { node.setRotate(0); node.setEffect(null); });
            rt.play(); blurTimeline.play();
        };
        addDictionaryEntry("pusing", "Kepala berputar.", "dizzy", "Spinning sensation.", dizzyGimmick);

        // 9. BLUR (Buram)
        Gimmick blurGimmick = (node) -> {
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
        addDictionaryEntry("buram", "Tidak jelas.", "blur", "Unclear vision.", blurGimmick);

        // 10. JELLO (Slime/Lendir)
        Gimmick jelloGimmick = (node) -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(300), node);
            st.setFromX(1.0); st.setFromY(1.0);
            st.setToX(1.1);   st.setToY(0.9); // Gepeng
            st.setCycleCount(4); st.setAutoReverse(true);
            st.play();
        };
        addDictionaryEntry("lendir", "Cairan kental.", "slime", "Viscous liquid.", jelloGimmick);

        Gimmick ghostGimmick = (node) -> {
            try {
                String path = "/hantu.png";
                if (getClass().getResource(path) != null) {
                    Image img = new Image(getClass().getResourceAsStream(path));
                    ImageView view = new ImageView(img);
                    view.setFitWidth(400); view.setFitHeight(650); view.setPreserveRatio(false);
                    
                    if (node instanceof Pane) {
                        Pane p = (Pane) node;
                        p.getChildren().add(view);
                        new Timeline(new KeyFrame(Duration.seconds(1.5), e -> p.getChildren().remove(view))).play();
                    }
                }
            } catch (Exception e) { System.out.println("Gagal load hantu.png"); }
        };
        addDictionaryEntry("hantu", "Makhluk halus.", "ghost", "Spooky spirit.", ghostGimmick);

        Gimmick birdGimmick = (node) -> {
            try {
                String path = "/burung_kawin.png"; 
                if (getClass().getResource(path) != null) {
                    Image img = new Image(getClass().getResourceAsStream(path));
                    ImageView view = new ImageView(img);
                    view.setFitWidth(380); view.setPreserveRatio(true);
                    
                    StackPane overlay = new StackPane(view);
                    overlay.setStyle("-fx-background-color: rgba(0,0,0,0.8);"); 
                    overlay.setPrefSize(400, 650);
                    
                    overlay.setOnMouseClicked(e -> {
                        if(node instanceof Pane) ((Pane)node).getChildren().remove(overlay);
                    });

                    if (node instanceof Pane) ((Pane) node).getChildren().add(overlay);
                }
            } catch (Exception e) { System.out.println("Gagal load burung_kawin.png"); }
        };
        addDictionaryEntry("burung", "Hewan bersayap.", "bird", "Winged animal.", birdGimmick);

        Gimmick hackerGimmick = (node) -> {
            if (!(node instanceof Pane)) return;
            Pane rootPane = (Pane) node;
            
            // 1. Siapkan Canvas Full Screen
            Canvas matrixCanvas = new Canvas(rootPane.getWidth(), rootPane.getHeight());
            GraphicsContext gc = matrixCanvas.getGraphicsContext2D();
            
            // Masukkan ke root (paling depan)
            rootPane.getChildren().add(matrixCanvas);
            
            // 2. Setup Kolom Matrix
            int fontSize = 16;
            int columns = (int) (rootPane.getWidth() / fontSize);
            int[] drops = new int[columns]; // Posisi Y setiap kolom
            
            // 3. Animasi Loop
            AnimationTimer timer = new AnimationTimer() {
                long lastRun = 0;
                
                @Override
                public void handle(long now) {
                    if (now - lastRun < 50_000_000) return; // Batasi kecepatan (50ms)
                    lastRun = now;

                    // Efek Fade (Timpa layer hitam tipis agar jejak huruf terlihat pudar)
                    gc.setFill(new Color(0, 0, 0, 0.1));
                    gc.fillRect(0, 0, matrixCanvas.getWidth(), matrixCanvas.getHeight());
                    
                    gc.setFill(Color.LIME); // Huruf Hijau
                    gc.setFont(Font.font("Monospaced", fontSize));
                    
                    for (int i = 0; i < drops.length; i++) {
                        // Karakter Katakana / Acak
                        String text = String.valueOf((char) (0x30A0 + Math.random() * 96)); 
                        gc.fillText(text, i * fontSize, drops[i] * fontSize);
                        
                        // Reset jika sudah di bawah layar atau acak
                        if (drops[i] * fontSize > matrixCanvas.getHeight() && Math.random() > 0.975) {
                            drops[i] = 0;
                        }
                        drops[i]++;
                    }
                }
            };
            activeTimer = timer; 
            
            timer.start();
            
            // Klik berhenti
            matrixCanvas.setOnMouseClicked(e -> {
                timer.stop();
                activeTimer = null; // Bersihkan referensi
                rootPane.getChildren().remove(matrixCanvas);
            });
        };
        addDictionaryEntry("hacker", "Peretas sistem.", "hacker", "System intruder.", hackerGimmick);

        Gimmick soundGimmick = (node) -> {
            try {
                
                var resource = getClass().getResource("/presiden.mp3");
                if (resource == null) {
                    System.out.println("File audio tidak ditemukan!");
                    return;
                }
                // Load file audio dari resources
                String audioPath = getClass().getResource("/presiden.mp3").toExternalForm();
                AudioClip clip = new AudioClip(audioPath);
                
                // Mainkan suara
                clip.play();
                
                // Opsional: Tampilkan efek visual juga (misal gambar bendera atau hormat)
                // Disini kita pakai animasi simple scale sebagai tanda audio main
                ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
                st.setByX(0.1); st.setByY(0.1);
                st.setCycleCount(2); 
                st.setAutoReverse(true);
                st.play();
                
            } catch (Exception e) {
                System.out.println("Gagal memuat audio: " + e.getMessage());
            }
        };
        addDictionaryEntry("presiden", "Kepala negara.", "president", "Head of state.", soundGimmick);

        Gimmick calcGimmick = (node) -> {
            try {
                Stage s = new Stage();
                s.setTitle("Calculator");
                s.setScene(new Scene(new MiniCalculator(() -> s.close())));
                s.show();
            } catch (Exception e) { e.printStackTrace(); }
        };
        addDictionaryEntry("kalkulator", "Alat hitung.", "calculator", "Counting tool.", calcGimmick);

        // 14. NOTEPAD (Python)
        Gimmick noteGimmick = (node) -> {
            try {
                Stage s = new Stage();
                s.setTitle("Python Playground");
                s.setScene(new Scene(new MiniNotepad(() -> s.close())));
                s.show();
            } catch (Exception e) { e.printStackTrace(); }
        };
        addDictionaryEntry("python", "Bahasa program.", "python", "Programming language.", noteGimmick);
        addDictionaryEntry("catatan", "Aplikasi tulis.", "notepad", "Note app.", noteGimmick);

        Gimmick upsideDownGimmick = (node) -> {
            RotateTransition rt = new RotateTransition(Duration.seconds(1), node);
            rt.setToAngle(180); // Putar sampai terbalik
            rt.play();
            // Note: Tidak perlu reset manual disini, karena di performSearch() 
            // kita sudah punya 'root.setRotate(0)' yang akan meresetnya nanti.
        };
        addDictionaryEntry("terbalik", "Posisi atas di bawah.", "upside down", "Inverted position.", upsideDownGimmick);
        addDictionaryEntry("australia", "Negara di belahan bumi selatan.", "australia", "Country down under.", upsideDownGimmick);

        Gimmick dvdGimmick = (node) -> {
            if (!(node instanceof Pane)) return;
            Pane rootPane = (Pane) node;

            // Buat Label DVD
            Label dvdLabel = new Label("DVD");
            dvdLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 10; -fx-background-color: black; -fx-background-radius: 5;");
            
            // Container hitam full screen (agar fokus)
            Pane screensaverPane = new Pane(dvdLabel);
            screensaverPane.setStyle("-fx-background-color: black;");
            screensaverPane.setPrefSize(400, 650);
            
            rootPane.getChildren().add(screensaverPane);

            // Logika Gerak Pantul
            AnimationTimer timer = new AnimationTimer() {
                double x = 50, y = 50;
                double dx = 2, dy = 2; // Kecepatan
                
                @Override
                public void handle(long now) {
                    // Update posisi
                    x += dx;
                    y += dy;
                    
                    // Cek Tabrakan Dinding
                    if (x <= 0 || x + dvdLabel.getWidth() >= screensaverPane.getWidth()) {
                        dx = -dx; // Balik arah X
                        dvdLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 10; -fx-background-color: " + randomColor() + "; -fx-background-radius: 5;");
                    }
                    if (y <= 0 || y + dvdLabel.getHeight() >= screensaverPane.getHeight()) {
                        dy = -dy; // Balik arah Y
                        dvdLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 10; -fx-background-color: " + randomColor() + "; -fx-background-radius: 5;");
                    }
                    
                    dvdLabel.setLayoutX(x);
                    dvdLabel.setLayoutY(y);
                }
                
                // Helper warna acak
                private String randomColor() {
                    return "hsb(" + Math.random() * 360 + ", 70%, 100%)";
                }
            };
            timer.start();

            // Klik untuk keluar
            screensaverPane.setOnMouseClicked(e -> {
                timer.stop();
                rootPane.getChildren().remove(screensaverPane);
            });
        };
        addDictionaryEntry("lama", "Waktu yang panjang.", "boring", "Not interesting.", dvdGimmick);
        addDictionaryEntry("dvd", "Media penyimpanan optik.", "dvd", "Optical storage.", dvdGimmick);

        Gimmick roachGimmick = (node) -> {
            if (!(node instanceof Pane)) return;
            Pane rootPane = (Pane) node;

            try {
                // 1. Load Gambar (Sekali saja agar ringan)
                String path = "/kecoa.png"; 
                if (getClass().getResource(path) == null) {
                    System.out.println("Gagal: kecoa.png tidak ditemukan!");
                    return;
                }
                Image img = new Image(getClass().getResourceAsStream(path));

                // 2. Loop untuk memunculkan BANYAK kecoa
                int jumlahKecoa = 100; // Bisa ditambah jika ingin lebih ekstrem
                
                for (int i = 0; i < jumlahKecoa; i++) {
                    ImageView roach = new ImageView(img);
                    
                    // Acak Ukuran (Biar ada yang besar dan kecil)
                    double size = 40 + Math.random() * 60; // Antara 40px - 100px
                    roach.setFitWidth(size); 
                    roach.setPreserveRatio(true);

                    // 3. Tentukan Posisi Awal & Akhir secara Acak
                    double startX, startY, endX, endY;
                    double width = rootPane.getWidth();
                    double height = rootPane.getHeight();
                    
                    // Pilih sisi muncul acak (0=Atas, 1=Kanan, 2=Bawah, 3=Kiri)
                    int side = (int) (Math.random() * 4);
                    
                    if (side == 0) { // Dari Atas ke Bawah
                        startX = Math.random() * width; startY = -100;
                        endX = Math.random() * width;   endY = height + 100;
                    } else if (side == 1) { // Dari Kanan ke Kiri
                        startX = width + 100;           startY = Math.random() * height;
                        endX = -100;                    endY = Math.random() * height;
                    } else if (side == 2) { // Dari Bawah ke Atas
                        startX = Math.random() * width; startY = height + 100;
                        endX = Math.random() * width;   endY = -100;
                    } else { // Dari Kiri ke Kanan
                        startX = -100;                  startY = Math.random() * height;
                        endX = width + 100;             endY = Math.random() * height;
                    }

                    // Set posisi awal
                    roach.setTranslateX(startX);
                    roach.setTranslateY(startY);

                    // 4. Hitung Rotasi (Agar kepala menghadap arah lari)
                    // Math.atan2 menghasilkan sudut dalam radian, kita ubah ke derajat
                    double angle = Math.toDegrees(Math.atan2(endY - startY, endX - startX));
                    roach.setRotate(angle + 90); // +90 asumsi gambar asli menghadap ke atas

                    // Tambahkan ke layar
                    rootPane.getChildren().add(roach);

                    // 5. Animasi Jalan
                    // Kecepatan acak (1 detik s.d 4 detik) -> Makin kecil makin ngebut
                    double duration = 1.0 + Math.random() * 3.0;
                    
                    TranslateTransition tt = new TranslateTransition(Duration.seconds(duration), roach);
                    tt.setFromX(startX); tt.setFromY(startY);
                    tt.setToX(endX);     tt.setToY(endY);
                    
                    // Hapus kecoa dari memori setelah lewat layar
                    tt.setOnFinished(e -> rootPane.getChildren().remove(roach));
                    
                    // Beri sedikit delay acak agar tidak muncul serentak barengan
                    tt.setDelay(Duration.millis(Math.random() * 1000));
                    
                    tt.play();
                }

            } catch (Exception e) {
                System.out.println("Error cockroach: " + e.getMessage());
            }
        };
        // Update entri kamus
        addDictionaryEntry("jorok", "Kotor dan menjijikkan.", "dirty", "Not clean.", roachGimmick);
        addDictionaryEntry("kecoa", "Serangga hama.", "cockroach", "Pest insect.", roachGimmick);

        // 15. PAINT (Canvas) - Asumsi Anda punya class MiniPaint
        Gimmick paintGimmick = (node) -> {
            try {
                Stage s = new Stage();
                s.setTitle("Mini Canvas");
                s.setScene(new Scene(new MiniPaint(() -> s.close())));
                s.show();
            } catch (Exception e) {}
        };
        addDictionaryEntry("gambar", "Melukis.", "draw", "To sketch.", paintGimmick);

        Gimmick gravityGimmick = (node) -> {
            if (node instanceof Pane) {
                Pane container = (Pane) node;
                // Loop ke semua anak (elemen) di dalam layar
                for (javafx.scene.Node child : container.getChildren()) {
                    
                    // Simpan posisi translasi awal agar bisa dikembalikan
                    double originalY = child.getLayoutY() + child.getTranslateY();
                    
                    Timeline timeline = new Timeline(
                        // 1. Mulai dari posisi saat ini
                        new KeyFrame(Duration.ZERO, new KeyValue(child.translateYProperty(), child.getTranslateY())),
                        
                        // 2. Jatuh ke bawah (Y = 1000) dalam 1 detik (Makin lama makin cepat/Ease In)
                        new KeyFrame(Duration.seconds(1.0), new KeyValue(child.translateYProperty(), 1000, Interpolator.EASE_IN)),
                        
                        // 3. Diam di bawah selama 0.5 detik (sampai detik ke-1.5)
                        new KeyFrame(Duration.seconds(1.5)),
                        
                        // 4. Kembali ke posisi awal (Membal) dalam 0.5 detik
                        new KeyFrame(Duration.seconds(2.0), new KeyValue(child.translateYProperty(), 0, Interpolator.EASE_OUT))
                    );
                    timeline.play();
                }
            }
        };
        addDictionaryEntry("jatuh", "Bergerak ke bawah dengan cepat.", "fall", "Move downwards.", gravityGimmick);
        addDictionaryEntry("gravitasi", "Gaya tarik bumi.", "gravity", "Force that pulls objects down.", gravityGimmick);
        addDictionaryEntry("rusak", "Kondisi tidak berfungsi.", "broken", "Not working properly.", gravityGimmick);

        Gimmick bsodGimmick = (node) -> {
            if (!(node instanceof Pane)) return;
            Pane rootPane = (Pane) node;

            // Container Biru Error
            VBox bsod = new VBox(20);
            bsod.setAlignment(Pos.CENTER_LEFT);
            bsod.setPadding(new Insets(50));
            bsod.setStyle("-fx-background-color: #0078D7;"); // Warna Biru Windows
            bsod.setPrefSize(400, 650);

            // Emoticon Sedih
            Label sadFace = new Label(":(");
            sadFace.setStyle("-fx-font-size: 80px; -fx-text-fill: white; -fx-font-family: 'Segoe UI', sans-serif;");

            // Teks Error
            Label errorText = new Label("Your PC ran into a problem and needs to restart.\nWe're just collecting some error info, and then\nwe'll restart for you.");
            errorText.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-family: 'Segoe UI', sans-serif;");
            errorText.setWrapText(true);

            Label progress = new Label("0% complete");
            progress.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-family: 'Segoe UI', sans-serif;");

            bsod.getChildren().addAll(sadFace, errorText, progress);
            rootPane.getChildren().add(bsod);

            // Animasi Persentase Palsu
            Timeline crashTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> progress.setText("10% complete")),
                new KeyFrame(Duration.seconds(1.5), e -> progress.setText("45% complete")),
                new KeyFrame(Duration.seconds(3.0), e -> progress.setText("90% complete")),
                new KeyFrame(Duration.seconds(4.0), e -> progress.setText("100% complete"))
            );
            crashTimeline.play();

            // Klik untuk sembuh (Exit prank)
            bsod.setOnMouseClicked(e -> rootPane.getChildren().remove(bsod));
        };
        addDictionaryEntry("error", "Kesalahan sistem.", "error", "System mistake.", bsodGimmick);
        addDictionaryEntry("rusak", "Tidak berfungsi.", "crash", "System failure.", bsodGimmick);

        addDictionaryEntry("gelap", "Mode malam.", "dark", "Night mode.", (node) -> changeTheme(true));
        
        // 20. LIGHT MODE (Reset)
        addDictionaryEntry("terang", "Mode siang.", "light", "Day mode.", (node) -> changeTheme(false));

        addDictionaryEntry("apel", "Buah merah.", "apple", "Red fruit.", null);
        addDictionaryEntry("buku", "Jendela dunia.", "book", "Reading material.", null);
        addDictionaryEntry("rumah", "Tempat tinggal.", "house", "Place to live.", null);
        // ... Tambahkan data lain di sini ...
    }

    private void changeTheme(boolean isDark) {
        this.isDarkTheme = isDark; // Simpan status agar populateSuggestions tahu

        // Tentukan Palet Warna
        String bgColor = isDark ? "#121212" : "white";
        String textColor = isDark ? "white" : "black";
        String subTextColor = isDark ? "#B0B0B0" : "#888888";
        String fieldBg = isDark ? "#1E1E1E" : "#F2F2F7";
        String cardBg = isDark ? "#1E1E1E" : "white"; 

        // 1. Background Utama
        root.setStyle("-fx-background-color: " + bgColor + ";");

        // 2. Judul & Mode Label
        titleLabel.setStyle(FONT_SERIF + "-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
        modeLabel.setStyle(FONT_SANS + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: " + subTextColor + "; -fx-letter-spacing: 1px;");

        // 3. Search Field
        searchField.setStyle(
            FONT_SANS +
            "-fx-background-color: " + fieldBg + ";" + 
            "-fx-text-fill: " + textColor + ";" +
            "-fx-prompt-text-fill: #999;" +
            "-fx-background-radius: 12; -fx-padding: 12 15; -fx-font-size: 14px;"
        );

        // 4. Tombol Switch (FIX: Ubah warna text dan border)
        switchBtn.setStyle(
            FONT_SANS + 
            "-fx-background-color: transparent;" + 
            "-fx-text-fill: " + textColor + ";" + // Ikuti warna text (Putih/Hitam)
            "-fx-font-weight: bold; -fx-cursor: hand;" +
            "-fx-border-color: " + (isDark ? "#333" : "#E5E5EA") + ";" + // Border lebih gelap di dark mode
            "-fx-border-radius: 15; -fx-padding: 5 10;"
        );

        // 5. Hasil Definisi
        wordLabel.setStyle(FONT_SERIF + "-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
        tagLabel.setStyle(FONT_SANS + "-fx-font-size: 12px; -fx-text-fill: " + bgColor + "; -fx-background-color: " + textColor + "; -fx-padding: 4 10; -fx-background-radius: 15;");
        definitionLabel.setStyle(FONT_SANS + "-fx-font-size: 16px; -fx-text-fill: " + (isDark ? "#E0E0E0" : "#333333") + "; -fx-line-spacing: 4px;");
        transLabel.setStyle(FONT_SERIF + "-fx-font-size: 18px; -fx-text-fill: " + subTextColor + "; -fx-font-style: italic;");

        // 6. Container Suggestion (Hanya wadahnya)
        suggestionBox.setStyle("-fx-background-color: " + cardBg + ";");
        suggestionScroll.setStyle("-fx-background-color: " + cardBg + "; -fx-background: " + cardBg + "; -fx-border-color: transparent;");
    }


    public static void main(String[] args) {
        launch();
    }
}