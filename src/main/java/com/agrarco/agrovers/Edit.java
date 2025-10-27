package com.agrarco.agrovers;

import com.agrarco.agrovers.Models.Anbar;
import com.agrarco.agrovers.Models.CASScaleReader;
import com.agrarco.agrovers.Models.Menteqe;
import com.agrarco.agrovers.Models.Region;
import com.agrarco.agrovers.Models.Tedarukcu;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class Edit {
    public class EditController {
        private String userRole;

        public EditController(String userRole) {
            this.userRole = userRole;
        }
    }
    private CASScaleReader scaleReader;
    private Scene formScene;
    private int editingIndex = -1;


    private TextField tarixField;
    private TextField neqliyyatField;
    private ComboBox<String> menteqeBox;
    private ComboBox<String> regionBox;
    private ComboBox<String> anbarBox;
    private ComboBox<String> tedarukcuBox;
    private TextField kiseSayiField;
    private TextField birKiseField;
    private TextField paletSayiField;
    private TextField birPaletField;
    private TextArea qeydArea;

    private TextField lotFieldAuto;
    private TextField doluCekiAuto;
    private TextField bosCekiAuto;
    private TextField netCekiAuto;
    private ObservableList<MiniPurchase> miniPurchaseList = FXCollections.observableArrayList();
    TableView<MiniPurchase> miniTable = new TableView<>(miniPurchaseList);
    private Label tedarukcuLabel = new Label("Tədarükçü:");
    private Label regionLabel = new Label("Region:");

    private TextField doluCekiManual;
    private TextField bosCekiManual;
    private TextField netCekiManual;

    private StackPane doluContainer;
    private StackPane bosContainer;
    private StackPane netContainer;

    private Label doluBox = new Label("DOLU ÇƏKİ:");
    private Label bosBox = new Label("BOS ÇƏKİ:");
    private CheckBox manualToggle;
    private HelloApplication.Purchase originalPurchase;

    private boolean canShowAlert = true;
    private final Stage primaryStage;
    private final Scene tableScene;
    private final TableView<HelloApplication.Purchase> tableView;
    private final ObservableList<HelloApplication.Purchase> purchaseList;
    private final String userRole;
    public Edit(Stage primaryStage,
                Scene tableScene,
                TableView<HelloApplication.Purchase> tableView,
                ObservableList<HelloApplication.Purchase> purchaseList, String userRole) throws IOException, InterruptedException {
        this.primaryStage = primaryStage;
        this.tableScene = tableScene;
        this.tableView = tableView;
        this.purchaseList = purchaseList;
        this.userRole = userRole;
        buildFormScene();
    }


    public Scene getFormScene() {
        return formScene;
    }

    public static class Purchase {
        private long id;
        private LocalDateTime doluTarix;
        private LocalDateTime bosTarix;
        private final String neqliyyatNomresi;
        private final String lotNomresi;
        private final String menteqe;
        private final String regionBag;
        private final String anbar;
        private final String tedarukcu;
        private final int kiseSayi;
        private final double birKiseninCekisi;
        private final int paletSayi;
        private final double birPaletinCekisi;
        private final double doluCeki;
        private final double bosCeki;
        private final double netCeki;
        private final String qeyd;

        public Purchase(long id,String neqliyyatNomresi, String lotNomresi,
                        String menteqe, String regionBag, String anbar, String tedarukcu,
                        int kiseSayi, double birKiseninCekisi,
                        double doluCeki, double bosCeki, double netCeki, String qeyd,
                        LocalDateTime doluTarix, LocalDateTime bosTarix,int paletSayi, double birPaletinCekisi) {
            this.id = id;
            this.neqliyyatNomresi = neqliyyatNomresi;
            this.lotNomresi = lotNomresi;
            this.menteqe = menteqe;
            this.regionBag = regionBag;
            this.anbar = anbar;
            this.tedarukcu = tedarukcu;
            this.kiseSayi = kiseSayi;
            this.birKiseninCekisi = birKiseninCekisi;
            this.paletSayi = paletSayi;
            this.birPaletinCekisi = birPaletinCekisi;
            this.doluCeki = doluCeki;
            this.bosCeki = bosCeki;
            this.netCeki = netCeki;
            this.qeyd = qeyd;
            this.doluTarix = doluTarix;
            this.bosTarix = bosTarix;
        }

        public long getId() { return id; }
        public LocalDateTime getDoluTarix() { return doluTarix; }
        public LocalDateTime getBosTarix() { return bosTarix; }
        public String getNeqliyyatNomresi() { return neqliyyatNomresi; }
        public String getLotNomresi() { return lotNomresi; }
        public String getMenteqe() { return menteqe; }
        public String getRegionBag() { return regionBag; }
        public String getAnbar() { return anbar; }
        public String getTedarukcu() { return tedarukcu; }
        public int getKiseSayi() { return kiseSayi; }
        public double getBirKiseninCekisi() { return birKiseninCekisi; }
        private double getBirPaletinCekisi() { return birPaletinCekisi; }
        private int getPaletSayi() { return paletSayi; }
        public double getDoluCeki() { return doluCeki; }
        public double getBosCeki() { return bosCeki; }
        public double getNetCeki() { return netCeki; }
        public String getQeyd() { return qeyd; }
    }
    Button saveButton = new Button("Yadda saxla");
    public void loadPurchase(HelloApplication.Purchase p) {
        this.originalPurchase = p;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        if ("QS".equalsIgnoreCase(p.getRegionBag())) {
            regionBox.setVisible(false);
            regionLabel.setVisible(false);
            tedarukcuLabel.setText("Alıcı:");
            doluBox.setText("BOS ÇƏKİ:");
            bosBox.setText("DOLU ÇƏKİ:");

        }
        else if ("LA".equalsIgnoreCase(p.getRegionBag())){
            regionBox.setVisible(false);
            regionLabel.setVisible(false);
            tedarukcuLabel.setText("Tədarükçü:");
            doluBox.setText("DOLU ÇƏKİ:");
            bosBox.setText("BOS ÇƏKİ:");
        }
            else {
            regionBox.setVisible(true);
            regionLabel.setVisible(true);
            tedarukcuLabel.setText("Tədarükçü:");
            doluBox.setText("DOLU ÇƏKİ:");
            bosBox.setText("BOS ÇƏKİ:");
        }

        if (p.getDoluTarix() != null) {
            tarixField.setText(p.getDoluTarix().format(formatter));
        } else {
            tarixField.setText("");
        }

        neqliyyatField.setText(p.getNeqliyyatNomresi());
        lotFieldAuto.setText(p.getLotNomresi());
        menteqeBox.setValue(p.getMenteqe());
        regionBox.setValue(p.getRegionBag());
        anbarBox.setValue(p.getAnbar());
        tedarukcuBox.setValue(p.getTedarukcu());
        kiseSayiField.setText(String.valueOf(p.getKiseSayi()));
        birKiseField.setText(trimZeros(p.getBirKiseninCekisi()));
        paletSayiField.setText(String.valueOf(p.getPaletSayi()));
        birPaletField.setText(trimZeros(p.getBirPaletinCekisi()));
        qeydArea.setText(p.getQeyd());

        manualToggle.setSelected(false);
        doluCekiAuto.setText(trimZeros(p.getDoluCeki()));

        if (p.getBosTarix() != null && !"ADMIN".equalsIgnoreCase(userRole)) {
            neqliyyatField.setDisable(true);
            tarixField.setDisable(true);
            lotFieldAuto.setDisable(true);
            kiseSayiField.setDisable(true);
            birKiseField.setDisable(true);
            paletSayiField.setDisable(true);
            birPaletField.setDisable(true);
            qeydArea.setDisable(true);
            saveButton.setDisable(true);
            menteqeBox.setDisable(true);
            manualToggle.setDisable(true);
            doluCekiAuto.setDisable(true);
            bosCekiAuto.setDisable(true);
            netCekiAuto.setDisable(true);
            regionBox.setDisable(true);
            anbarBox.setDisable(true);
            tedarukcuBox.setDisable(true);

        }
        loadMiniPurchasesForPurchase(p.getId());
    }
    private void updatePurchaseToApi(HelloApplication.Purchase purchase, long purchaseId) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL("http://localhost:8080/api/purchases/" + purchaseId);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    JavaTimeModule module = new JavaTimeModule();
                    module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
                    module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));

                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(module);
                    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                    String json = mapper.writeValueAsString(purchase);

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(json.getBytes(StandardCharsets.UTF_8));
                    }

                    int code = conn.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) {
                        System.out.println("Purchase updated successfully");
                    } else {
                        System.out.println("Failed to update purchase: HTTP " + code);
                    }

                } finally {
                    if (conn != null) conn.disconnect();
                }
                return null;
            }
        };
        new Thread(task).start();
    }




    // ------------------ UI BUILD ------------------
    private void buildFormScene() throws IOException, InterruptedException {
        GridPane formLayout = new GridPane();
        formLayout.prefWidthProperty().bind(primaryStage.widthProperty());
        formLayout.prefHeightProperty().bind(primaryStage.heightProperty());
        formLayout.setHgap(8);
        formLayout.setVgap(12);
        formLayout.setAlignment(Pos.CENTER);
        formLayout.setPadding(new Insets(30));

        Font inputFont = Font.font("Arial", 16);


        tarixField = new TextField();
        HelloApplication.styleReadOnly(tarixField);

        neqliyyatField = new TextField();
        setupNeqliyyatField(neqliyyatField);

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        HttpRequest suppliersRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/tedarukcu/all"))
                .build();
        String suppliersJson = client.send(suppliersRequest, HttpResponse.BodyHandlers.ofString()).body();
        Tedarukcu[] suppliers = mapper.readValue(suppliersJson, Tedarukcu[].class);

        HttpRequest menteqeRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/menteqe/all"))
                .build();
        String menteqeJson = client.send(menteqeRequest, HttpResponse.BodyHandlers.ofString()).body();
        Menteqe[] menteqe = mapper.readValue(menteqeJson, Menteqe[].class);

        HttpRequest regionRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/region/all"))
                .build();
        String regionJson = client.send(regionRequest, HttpResponse.BodyHandlers.ofString()).body();
        com.agrarco.agrovers.Models.Region[] region = mapper.readValue(regionJson, com.agrarco.agrovers.Models.Region[].class);

        HttpRequest anbarRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/anbar/all"))
                .build();
        String anbarJson = client.send(anbarRequest, HttpResponse.BodyHandlers.ofString()).body();
        Anbar[] anbar = mapper.readValue(anbarJson, Anbar[].class);

        menteqeBox = new ComboBox<>();
        menteqeBox.getItems().addAll(
                Arrays.stream(menteqe).map(Menteqe::getMenteqe).toList()
        );

        regionBox = new ComboBox<>();
        regionBox.getItems().addAll(
                Arrays.stream(region).map(Region::getRegion).toList()
        );

        anbarBox = new ComboBox<>();
        anbarBox.getItems().addAll(
                Arrays.stream(anbar).map(Anbar::getAnbar).toList()
        );

        tedarukcuBox = new ComboBox<>();
        tedarukcuBox.getItems().addAll(
                Arrays.stream(suppliers).map(Tedarukcu::getTedarukcu).toList()
        );


        kiseSayiField = new TextField();
        setupIntegerField(kiseSayiField);
        birKiseField = new TextField();
        setupDecimalField(birKiseField);
        paletSayiField = new TextField();
        setupIntegerField(paletSayiField);
        birPaletField = new TextField();
        setupDecimalField(birPaletField);
        qeydArea = new TextArea();

        lotFieldAuto = new TextField();
        doluCekiAuto = new TextField();
        bosCekiAuto = new TextField();
        netCekiAuto = new TextField();
        netCekiAuto.setEditable(false);

        updateNetCeki(doluCekiAuto, bosCekiAuto, netCekiAuto,paletSayiField,birPaletField,birKiseField,kiseSayiField);
        HelloApplication.styleReadOnly(lotFieldAuto);
        HelloApplication.styleReadOnly(doluCekiAuto);
        HelloApplication.styleReadOnly(bosCekiAuto);
        HelloApplication.styleReadOnly(netCekiAuto);
        bindNetCeki(doluCekiAuto, bosCekiAuto, netCekiAuto,kiseSayiField,birKiseField,birPaletField,paletSayiField);

        doluCekiManual = new TextField();
        bosCekiManual = new TextField();
        netCekiManual = new TextField();
        setupDecimalField(doluCekiManual);
        setupDecimalField(bosCekiManual);
        setupDecimalField(netCekiManual);
        HelloApplication.styleTextInput(doluCekiManual);
        HelloApplication.styleTextInput(bosCekiManual);
        HelloApplication.styleTextInput(netCekiManual);
        bindNetCeki(doluCekiManual, bosCekiManual, netCekiManual,birKiseField,kiseSayiField,paletSayiField,birPaletField);

        StackPane lotContainer = new StackPane(lotFieldAuto);
        doluContainer = new StackPane(doluCekiAuto);
        bosContainer = new StackPane(bosCekiAuto);
        netContainer = new StackPane(netCekiAuto);

        manualToggle = new CheckBox("Manual çəki");
        manualToggle.setFont(Font.font("Arial", 16));

        TextField[] fields = {neqliyyatField, kiseSayiField, birKiseField,birPaletField,paletSayiField};
        for (TextField tf : fields) {
            tf.setFont(inputFont);
            HelloApplication.styleTextInput(tf);
        }
        HelloApplication.styleTextArea(qeydArea);
        HelloApplication.styleComboBox(menteqeBox);
        HelloApplication.styleComboBox(regionBox);
        HelloApplication.styleComboBox(anbarBox);
        HelloApplication.styleComboBox(tedarukcuBox);

        int row = 0;
        formLayout.add(new Label("Tarix:"), 0, row);
        formLayout.add(tarixField, 1, row);

        formLayout.add(new Label("Nəqliyyatın nömrəsi:"), 2, row);
        formLayout.add(HelloApplication.createClearableField(neqliyyatField), 3, row);

        formLayout.add(new Label("Lot nömrəsi:"), 4, row);
        formLayout.add(lotContainer, 5, row);

        row++;
        formLayout.add(manualToggle, 5, row);

        row++;
        formLayout.add(new Label("Məntəqə:"), 0, row);
        formLayout.add(menteqeBox, 1, row);
        formLayout.add(regionLabel, 2, row);
        formLayout.add(regionBox, 3, row);

        row++;
        formLayout.add(new Label("Anbar:"), 0, row);
        formLayout.add(anbarBox, 1, row);
        formLayout.add(tedarukcuLabel, 2, row);
        formLayout.add(tedarukcuBox, 3, row);

        row++;
        formLayout.add(new Label("Kisə sayı (ədəd):"), 0, row);
        formLayout.add(HelloApplication.createClearableField(kiseSayiField), 1, row);
        formLayout.add(new Label("Bir kisənin çəkisi (q):"), 2, row);
        formLayout.add(HelloApplication.createClearableField(birKiseField), 3, row);
        row++;
        formLayout.add(new Label("Palet sayı (ədəd):"), 0, row);
        formLayout.add(HelloApplication.createClearableField(paletSayiField), 1, row);
        formLayout.add(new Label("Bir Paletin çəkisi (q):"), 2, row);
        formLayout.add(HelloApplication.createClearableField(birPaletField), 3, row);

        row++;
        formLayout.add(new Label("Qeyd:"), 0, row);
        formLayout.add(HelloApplication.createClearableField(qeydArea), 1, row, 5, 1);
        qeydArea.setMaxHeight(100);

        row++;
        formLayout.add(doluBox, 0, row);
        formLayout.add(doluContainer, 1, row);
        formLayout.add(bosBox, 2, row);
        formLayout.add(bosContainer, 3, row);
        formLayout.add(new Label("NET ÇƏKİ:"), 4, row);
        formLayout.add(netContainer, 5, row);

        MiniPurchaseTable miniTableHelper = new MiniPurchaseTable();
        VBox miniSection = miniTableHelper.createMiniPurchaseSection(
                doluCekiManual, kiseSayiField, birKiseField, paletSayiField, birPaletField
        );
        miniPurchaseList = miniTableHelper.getMiniPurchases(); // <-- link your list
        miniTable.setItems(miniPurchaseList); // make sure the TableView in Edit uses this list
        miniTable.refresh();

        row++;
        formLayout.add(miniSection, 1, row, 5, 1);

        miniSection.setVisible(true);
        manualToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                doluContainer.getChildren().setAll(doluCekiAuto);
                bosContainer.getChildren().setAll(bosCekiAuto);
                netContainer.getChildren().setAll(netCekiAuto);
                miniSection.setVisible(false);
            } else {
                doluCekiManual.clear();
                bosCekiManual.clear();
                netCekiManual.clear();
                doluContainer.getChildren().setAll(doluCekiManual);
                bosContainer.getChildren().setAll(bosCekiManual);
                netContainer.getChildren().setAll(netCekiManual);
                miniSection.setVisible(true);
            }
        });

        Button backButton = new Button("Geri");
        HelloApplication.styleButtonGrey(backButton);
        backButton.setOnAction(e -> {
            primaryStage.setScene(tableScene);
            tableView.refresh();

        });
        CASScaleReader scaleReader = HelloApplication.getScaleReader();
        if (scaleReader != null) {
            scaleReader.addWeightListener(weight ->
                    Platform.runLater(() -> bosCekiAuto.setText(weight))
            );
        }

        HelloApplication.styleButtonGreen(saveButton);
        saveButton.setOnAction(e -> {
            try {
                LocalDateTime doluTarix;
                LocalDateTime bosTarix = null;
                long purchaseId = -1;
                String lot;
                String loggedInUser;

                if (originalPurchase != null) {
                    purchaseId = originalPurchase.getId();
                    doluTarix = originalPurchase.getDoluTarix();
                    bosTarix = (originalPurchase.getBosTarix() != null) ? originalPurchase.getBosTarix() : LocalDateTime.now();
                    lot = originalPurchase.getLotNomresi();
                    loggedInUser = originalPurchase.getLoggedInUser();
                } else {
                    doluTarix = LocalDateTime.now();
                    bosTarix = null;
                    lot = fetchNewLot();
                    loggedInUser = originalPurchase.getLoggedInUser();
                }

                int kiseSayi = Integer.parseInt(kiseSayiField.getText());
                double birKise = Double.parseDouble(birKiseField.getText());
                int paletSayi = Integer.parseInt(paletSayiField.getText());
                double birPalet = Double.parseDouble(birPaletField.getText());
                double doluCeki = parseDoubleSafe(manualToggle.isSelected() ? doluCekiManual.getText() : doluCekiAuto.getText());
                double bosCeki = parseDoubleSafe(manualToggle.isSelected() ? bosCekiManual.getText() : bosCekiAuto.getText());
                double netCeki = parseDoubleSafe(manualToggle.isSelected() ? netCekiManual.getText() : netCekiAuto.getText());

                HelloApplication.Purchase purchase = new HelloApplication.Purchase(
                        loggedInUser, purchaseId,
                        neqliyyatField.getText(), lot,
                        menteqeBox.getValue(), regionBox.getValue(), anbarBox.getValue(), tedarukcuBox.getValue(),
                        kiseSayi, birKise,
                        birPalet,paletSayi,
                        doluCeki, bosCeki, netCeki, qeydArea.getText(),
                        doluTarix, bosTarix
                );

                updatePurchaseToApi(purchase, purchaseId);

                if (originalPurchase != null) {
                    int actualIndex = purchaseList.indexOf(originalPurchase);
                    if (actualIndex != -1) {
                        purchaseList.set(actualIndex, purchase);
                    }
                } else {
                    purchaseList.add(purchase);
                }

                resetFields();
                primaryStage.setScene(tableScene);

            } catch (NumberFormatException ex) {
                System.out.println("Xahiş olunur, rəqəm daxil edin");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });




        HBox buttonsBox = new HBox(20, backButton, saveButton);
        buttonsBox.setAlignment(Pos.CENTER);
        row++;
        formLayout.add(buttonsBox, 0, row, 6, 1);

        for (Node node : formLayout.getChildren()) {
            if (node instanceof Label label) {
                GridPane.setHalignment(label, HPos.RIGHT);
            }
        }

        VBox formContainer = new VBox(formLayout);
        formContainer.prefWidthProperty().bind(primaryStage.widthProperty());
        formContainer.prefHeightProperty().bind(primaryStage.heightProperty());
        formContainer.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        formScene = new Scene(formContainer);
    }
    private void loadMiniPurchasesForPurchase(long purchaseId) {
        Task<List<MiniPurchase>> task = new Task<>() {
            @Override
            protected List<MiniPurchase> call() throws Exception {
                URL url = new URL("http://localhost:8080/api/minipurchase/byPurchase/" + purchaseId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() == 200) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    JavaTimeModule module = new JavaTimeModule();
                    module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
                    module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));

                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(module);
                    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                    return Arrays.asList(mapper.readValue(conn.getInputStream(), MiniPurchase[].class));

                } else {
                    throw new RuntimeException("Failed to fetch MiniPurchases: " + conn.getResponseCode());
                }
            }
        };

        task.setOnSucceeded(e -> {
            List<MiniPurchase> miniPurchases = task.getValue();
            // ✅ Populate your table or fields
            miniPurchaseList.clear();
            miniPurchaseList.addAll(miniPurchases);
            System.out.println("Loaded " + miniPurchases.size() + " mini purchases for purchaseId=" + purchaseId);
            tableView.refresh();

        });

        task.setOnFailed(e -> task.getException().printStackTrace());
        miniTable.setItems(miniPurchaseList);

        new Thread(task).start();
    }

    private void setupIntegerField(TextField field) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) return change;
            return null;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
    }

    private void resetFields() {
        tarixField.clear();
        neqliyyatField.clear();
        kiseSayiField.clear();
        birKiseField.clear();
        paletSayiField.clear();
        birPaletField.clear();
        menteqeBox.getSelectionModel().clearSelection();
        regionBox.getSelectionModel().clearSelection();
        anbarBox.getSelectionModel().clearSelection();
        tedarukcuBox.getSelectionModel().clearSelection();
        qeydArea.clear();
        doluCekiManual.clear();
        bosCekiManual.clear();
        netCekiManual.clear();
        manualToggle.setSelected(false);
    }

    private void setupDecimalField(TextField field) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*(\\.\\d{0,2})?")) return change;
            return null;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
    }
    private String fetchNewLot() throws Exception {
        URL url = new URL("http://localhost:8080/api/purchases");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        String lot = null;
        if (conn.getResponseCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> response = mapper.readValue(conn.getInputStream(), List.class);
            lot = (String) ((Map<String, Object>) response.get(0)).get("lot");

        } else {
            throw new RuntimeException("Failed to fetch new lot: " + conn.getResponseCode());
        }

        conn.disconnect();
        return lot;
    }

    private void setupNeqliyyatField(TextField neqliyyatField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText().toUpperCase();
            text = text.replaceAll("[^A-Z0-9-]", "");
            change.setText(text);
            change.setRange(0, change.getControlText().length());
            return change;
        };

        neqliyyatField.setTextFormatter(new TextFormatter<>(filter));

        neqliyyatField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.trim().isEmpty()) return;

            if (!newVal.matches("[A-Z0-9-]*")) {
                neqliyyatField.setText(oldVal);
            }
        });
    }

    private int countDashesBefore(String text, int pos) {
        int count = 0;
        for (int i = 0; i < Math.min(pos, text.length()); i++) {
            if (text.charAt(i) == '-') count++;
        }
        return count;
    }

    private void bindNetCeki(TextField doluField, TextField bosField, TextField netField, TextField kiseSayiField, TextField birKiseField, TextField paletSayiField, TextField birPaletField) {
        doluField.textProperty().addListener((obs, oldVal, newVal) -> calculateNet(doluField, bosField, netField,birKiseField,kiseSayiField,paletSayiField,birPaletField));
        bosField.textProperty().addListener((obs, oldVal, newVal) -> calculateNet(doluField, bosField, netField,birKiseField,kiseSayiField,paletSayiField,birPaletField));
    }

    private void updateNetCeki(TextField doluField, TextField bosField, TextField netField,TextField birKiseField,TextField kiseSayiField, TextField paletSayiField, TextField birPaletField) {
        double dolu = parseDoubleSafe(doluField.getText());
        double bos = parseDoubleSafe(bosField.getText());
        double kisesayi = parseDoubleSafe(kiseSayiField.getText());
        double birkise = parseDoubleSafe(birKiseField.getText());
        double paletsayi = parseDoubleSafe(paletSayiField.getText());
        double birpalet = parseDoubleSafe(birPaletField.getText());
        netField.setText((dolu - (bos + kisesayi*(birkise/1000) + paletsayi*(birpalet/1000))) + " kg");
    }

    private void calculateNet(TextField doluField, TextField bosField, TextField netField, TextField birKiseField, TextField kiseSayiField,TextField paletSayiField, TextField birPaletField) {
        double dolu = parseDoubleSafe(doluField.getText());
        double bos = parseDoubleSafe(bosField.getText());
        double birkise = parseDoubleSafe(birKiseField.getText());
        double kisesayi = parseDoubleSafe(kiseSayiField.getText());
        double birpalet = parseDoubleSafe(birPaletField.getText());
        double paletsayi = parseDoubleSafe(paletSayiField.getText());
        double net = dolu - (bos + kisesayi*(birkise/1000) + paletsayi*(birpalet/1000));

        BigDecimal rounded = new BigDecimal(net).setScale(2, RoundingMode.HALF_UP);

        netField.setText(rounded.toString());
    }

    private double parseDoubleSafe(String text) {
        try { return Double.parseDouble(text); } catch (Exception e) { return 0; }
    }

    private String trimZeros(double v) {
        if (Math.floor(v) == v) return String.valueOf((long) v);
        BigDecimal bd = new BigDecimal(v).setScale(2, RoundingMode.HALF_UP);
        return bd.stripTrailingZeros().toPlainString();
    }
}
