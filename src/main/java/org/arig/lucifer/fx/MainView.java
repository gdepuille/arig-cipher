package org.arig.lucifer.fx;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.Objects;

public class MainView {

    private final Stage stage;

    private TextField sourceField;
    private TextField destField;
    private RadioButton encryptRadio;
    private RadioButton decryptRadio;
    private Button launchButton;
    private Label statusLabel;
    private ProgressBar progressBar;

    public MainView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        stage.setTitle("ARIG Cipher");
        stage.setWidth(560);
        stage.setHeight(635);
        stage.setMinWidth(480);
        stage.setMinHeight(575);

        Scene scene = new Scene(buildRoot());
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/org/arig/lucifer/fx/styles.css")).toExternalForm()
        );

        stage.setScene(scene);
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.centerOnScreen();
        stage.show();
    }

    private VBox buildRoot() {
        VBox root = new VBox(18);
        root.getStyleClass().add("root-pane");
        root.setPadding(new Insets(28));

        VBox statusCard = buildStatusCard();
        VBox.setVgrow(statusCard, Priority.ALWAYS);

        root.getChildren().addAll(
                buildHeader(),
                buildSourceCard(),
                buildOperationCard(),
                buildDestCard(),
                buildLaunchRow(),
                statusCard
        );
        return root;
    }

    private HBox buildHeader() {
        Image logoImg = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/org/arig/lucifer/fx/logo.png"))
        );
        ImageView logoView = new ImageView(logoImg);
        logoView.setFitHeight(50);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);

        StackPane logoContainer = new StackPane(logoView);
        logoContainer.setMinSize(116, 58);
        logoContainer.setMaxSize(116, 58);

        Label title = new Label("ARIG CIPHER");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Chiffrement de fichiers par algorithme Lucifer");
        subtitle.getStyleClass().add("app-subtitle");

        VBox textBox = new VBox(3, title, subtitle);
        textBox.setAlignment(Pos.CENTER_LEFT);
        textBox.setPadding(new Insets(0, 0, 0, 10));

        HBox header = new HBox(0, logoContainer, textBox);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private VBox buildSourceCard() {
        Label sectionLabel = new Label("FICHIER SOURCE");
        sectionLabel.getStyleClass().add("section-label");

        sourceField = new TextField();
        sourceField.setPromptText("Aucun fichier sélectionné...");
        sourceField.setEditable(false);
        sourceField.getStyleClass().add("path-field");
        HBox.setHgrow(sourceField, Priority.ALWAYS);

        Button browseBtn = new Button("Parcourir...");
        browseBtn.getStyleClass().add("browse-button");
        browseBtn.setOnAction(e -> handleBrowse());

        HBox row = new HBox(8, sourceField, browseBtn);
        row.setAlignment(Pos.CENTER);

        VBox card = new VBox(10, sectionLabel, row);
        card.getStyleClass().add("card");
        return card;
    }

    private VBox buildOperationCard() {
        Label sectionLabel = new Label("OPÉRATION");
        sectionLabel.getStyleClass().add("section-label");

        ToggleGroup group = new ToggleGroup();

        encryptRadio = new RadioButton("🔒  Chiffrer");
        encryptRadio.setToggleGroup(group);
        encryptRadio.setSelected(true);
        encryptRadio.getStyleClass().add("custom-radio");
        encryptRadio.selectedProperty().addListener((obs, o, n) -> updateDestField());

        decryptRadio = new RadioButton("🔓  Déchiffrer");
        decryptRadio.setToggleGroup(group);
        decryptRadio.getStyleClass().add("custom-radio");

        HBox radioRow = new HBox(24, encryptRadio, decryptRadio);
        radioRow.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(10, sectionLabel, radioRow);
        card.getStyleClass().add("card");
        return card;
    }

    private Label destSectionLabel;

    private VBox buildDestCard() {
        destSectionLabel = new Label("FICHIER DE SORTIE  —  extension .arig");
        destSectionLabel.getStyleClass().add("section-label");

        destField = new TextField();
        destField.setPromptText("Défini après sélection du fichier source...");
        destField.setEditable(false);
        destField.getStyleClass().addAll("path-field", "path-field-readonly");

        VBox card = new VBox(10, destSectionLabel, destField);
        card.getStyleClass().add("card");
        return card;
    }

    private HBox buildLaunchRow() {
        launchButton = new Button("▶   LANCER L'OPÉRATION");
        launchButton.getStyleClass().add("launch-button");
        launchButton.setDisable(true);
        launchButton.setPrefWidth(260);
        launchButton.setPrefHeight(44);
        launchButton.setOnAction(e -> handleLaunch());

        HBox row = new HBox(launchButton);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(4, 0, 4, 0));
        return row;
    }

    private VBox buildStatusCard() {
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(6);
        progressBar.setVisible(false);
        progressBar.getStyleClass().add("lucifer-progress");

        Label dot = new Label("●");
        dot.getStyleClass().add("status-dot");

        statusLabel = new Label("Prêt");
        statusLabel.getStyleClass().addAll("status-text", "status-idle");

        HBox indicator = new HBox(8, dot, statusLabel);
        indicator.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(10, progressBar, indicator);
        card.getStyleClass().addAll("card", "status-card");
        return card;
    }

    private void handleBrowse() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Sélectionner le fichier source");
        File selected = fc.showOpenDialog(stage);
        if (selected != null) {
            sourceField.setText(selected.getAbsolutePath());
            if (selected.getName().toLowerCase().endsWith(".arig")) {
                decryptRadio.setSelected(true);
            } else {
                encryptRadio.setSelected(true);
            }
            updateDestField();
            launchButton.setDisable(false);
        }
    }

    private void updateDestField() {
        String src = sourceField.getText();
        if (src == null || src.isBlank()) return;

        if (encryptRadio.isSelected()) {
            File srcFile = new File(src);
            String name = srcFile.getName();
            String parent = srcFile.getParent();
            int dotIdx = name.lastIndexOf('.');
            String base = dotIdx > 0 ? name.substring(0, dotIdx) : name;
            destSectionLabel.setText("FICHIER DE SORTIE  —  extension .arig");
            destField.setText(parent != null ? parent + File.separator + base + ".arig" : base + ".arig");
        } else {
            destSectionLabel.setText("FICHIER RESTAURÉ  —  nom extrait de l'en-tête");
            destField.setText("Nom d'origine restauré au déchiffrement...");
        }
    }

    private void handleLaunch() {
        String srcPath = sourceField.getText();
        boolean encrypt = encryptRadio.isSelected();
        File srcFile = new File(srcPath);
        String destDir = srcFile.getParent() != null ? srcFile.getParent() : ".";

        launchButton.setDisable(true);
        progressBar.setVisible(true);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        setStatus("⏳  Opération en cours...", "idle");

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return encrypt
                        ? CipherService.encrypt(srcPath, destDir)
                        : CipherService.decrypt(srcPath, destDir);
            }

            @Override
            protected void succeeded() {
                progressBar.setProgress(1.0);
                String outPath = getValue();
                destField.setText(outPath);
                String op = encrypt ? "Chiffrement" : "Déchiffrement";
                setStatus("✓  " + op + " terminé → " + new File(outPath).getName(), "success");
                launchButton.setDisable(false);
            }

            @Override
            protected void failed() {
                progressBar.setProgress(0);
                progressBar.setVisible(false);
                Throwable ex = getException();
                setStatus("✗  Erreur : " + (ex != null ? ex.getMessage() : "inconnue"), "error");
                launchButton.setDisable(false);
            }
        };

        Thread t = new Thread(task, "lucifer-worker");
        t.setDaemon(true);
        t.start();
    }

    private void setStatus(String text, String type) {
        statusLabel.setText(text);
        statusLabel.getStyleClass().removeAll("status-success", "status-error", "status-idle");
        statusLabel.getStyleClass().add("status-" + type);
    }
}
