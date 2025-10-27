package com.agrarco.agrovers;

import com.agrarco.agrovers.Models.Anbar;
import com.agrarco.agrovers.Models.CASScaleReader;
import com.agrarco.agrovers.Models.Menteqe;
import com.agrarco.agrovers.Models.Tedarukcu;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.agrarco.agrovers.Models.Region;
import com.itextpdf.kernel.font.PdfFontFactory;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;
import lombok.Getter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.awt.print.PrinterJob;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class HelloApplication extends Application {
    TableColumn<Purchase, String> regionCol;
    private final ObservableList<Purchase> saleList = FXCollections.observableArrayList();
    TableColumn<Purchase, String> tedarukcuCol;
    TableColumn<Purchase, String> bosCol;
    TableColumn<Purchase, String> doluCol;
    private Scene saleTableScene;
    private Scene qabiqSatishiFormScene;
    public static String jwtToken;
    private FilteredList<Purchase> filteredData;
    public static String userName;
    public static String type;
    private String tarix, neqliyyatNomresi, lotNomresi, menteqe, regionBag, anbar, tedarukcu;
    private int kiseSayi,paletSayi;
    private double birKiseninCekisi, birPaletinCekisi, doluCeki, bosCeki, netCeki;
    private String qeyd;
    @Getter
    private static CASScaleReader scaleReader;
    private TextField doluCekiAuto;
    private final ObjectMapper mapper = new ObjectMapper();
    private ComboBox<String> regionBox;
    private ComboBox<String> menteqeBox;
    private ComboBox<String> anbarBox;
    private ComboBox<String> tedarukcuBox;
    private Label tedarukcuLabel = new Label("Tədarükçü:");
    private Label regionLabel = new Label("Region:");
    private Label doluLabel = new Label("dolu");
    private ObservableList<MiniPurchase> miniPurchaseList = FXCollections.observableArrayList();


    @Override
    public void start(Stage primaryStage) throws IOException, InterruptedException {

        // ------------------ MAIN SCENE ------------------
        Button yeniAlis = new Button("Yeni alış");
        Button alisSiyahisi = new Button("Alış siyahısı");

        VBox leftBox = createStyledBox("", "img_2.png", "#2E8B57", yeniAlis, alisSiyahisi);
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.25)); // black with 25% opacity
        shadow.setRadius(10);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);

        leftBox.setEffect(shadow);
        Button yeniLepeAlis = new Button("Yeni alış");
        Button lepeAlisSiyahisi = new Button("Alış siyahısı");
        VBox middleBox = createStyledBox("", "img_3.png", "#3CC258", yeniLepeAlis, lepeAlisSiyahisi);

        Button qabiqSatis = new Button("Yeni satış");
        Button satisSiyahisi = new Button("Satış siyahısı");
        VBox rightBox = createStyledBox("", "img_1.png", "#28A745", qabiqSatis, satisSiyahisi);

        leftBox.setAlignment(Pos.TOP_CENTER);
        leftBox.setPadding(new Insets(20));
        leftBox.setMaxHeight(220);
        leftBox.setMinWidth(300);
        leftBox.setBackground(new Background(new BackgroundFill(Color.web("#F7F7F9"), new CornerRadii(10), Insets.EMPTY)));

        Label leftTitle = new Label("Qabıqlı fındıq alışı");
        leftTitle.setFont(Font.font("Arial", 28));
        leftTitle.setTextFill(Color.DARKGREEN);

        yeniAlis.getStyleClass().add("main-button");
        alisSiyahisi.getStyleClass().add("main-button");

        Button logoutButton = new Button("Çıxış");
        logoutButton.getStyleClass().add("red-button");
        logoutButton.setOnAction(e -> {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();
            stop();

            LoginPage loginPage = new LoginPage();
            Stage loginStage = new Stage();
            try {
                loginPage.start(loginStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        leftBox.getChildren().addAll(leftTitle, yeniAlis, alisSiyahisi);

// ------------------ MIDDLE BOX (Ləpə alışı) ------------------
        middleBox.setAlignment(Pos.TOP_CENTER);
        middleBox.setPadding(new Insets(20));
        middleBox.setMaxHeight(220);
        middleBox.setMinWidth(300);
        middleBox.setBackground(new Background(new BackgroundFill(Color.web("#F7F7F9"), new CornerRadii(10), Insets.EMPTY)));
        middleBox.setEffect(shadow);
        Label middleTitle = new Label("Ləpə alışı");
        middleTitle.setFont(Font.font("Arial", 28));
        middleTitle.setTextFill(Color.web("#2E8B57"));

        styleButtonGreen(yeniLepeAlis);
        styleButtonGreen(lepeAlisSiyahisi);

        middleBox.getChildren().addAll(middleTitle, yeniLepeAlis, lepeAlisSiyahisi);

// ------------------ RIGHT BOX ------------------
        rightBox.setAlignment(Pos.TOP_CENTER);
        rightBox.setPadding(new Insets(20));
        rightBox.setMaxHeight(220);
        rightBox.setMinWidth(300);
        rightBox.setBackground(new Background(new BackgroundFill(Color.web("#F7F7F9"), new CornerRadii(10), Insets.EMPTY)));
        rightBox.setEffect(shadow);
        Label rightTitle = new Label("Qabıq satışı");
        rightTitle.setFont(Font.font("Arial", 28));
        rightTitle.setTextFill(Color.web("#3CC258"));

        styleButtonLightGreen(qabiqSatis);
        styleButtonLightGreen(satisSiyahisi);

        rightBox.getChildren().addAll(rightTitle, qabiqSatis, satisSiyahisi);

// ------------------ MAIN LAYOUT ------------------
        HBox mainLayout = new HBox(20);
        mainLayout.prefWidthProperty().bind(primaryStage.widthProperty());
        mainLayout.prefHeightProperty().bind(primaryStage.heightProperty());
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(-30, 0, 0, 0));
        mainLayout.getChildren().addAll(leftBox, middleBox, rightBox);
        mainLayout.setBackground(new Background(new BackgroundFill(Color.web("#E9FFE7"), CornerRadii.EMPTY, Insets.EMPTY)));

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(mainLayout);

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setBackground(new Background(new BackgroundFill(Color.web("#E9FFE7"), CornerRadii.EMPTY, Insets.EMPTY)));
        topBar.setPadding(new Insets(20, 0, 0, 20));
        topBar.getChildren().add(logoutButton);

        mainPane.setTop(topBar);
        Scene mainScene = new Scene(mainPane);
        mainScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        // ------------------ FORM SCENE ------------------
        GridPane formLayout = new GridPane();
        formLayout.prefWidthProperty().bind(primaryStage.widthProperty());
        formLayout.prefHeightProperty().bind(primaryStage.heightProperty());
        formLayout.setHgap(8);
        formLayout.setVgap(12);
        formLayout.setAlignment(Pos.CENTER);
        formLayout.setPadding(new Insets(30));
        formLayout.setBackground(new Background(new BackgroundFill(Color.web("#E9FFE7"), new CornerRadii(10), Insets.EMPTY)));

        Font inputFont = Font.font("Arial", 16);
        Edit edit = new Edit(primaryStage, tableScene, tableView, purchaseList,loggedInRole);
        // -------------- FIELDS ----------------
        TextField tarixField = new TextField();
        TextField neqliyyatField = new TextField();
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
        Region[] region = mapper.readValue(regionJson, Region[].class);

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



        TextField kiseSayiField = new TextField();
        setupIntegerField(kiseSayiField);
        TextField birKiseField = new TextField();
        setupDecimalField(birKiseField);
        TextField birPaletField = new TextField();
        setupDecimalField(birPaletField);
        TextField paletSayiField = new TextField();
        setupIntegerField(paletSayiField);
        TextArea qeydArea = new TextArea();
        String selectedName = tedarukcuBox.getValue();

        Tedarukcu selectedSupplier = Arrays.stream(suppliers)
                .filter(t -> t.getTedarukcu().equals(selectedName))
                .findFirst()
                .orElse(null);

        if (selectedSupplier != null) {
            String supplierId = selectedSupplier.getId();
            System.out.println("Selected supplier ID: " + supplierId);
        }

        TextField doluCekiAuto = new TextField();
        doluCekiAuto.setEditable(false);

        styleReadOnly(doluCekiAuto);
        TextField doluCekiManual = new TextField();
        TextField bosCekiManual = new TextField();
        TextField netCekiManual = new TextField();
        setupDecimalField(doluCekiManual);
        setupDecimalField(bosCekiManual);
        setupDecimalField(netCekiManual);
        styleTextInput(doluCekiManual);
        styleTextInput(bosCekiManual);
        styleTextInput(netCekiManual);
        bindNetCeki(doluCekiManual, bosCekiManual, netCekiManual,kiseSayiField,birKiseField,paletSayiField,birPaletField);
        doluCekiManual.textProperty().addListener((obs, oldVal, newVal) ->
                updateNetCeki(doluCekiManual, bosCekiManual, netCekiManual,  kiseSayiField, birKiseField,paletSayiField,  birPaletField));
        bosCekiManual.textProperty().addListener((obs, oldVal, newVal) ->
                updateNetCeki(doluCekiManual, bosCekiManual, netCekiManual,  kiseSayiField, birKiseField,paletSayiField,  birPaletField));

        setupFieldValidation(kiseSayiField, true);
        setupFieldValidation(birPaletField, true);
        setupFieldValidation(paletSayiField, true);
        setupFieldValidation(birKiseField, true);
        setupFieldValidation(doluCekiAuto, true);
        setupFieldValidation(doluCekiManual, true);
        setupFieldValidation(bosCekiManual, true);

        StackPane doluContainer = new StackPane(doluCekiAuto);

        CheckBox manualToggle = new CheckBox("Manual çəki");
        manualToggle.setFont(Font.font("Arial", 16));


        TextField[] fields = {tarixField, neqliyyatField, kiseSayiField, birKiseField,paletSayiField,birPaletField};
        for (TextField tf : fields) {
            tf.setFont(inputFont);
            styleTextInput(tf);
        }
        styleTextArea(qeydArea);
        styleComboBox(menteqeBox);
        styleComboBox(regionBox);
        styleComboBox(anbarBox);
        styleComboBox(tedarukcuBox);

        styleReadOnly(tarixField);

        // ------------------ ADD FIELDS TO GRID ------------------
        int row = 0;

        formLayout.add(new Label("Nəqliyyatın nömrəsi:"), 0, row);
        formLayout.add(createClearableField(neqliyyatField), 1, row);
        formLayout.add(manualToggle, 3, row);

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
        formLayout.add(createClearableField(kiseSayiField), 1, row);
        formLayout.add(new Label("Bir kisənin çəkisi (q):"), 2, row);
        formLayout.add(createClearableField(birKiseField), 3, row);

        row++;
        formLayout.add(new Label("Palet sayı (ədəd):"), 0, row);
        formLayout.add(createClearableField(paletSayiField), 1, row);
        formLayout.add(new Label("Bir Paletin çəkisi (q):"), 2, row);
        formLayout.add(createClearableField(birPaletField), 3, row);

        row++;
        formLayout.add(new Label("Qeyd:"), 0, row);
        formLayout.add(createClearableField(qeydArea), 1, row, 5, 1);
        qeydArea.setMaxHeight(100);

        row++;
        formLayout.add(doluLabel, 0, row);
        formLayout.add(doluContainer, 1, row);

        // Create the MiniPurchase table section
        MiniPurchaseTable miniTableSection = new MiniPurchaseTable();
        VBox miniPurchasesBox = miniTableSection.createMiniPurchaseSection(
                doluCekiManual,  // or doluCekiAuto if using auto field
                kiseSayiField,
                birKiseField,
                paletSayiField,
                birPaletField
        );

        row++;
        formLayout.add(miniPurchasesBox, 0, row, 6, 1);  // span all 6 columns

        // ------------------ BUTTONS ------------------
        Button saveButton = new Button("Yadda saxla");
        Button resetButton = new Button("Reset");
        Button backButton = new Button("Geri");
        styleButtonGreen(saveButton);
        styleButtonRed(resetButton);
        styleButtonGrey(backButton);

        for (Node node : formLayout.getChildren()) {
            if (node instanceof Label label) {
                GridPane.setHalignment(label, HPos.RIGHT);
            }
        }

        HBox leftButtons = new HBox(20, backButton, saveButton);
        leftButtons.setAlignment(Pos.CENTER);

        HBox rightButton = new HBox(resetButton);
        rightButton.setAlignment(Pos.CENTER_RIGHT);

        BorderPane buttonBar = new BorderPane();
        buttonBar.setCenter(leftButtons);
        buttonBar.setRight(rightButton);
        buttonBar.setPadding(new Insets(20, 0, 20, 0));

        row++;
        formLayout.add(buttonBar, 0, row, 6, 1);

        VBox formContainer = new VBox(formLayout);
        FadeTransition formFade = new FadeTransition(Duration.seconds(0.5), formContainer);
        formFade.setFromValue(0);
        formFade.setToValue(1);
        formFade.play();

        ScaleTransition formScale = new ScaleTransition(Duration.seconds(0.5), formContainer);
        formScale.setFromX(0.95);
        formScale.setFromY(0.95);
        formScale.setToX(1);
        formScale.setToY(1);
        formScale.play();
        formContainer.prefWidthProperty().bind(primaryStage.widthProperty());
        formContainer.prefHeightProperty().bind(primaryStage.heightProperty());
        formContainer.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene formScene = new Scene(formContainer);
        formScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        BorderPane tableLayout = new BorderPane();
        tableScene = createPurchaseTableScene(primaryStage, mainScene, formScene);
        tableScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        tableLayout.prefWidthProperty().bind(primaryStage.widthProperty());
        tableLayout.prefHeightProperty().bind(primaryStage.heightProperty());

        tableLayout.setPadding(new Insets(20));
        tableLayout.setStyle("-fx-background-color: white;");

        TableView<Purchase> tableView = new TableView<>();
        tableLayout.setCenter(tableView);

        startScaleReader();
        miniPurchasesBox.setVisible(true);


        manualToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                doluContainer.getChildren().setAll(doluCekiAuto);
                miniPurchasesBox.setVisible(false);
            } else {
                doluCekiManual.clear();
                bosCekiManual.clear();
                netCekiManual.clear();
                doluContainer.getChildren().setAll(doluCekiManual);
                miniPurchasesBox.setVisible(true);
            }
        });

        alisSiyahisi.setOnAction(e -> {
            type = "FA";
            tedarukcuCol.setText("Tədarükçü");
            regionCol.setVisible(true);
            bosCol.setText("Boş çəki");
            doluCol.setText("Dolu çəki");
            doluLabel.setText("DOLU ÇƏKİ:");
            tedarukcuLabel.setText("Tədarükçü:");
            regionBox.setVisible(true);
            regionLabel.setVisible(true);
            regionBox.setValue(null);

            filteredData.setPredicate(p -> {
                String region1 = p.getRegionBag();
                if (region1 == null) return true; // keep null values
                return !(region1.contains("QS") || region1.contains("LA"));
            });

            loadPurchasesFromApi();
            primaryStage.setScene(tableScene);
        });

        lepeAlisSiyahisi.setOnAction(e -> {
            type = "LA";
            tedarukcuLabel.setText("Tədarükçü:");
            regionBox.setVisible(false);
            regionLabel.setVisible(false);
            regionBox.setValue("LA");
            tedarukcuCol.setText("Tədarükçü");
            doluLabel.setText("DOLU ÇƏKİ:");
            regionCol.setVisible(true);
            bosCol.setText("Dolu çəki");
            doluCol.setText("Boş çəki");

            filteredData.setPredicate(p -> {
                String region1 = p.getRegionBag();
                if (region1 == null) return true; // keep null values
                return region1.contains("LA");
            });

            loadPurchasesFromApi();
            primaryStage.setScene(tableScene);
        });

        satisSiyahisi.setOnAction(e -> {
            type = "QS";
            tedarukcuCol.setText("Alıcı");
            regionCol.setVisible(false);
            bosCol.setText("Dolu çəki");
            doluCol.setText("Boş çəki");
            doluLabel.setText("BOS ÇƏKİ:");
            tedarukcuLabel.setText("Alıcı:");
            regionBox.setVisible(false);
            regionLabel.setVisible(false);
            regionBox.setValue("QS");


            filteredData.setPredicate(p -> {
                String region1 = p.getRegionBag();
                return region1 != null && region1.contains("QS");
            });

            loadPurchasesFromApi();
            primaryStage.setScene(tableScene);
        });




        // ------------------ BUTTON ACTIONS ------------------
        yeniAlis.setOnAction(e -> {
            primaryStage.setScene(formScene);
            type = "FA";
            tedarukcuLabel.setText("Tədarükçü:");
            regionBox.setVisible(true);
            regionLabel.setVisible(true);
            regionBox.setValue(null);
            tedarukcuCol.setText("Tədarükçü");
            doluLabel.setText("DOLU ÇƏKİ:");
            regionCol.setVisible(true);
            bosCol.setText("Boş çəki");
            doluCol.setText("Dolu çəki");

            filteredData.setPredicate(p -> {
                String region1 = p.getRegionBag();
                if (region1 == null) return true; // keep null values
                return !(region1.contains("QS") || region1.contains("LA"));
            });
        });
        yeniLepeAlis.setOnAction(e -> {
            primaryStage.setScene(formScene);
            type = "LA";
            tedarukcuLabel.setText("Tədarükçü:");
            regionBox.setVisible(false);
            regionLabel.setVisible(false);
            regionBox.setValue("LA");
            tedarukcuCol.setText("Tədarükçü");
            doluLabel.setText("DOLU ÇƏKİ:");
            regionCol.setVisible(true);
            bosCol.setText("Boş çəki");
            doluCol.setText("Dolu çəki");

            filteredData.setPredicate(p -> {
                String region1 = p.getRegionBag();
                if (region1 == null) return true; // keep null values
                return region1.contains("LA");
            });
        });
        qabiqSatis.setOnAction(e -> {
            primaryStage.setScene(formScene);
            primaryStage.setMaximized(true);
            type = "QS";
            tedarukcuLabel.setText("Alıcı:");
            regionBox.setVisible(false);
            regionLabel.setVisible(false);
            regionBox.setValue("QS");
            tedarukcuCol.setText("Alıcı");
            regionCol.setVisible(false);
            doluLabel.setText("BOS ÇƏKİ:");
            bosCol.setText("Dolu çəki");
            doluCol.setText("Boş çəki");


            filteredData.setPredicate(p -> {
                String region1 = p.getRegionBag();
                return region1 != null && region1.contains("QS");
            });


        });
        backButton.setOnAction(e -> {
            tarixField.clear();
            neqliyyatField.clear();
            kiseSayiField.clear();
            birKiseField.clear();
            paletSayiField.clear();
            miniPurchaseList.clear();
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
            primaryStage.setScene(mainScene);
        });

        saveButton.setOnAction(e -> {
            try {
                LocalDateTime doluTarix = LocalDateTime.now();
                LocalDateTime bosTarix = null;
                String lot = fetchNewLot();

                Purchase purchase = new Purchase(
                        loggedInName, -1,
                        neqliyyatField.getText(), lot,
                        menteqeBox.getValue(), regionBox.getValue(), anbarBox.getValue(), tedarukcuBox.getValue(),
                        Integer.parseInt(kiseSayiField.getText()),
                        Double.parseDouble(birKiseField.getText()),
                        Double.parseDouble(birPaletField.getText()),
                        Integer.parseInt(paletSayiField.getText()),
                        parseDoubleSafe(manualToggle.isSelected() ? doluCekiManual.getText() : doluCekiAuto.getText()),
                        parseDoubleSafe(manualToggle.isSelected() ? bosCekiManual.getText() : "0.0"),
                        parseDoubleSafe(manualToggle.isSelected() ? netCekiManual.getText() : "0.0"),
                        qeydArea.getText(), doluTarix, bosTarix
                );

                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        // 1️⃣ Send main Purchase and get only the backend-generated ID
                        long purchaseId = sendPurchaseToApi(purchase); // now returns long

                        // 2️⃣ Create MiniPurchase and attach correct Purchase ID
                        MiniPurchase miniPurchase = new MiniPurchase(
                                0,
                                purchaseId, // ✅ Use real backend ID
                                doluTarix,
                                parseDoubleSafe(manualToggle.isSelected() ? doluCekiManual.getText() : doluCekiAuto.getText()),
                                Integer.parseInt(kiseSayiField.getText()),
                                Double.parseDouble(birKiseField.getText()),
                                Integer.parseInt(paletSayiField.getText()),
                                Double.parseDouble(birPaletField.getText())
                        );

                        System.out.println("===== MiniPurchase Data =====");
                        System.out.println("purchaseId: " + miniPurchase.getPurchaseId());
                        System.out.println("=============================");

                        // 3️⃣ Send MiniPurchase to backend
                        sendMiniPurchaseToApi(miniPurchase);

                        // 4️⃣ Keep it in memory
                        miniPurchaseList.add(miniPurchase);

                        return null;
                    }
                };

                task.setOnSucceeded(ev -> {
                    // Clear UI fields
                    tarixField.clear();
                    neqliyyatField.clear();
                    kiseSayiField.clear();
                    birKiseField.clear();
                    birPaletField.clear();
                    paletSayiField.clear();
                    menteqeBox.getSelectionModel().clearSelection();
                    regionBox.getSelectionModel().clearSelection();
                    anbarBox.getSelectionModel().clearSelection();
                    tedarukcuBox.getSelectionModel().clearSelection();
                    qeydArea.clear();
                    doluCekiManual.clear();
                    bosCekiManual.clear();
                    netCekiManual.clear();
                    miniPurchaseList.clear();
                    manualToggle.setSelected(false);

                    System.out.println("Məlumat yadda saxlanıldı: Lot=" + lot + ", Nəqliyyat=" + neqliyyatField.getText());
                    primaryStage.setScene(tableScene);
                    loadPurchasesFromApi();
                });

                task.setOnFailed(ev -> task.getException().printStackTrace());

                new Thread(task).start();

            } catch (NumberFormatException ex) {
                System.out.println("Xahiş olunur, rəqəm daxil edin");
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            System.out.println("menteqeBox value: " + menteqeBox.getValue() + " (" + menteqeBox.getValue().getClass() + ")");
            System.out.println("regionBox value: " + regionBox.getValue() + " (" + regionBox.getValue().getClass() + ")");
            System.out.println("anbarBox value: " + anbarBox.getValue() + " (" + anbarBox.getValue().getClass() + ")");
            System.out.println("tedarukcuBox value: " + tedarukcuBox.getValue() + " (" + tedarukcuBox.getValue().getClass() + ")");
        });



        resetButton.setOnAction(e -> {
            tarixField.clear();
            neqliyyatField.clear();
            kiseSayiField.clear();
            birKiseField.clear();
            birPaletField.clear();
            paletSayiField.clear();
            menteqeBox.getSelectionModel().clearSelection();
            regionBox.getSelectionModel().clearSelection();
            anbarBox.getSelectionModel().clearSelection();
            tedarukcuBox.getSelectionModel().clearSelection();
            qeydArea.clear();
            miniPurchaseList.clear();
            miniPurchasesBox.setVisible(false);

            doluCekiManual.clear();
            bosCekiManual.clear();
            netCekiManual.clear();
        });

        primaryStage.setTitle("Fındıq Alışı");
        primaryStage.setScene(mainScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), mainLayout);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        ScaleTransition pop = new ScaleTransition(Duration.seconds(0.5), mainLayout);
        pop.setFromX(0.95);
        pop.setFromY(0.95);
        pop.setToX(1);
        pop.setToY(1);
        pop.play();

    }

    private boolean canShowAlertInt = true;
    private TableView<Purchase> tableView;
    private Scene tableScene;

    private String loggedInName;
    private String loggedInRole;
    public HelloApplication(String name,String role) {
        this.loggedInName = name;
        this.loggedInRole = role;
    }

    private void setupIntegerField(TextField field) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
    }

    public static class Purchase {
        @Getter
        private final String loggedInUser;
        @Getter
        private final long id;
        @Getter
        private final String neqliyyatNomresi;
        @Getter
        private final String lotNomresi;
        @Getter
        private final String menteqe;
        @Getter
        private final String regionBag;
        @Getter
        private final String anbar;
        @Getter
        private final String tedarukcu;
        @Getter
        private final int kiseSayi;
        @Getter
        private final double birKiseninCekisi;
        @Getter
        private final double doluCeki;
        @Getter
        private final double bosCeki;
        @Getter
        private final double netCeki;
        @Getter
        private final double birPaletinCekisi;
        @Getter
        private final int paletSayi;
        @Getter
        private final String qeyd;
        @Getter
        private final LocalDateTime doluTarix;
        @Getter
        private LocalDateTime bosTarix;
        private boolean locked;

        @JsonCreator
        public Purchase(
                @JsonProperty("LoggedInUser") String loggedInUser,
                @JsonProperty("id") long id,
                @JsonProperty("neqliyyatNomresi") String neqliyyatNomresi,
                @JsonProperty("lotNomresi") String lotNomresi,
                @JsonProperty("menteqe") String menteqe,
                @JsonProperty("regionBag") String regionBag,
                @JsonProperty("anbar") String anbar,
                @JsonProperty("tedarukcu") String tedarukcu,
                @JsonProperty("kiseSayi") int kiseSayi,
                @JsonProperty("birKiseninCekisi") double birKiseninCekisi,
                @JsonProperty("birPaletinCekisi") double birPaletinCekisi,
                @JsonProperty("paletSayi") int paletSayi,
                @JsonProperty("doluCeki") double doluCeki,
                @JsonProperty("bosCeki") double bosCeki,
                @JsonProperty("netCeki") double netCeki,
                @JsonProperty("qeyd") String qeyd,
                @JsonProperty("doluTarix") LocalDateTime doluTarix,
                @JsonProperty("bosTarix") LocalDateTime bosTarix
        ) {
            this.loggedInUser = loggedInUser;
            this.id = id;
            this.neqliyyatNomresi = neqliyyatNomresi.trim();
            this.lotNomresi = lotNomresi;
            this.menteqe = menteqe.trim();
            this.regionBag = regionBag.trim();
            this.anbar = anbar.trim();
            this.tedarukcu = tedarukcu.trim();
            this.kiseSayi = kiseSayi;
            this.birKiseninCekisi = birKiseninCekisi;
            this.birPaletinCekisi = birPaletinCekisi;
            this.paletSayi = paletSayi;
            this.doluCeki = doluCeki;
            this.bosCeki = bosCeki;
            this.netCeki = netCeki;
            this.qeyd = qeyd.trim();
            this.doluTarix = doluTarix;
            this.bosTarix = bosTarix;
        }
    }
    private final ObservableList<Purchase> purchaseList = FXCollections.observableArrayList();

    private void setupDecimalField(TextField field) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        };
        field.setTextFormatter(new TextFormatter<>(filter));
    }

    private void setupFieldValidation(TextField textField, boolean requireDecimal) {
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (requireDecimal) {
                if (!newVal.matches("\\d*(\\.\\d*)?")) {
                    triggerAlert("Bu sahəyə yalnız rəqəm daxil edilə bilər.");
                    textField.setText(oldVal);
                }
            } else {
                    if (newVal.trim().isEmpty()) {
                    triggerAlert("Bu sahə boş ola bilməz.");
                }
            }
        });
    }

    private long sendPurchaseToApi(Purchase purchase) throws Exception {
        URL url = new URL("http://localhost:8080/api/purchases");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // ObjectMapper with LocalDateTime formatting
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        JavaTimeModule module = new JavaTimeModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // --- Send Purchase ---
        String json = mapper.writeValueAsString(purchase);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        if (code == 200 || code == 201) {
            try (InputStream is = conn.getInputStream()) {
                // Read raw JSON response
                String response = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                System.out.println("Raw backend response: " + response);

                // Parse only the 'id' field
                JsonNode node = mapper.readTree(response);
                long purchaseId = node.get("id").asLong();
                System.out.println("✅ Purchase saved successfully, ID = " + purchaseId);
                return purchaseId;
            }
        } else {
            throw new RuntimeException("Failed to save Purchase: HTTP " + code);
        }
    }


    private void sendMiniPurchaseToApi(MiniPurchase miniPurchase) {
        try {
            URL url = new URL("http://localhost:8080/api/minipurchase/add");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // ObjectMapper with date handling
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            JavaTimeModule module = new JavaTimeModule();
            module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(module);
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // --- Send MiniPurchase ---
            String json = mapper.writeValueAsString(miniPurchase);
            System.out.println("Sending MiniPurchase JSON to API: " + json);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            if (code == 200 || code == 201) {
                System.out.println("✅ MiniPurchase saved to DB successfully");
            } else {
                System.out.println("❌ Failed to save MiniPurchase: HTTP " + code);
            }

            conn.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void triggerAlert(String message) {
        if (!canShowAlert) return;
        canShowAlert = false;

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Xəta");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();

        PauseTransition cooldown = new PauseTransition(Duration.seconds(30));
        cooldown.setOnFinished(e -> canShowAlert = true);
        cooldown.play();
    }

    private boolean canShowAlert = true;

    private VBox createStyledBox(String titleText, String imagePath, String colorHex, Button... buttons) {
        VBox box = new VBox(15);
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(20));
        box.setMinWidth(300);
        box.setMaxHeight(220);

        // Create a Label as the title
        Label title = new Label(titleText);
        title.setFont(Font.font("Arial", 28));
        title.setTextFill(Color.WHITE); // Title in white for contrast

        // Add buttons
        for (Button btn : buttons) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;"); // optional style
        }

        // StackPane to put background image behind content
        StackPane stack = new StackPane();

        // 🔥 Load background image safely
        javafx.scene.image.Image bgImage = null;
        try {
            bgImage = new javafx.scene.image.Image(getClass().getResource(imagePath).toExternalForm());
        } catch (Exception e) {
            System.err.println("⚠️ Could not load image: " + imagePath);
        }

// Create ImageView and add to StackPane
        javafx.scene.image.ImageView bg = new javafx.scene.image.ImageView();
        if (bgImage != null) {
            bg.setImage(bgImage);
            bg.setFitWidth(300);   // adjust width
            bg.setFitHeight(220);  // adjust height
            bg.setPreserveRatio(true);
        }

        javafx.scene.layout.StackPane stack1 = new javafx.scene.layout.StackPane();
        stack1.getChildren().add(bg);
        stack1.setMaxWidth(300);
        stack1.setMaxHeight(220);
        Rectangle clip = new Rectangle(bg.getFitWidth(), bg.getFitHeight());
        clip.setArcWidth(30);   // corner radius X
        clip.setArcHeight(30);  // corner radius Y
        bg.setClip(clip);

        // Overlay a colored semi-transparent layer for better readability
        Rectangle overlay = new Rectangle(300, 220, Color.web("transparent"));
        overlay.setArcWidth(20);
        overlay.setArcHeight(20);
        bg.setPreserveRatio(false);

        VBox content = new VBox(10, title);
        content.getChildren().addAll(buttons);
        content.setAlignment(Pos.TOP_CENTER);

        stack1.getChildren().addAll(overlay, content);

        box.getChildren().add(stack1);
        return box;
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

    private Scene createPurchaseTableScene(Stage primaryStage, Scene mainScene, Scene formScene) {
        if (tableView == null) {
            tableView = new TableView<>(purchaseList);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        TableColumn<Purchase, String> lotCol = new TableColumn<>("Lot");
        lotCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLotNomresi()));

        TableColumn<Purchase, String> neqliyyatCol = new TableColumn<>("Nəqliyyat");
        neqliyyatCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNeqliyyatNomresi()));

        TableColumn<Purchase, String> doluTarixCol = new TableColumn<>("Dolu Tarix");
        doluTarixCol.setCellValueFactory(data -> {
            LocalDateTime dt = data.getValue().getDoluTarix();
            return new SimpleStringProperty(dt != null ? dt.format(formatter) : "-");
        });

        TableColumn<Purchase, String> bosTarixCol = new TableColumn<>("Boş Tarix");
        bosTarixCol.setCellValueFactory(data -> {
            LocalDateTime bt = data.getValue().getBosTarix();
            return new SimpleStringProperty(bt != null ? bt.format(formatter) : "           -");
        });
        doluCol = new TableColumn<>("Dolu çəki");
        doluCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getDoluCeki()).asObject().asString());

        bosCol = new TableColumn<>("Boş çəki");
        bosCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getBosCeki()).asObject().asString());

        TableColumn<Purchase, Double> netCol = new TableColumn<>("Net çəki");
        netCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getNetCeki()).asObject());

        TableColumn<Purchase, Integer> kiseCol = new TableColumn<>("Kisə sayı");
        kiseCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getKiseSayi()).asObject());

        TableColumn<Purchase, Double> birKiseCol = new TableColumn<>("Bir kisənin çəkisi");
        birKiseCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getBirKiseninCekisi()).asObject());

        TableColumn<Purchase, Integer> paletCol = new TableColumn<>("Palet sayı");
        paletCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPaletSayi()).asObject());

        TableColumn<Purchase, Double> birPaletCol = new TableColumn<>("Bir paletin çəkisi");
        birPaletCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getBirPaletinCekisi()).asObject());

        TableColumn<Purchase, String> menteqeCol = new TableColumn<>("Məntəqə");
        menteqeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMenteqe()));

        regionCol = new TableColumn<>("Region/Bağ");
        regionCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRegionBag()));

        TableColumn<Purchase, String> anbarCol = new TableColumn<>("Anbar");
        anbarCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAnbar()));
        if (!"FA".equalsIgnoreCase(type)) {
            tedarukcuCol = new TableColumn<>("Tədarükçü");
        }
        else if(!"QS".equalsIgnoreCase(type)){
            tedarukcuCol = new TableColumn<>("Alıcı");
        }
        tedarukcuCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTedarukcu()));

        TableColumn<Purchase, String> userCol = new TableColumn<>("İstifadəçi");
        userCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLoggedInUser()));

        TableColumn<Purchase, String> qeydCol = new TableColumn<>("Qeyd");
        qeydCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getQeyd()));

        tableView.getColumns().setAll(lotCol, neqliyyatCol, doluTarixCol, bosTarixCol, doluCol, bosCol, netCol, kiseCol, birKiseCol,paletCol,birPaletCol, menteqeCol, regionCol, anbarCol, tedarukcuCol, qeydCol,userCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        filteredData = new FilteredList<>(purchaseList, p -> true);
        tableView.setItems(filteredData);

        Button btnYeni = new Button("Yeni");
        Button btnSil = new Button("Sil");
        Button btnTarix = new Button("Alış Tarixi");
        styleButtonGreen(btnYeni);
        styleButtonRed(btnSil);
        styleButtonGrey(btnTarix);
        if (!"ADMIN".equalsIgnoreCase(loggedInRole)) {
            btnSil.setVisible(false);
        }

        btnYeni.setOnAction(e ->
        {
            primaryStage.setScene(formScene);

        });

        btnSil.setOnAction(e -> {
            Purchase selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
                confirm.setTitle("Təsdiq");
                confirm.setHeaderText("Silmək istədiyinizə əminsiniz?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        try {
                            long id = selected.getId();

                            URL url = new URL("http://localhost:8080/api/purchases/" + id);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("DELETE");
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.connect();

                            int status = connection.getResponseCode();
                            if (status == 204) {
                                purchaseList.remove(selected);
                                new Alert(Alert.AlertType.INFORMATION, "Məlumat uğurla silindi!").show();
                            } else {
                                new Alert(Alert.AlertType.ERROR, "Silinmə uğursuz oldu! Kod: " + status).show();
                            }

                            connection.disconnect();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            new Alert(Alert.AlertType.ERROR, "Server ilə əlaqə qurulmadı!").show();
                        }
                    }
                });
            }
        });

        tableView.setRowFactory(tv -> {
            TableRow<Purchase> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Purchase selectedPurchase = row.getItem();
                    Edit editPage = null;
                    try {
                        editPage = new Edit(primaryStage, tableScene, tableView, purchaseList,loggedInRole);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    editPage.loadPurchase(selectedPurchase);
                    primaryStage.setScene(editPage.getFormScene());
                }
            });

            Runnable updateStyle = () -> {
                Purchase item = row.getItem();
                if (item != null && item.getBosTarix() != null) {
                    if (row.isSelected()) {
                        row.setStyle("-fx-background-color: #61c365; -fx-text-fill: white;");
                    } else {
                        row.setStyle("-fx-background-color: #ceffbe; -fx-text-fill: black;");
                    }
                } else {
                    row.setStyle("");
                }
            };

            row.itemProperty().addListener((obs, oldItem, newItem) -> updateStyle.run());
            row.selectedProperty().addListener((obs, oldSel, newSel) -> updateStyle.run());

            return row;
        });

        btnTarix.setOnAction(e -> {
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Tarixə görə filter");

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            DatePicker startDatePicker = new DatePicker();
            DatePicker endDatePicker = new DatePicker();

            grid.add(new Label("Başlanğıc:"), 0, 0);
            grid.add(startDatePicker, 1, 0);
            grid.add(new Label("Son:"), 0, 1);
            grid.add(endDatePicker, 1, 1);

            HBox quickButtons = new HBox(8);
            quickButtons.setAlignment(Pos.CENTER_LEFT);

            Button btnBugun = new Button("Bugün");
            Button btnDunen = new Button("Dünən");
            Button btn7Gun = new Button("Son 7 gün");
            Button btn30Gun = new Button("Son 30 gün");
            Button btnHamisi = new Button("Hamısı");

            quickButtons.getChildren().addAll(btnBugun, btnDunen, btn7Gun, btn30Gun, btnHamisi);
            grid.add(quickButtons, 0, 2, 2, 1);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(btn -> {
                LocalDate today = LocalDate.now();

                if (btn == ButtonType.OK) {
                    filteredData.setPredicate(p -> {
                        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null)
                            return true;
                        return !p.getDoluTarix().toLocalDate().isBefore(startDatePicker.getValue())
                                && !p.getDoluTarix().toLocalDate().isAfter(endDatePicker.getValue());
                    });

                }

                return null;
            });

            btnBugun.setOnAction(ev -> {
                dialog.close();
                LocalDate today = LocalDate.now();

                filteredData.setPredicate(p -> {
                    boolean dateOk = p.getDoluTarix() != null &&
                            p.getDoluTarix().toLocalDate().isEqual(today);

                    boolean typeOk = true;
                    String marker = p.getRegionBag();
                    if ("FA".equals(type)) {
                        typeOk = (marker == null || !marker.contains("QS"));
                    } else if ("QS".equals(type)) {
                        typeOk = (marker != null && marker.contains("QS"));
                    }

                    return dateOk && typeOk;
                });
            });

            btnDunen.setOnAction(ev -> {
                dialog.close();
                LocalDate yesterday = LocalDate.now().minusDays(1);

                filteredData.setPredicate(p -> {
                    boolean dateOk = p.getDoluTarix() != null &&
                            p.getDoluTarix().toLocalDate().isEqual(yesterday);

                    boolean typeOk = true;
                    String marker = p.getRegionBag();
                    if ("FA".equals(type)) {
                        typeOk = (marker == null || !marker.contains("QS"));
                    } else if ("QS".equals(type)) {
                        typeOk = (marker != null && marker.contains("QS"));
                    }

                    return dateOk && typeOk;
                });
            });

            btn7Gun.setOnAction(ev -> {
                dialog.close();
                LocalDate today = LocalDate.now();
                LocalDate sevenDaysAgo = today.minusDays(6);

                filteredData.setPredicate(p -> {
                    boolean dateOk = false;
                    if (p.getDoluTarix() != null) {
                        LocalDate d = p.getDoluTarix().toLocalDate();
                        dateOk = !d.isBefore(sevenDaysAgo) && !d.isAfter(today);
                    }

                    boolean typeOk = true;
                    String marker = p.getRegionBag();
                    if ("FA".equals(type)) {
                        typeOk = (marker == null || !marker.contains("QS"));
                    } else if ("QS".equals(type)) {
                        typeOk = (marker != null && marker.contains("QS"));
                    }

                    return dateOk && typeOk;
                });
            });

            btn30Gun.setOnAction(ev -> {
                dialog.close();
                LocalDate today = LocalDate.now();
                LocalDate thirtyDaysAgo = today.minusDays(29);

                filteredData.setPredicate(p -> {
                    boolean dateOk = false;
                    if (p.getDoluTarix() != null) {
                        LocalDate d = p.getDoluTarix().toLocalDate();
                        dateOk = !d.isBefore(thirtyDaysAgo) && !d.isAfter(today);
                    }

                    boolean typeOk = true;
                    String marker = p.getRegionBag();
                    if ("FA".equals(type)) {
                        typeOk = (marker == null || !marker.contains("QS"));
                    } else if ("QS".equals(type)) {
                        typeOk = (marker != null && marker.contains("QS"));
                    }

                    return dateOk && typeOk;
                });
            });

            btnHamisi.setOnAction(ev -> {
                dialog.close();

                filteredData.setPredicate(p -> {
                    boolean typeOk = true;
                    String marker = p.getRegionBag();
                    if ("FA".equals(type)) {
                        typeOk = (marker == null || !marker.contains("QS"));
                    } else if ("QS".equals(type)) {
                        typeOk = (marker != null && marker.contains("QS"));
                    }
                    return typeOk;
                });
            });


            dialog.showAndWait();
        });


        Button exportButton = new Button("Çap");
        styleButtonGrey(exportButton);
        exportButton.setOnAction(e -> {
            HelloApplication.Purchase selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                System.out.println("No row selected!");
                return;
            }

            ObservableList<HelloApplication.Purchase> oneItemList = FXCollections.observableArrayList(selected);
            byte[] pdfBytes = exportPurchasesToPDF(oneItemList);

            if (pdfBytes != null) {
                try {
                    showPDFViewer(pdfBytes, primaryStage);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        HBox topPanel = new HBox(15, btnYeni, btnSil, btnTarix, exportButton);
        topPanel.setPadding(new Insets(10));
        topPanel.setAlignment(Pos.CENTER_LEFT);

        Button logoutButton = new Button("Çıxış");
        logoutButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 14;");

        Button backButton = new Button("Geri");
        styleButtonGrey(backButton);
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        HBox bottomPanel = new HBox(backButton);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setAlignment(Pos.CENTER);

        Label rowCountLabel = new Label();
        Label totalNetCekiLabel = new Label();

        Runnable updateCounters = () -> {
            int rowCount = tableView.getItems().size();
            double totalNet = tableView.getItems().stream()
                    .mapToDouble(Purchase::getNetCeki)
                    .sum();
            rowCountLabel.setText("Sayı: " + rowCount);
            totalNetCekiLabel.setText("Cəm Net: " + String.format("%.2f", totalNet));
        };

        filteredData.addListener((ListChangeListener<Purchase>) c -> updateCounters.run());
        filteredData.predicateProperty().addListener((obs, oldV, newV) -> updateCounters.run());
        tableView.getItems().addListener((ListChangeListener<Purchase>) c -> updateCounters.run());
        updateCounters.run();

        HBox bottomPanel1 = new HBox();
        bottomPanel1.setPadding(new Insets(10));
        bottomPanel1.setAlignment(Pos.CENTER);

        HBox.setHgrow(backButton, Priority.NEVER);
        HBox.setHgrow(rowCountLabel, Priority.ALWAYS);
        HBox.setHgrow(totalNetCekiLabel, Priority.ALWAYS);

        javafx.scene.layout.Region spacerLeft = new javafx.scene.layout.Region();
        javafx.scene.layout.Region spacerRight = new javafx.scene.layout.Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        bottomPanel1.getChildren().addAll(rowCountLabel, spacerLeft, backButton, spacerRight, totalNetCekiLabel);

        BorderPane layout = new BorderPane();
        layout.setTop(topPanel);
        layout.setCenter(tableView);
        layout.setBottom(bottomPanel1);
        layout.setPadding(new Insets(55));
        layout.setStyle("-fx-background-color: #E9FFE7;");
        layout.prefWidthProperty().bind(primaryStage.widthProperty());
        layout.prefHeightProperty().bind(primaryStage.heightProperty());

        return new Scene(layout);
    }
    private byte[] exportPurchasesToPDF(ObservableList<HelloApplication.Purchase> purchases) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont font = PdfFontFactory.createFont("C:/Windows/Fonts/arial.ttf", PdfEncodings.IDENTITY_H, true);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            DeviceRgb yellowColor = new DeviceRgb(0xEF, 0xE4, 0xB0);

            for (HelloApplication.Purchase p : purchases) {
                if (p.getBosTarix() == null) continue;

                float[] colWidths = {2, 1, 1, 1, 1};
                Table table = new Table(UnitValue.createPercentArray(colWidths)).useAllAvailableWidth();
                table.setMarginBottom(24);

                AtomicInteger cellsAdded = new AtomicInteger();
                ImageData imageData = ImageDataFactory.create("a.jpg");
                Image img = new Image(imageData).setAutoScale(true).setHorizontalAlignment(HorizontalAlignment.CENTER);
                Cell photoCell = new Cell(1, 2).add(img)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(40);
                table.addCell(photoCell);
                cellsAdded.addAndGet(5);
                Cell standaloneTitle = new Cell(1, 3)
                        .add(new Paragraph("Məhsul çəki sənədi").setFont(font).setBold())
                        .setBackgroundColor(yellowColor)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(40);
                table.addCell(standaloneTitle);
                cellsAdded.addAndGet(5);

                List<String> titles = Arrays.asList(
                        "Məhsul novü", "Lot nomrəsi", "Nəqliyyat nomrəsi","Məntəqə","Kisə sayı","Tarix", "Dolu Çəki","Boş Çəki", "Net Çəki", "Bir Kisənin Çəkisi (q)"

                );

                List<String> values = Arrays.asList(
                        "Qabığlı Fındıq alısı", p.getLotNomresi(), p.getNeqliyyatNomresi(),
                        p.getMenteqe(),String.valueOf(p.getKiseSayi()),p.getDoluTarix() != null ? p.getDoluTarix().format(formatter) : "-",
                        trimZeros(p.getDoluCeki()), trimZeros(p.getBosCeki()), trimZeros(p.getNetCeki()),trimZeros(p.getBirPaletinCekisi()), trimZeros(p.getBirKiseninCekisi())
                );

                int index = 0;
                while (index < titles.size()) {
                    for (int i = 0; i < 5; i++) {
                        String text = (index + i < titles.size()) ? titles.get(index + i) : "";
                        Cell titleCell = new Cell().add(new Paragraph(text).setFont(font).setBold())
                                .setBackgroundColor(yellowColor)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                                .setBorder(new SolidBorder(1))
                                .setHeight(30).setFontSize(10);
                        table.addCell(titleCell);
                        cellsAdded.incrementAndGet();
                    }

                    for (int i = 0; i < 5; i++) {
                        String text = (index + i < values.size() && values.get(index + i) != null)
                                ? values.get(index + i)
                                : "";

                        Cell valueCell = new Cell().add(new Paragraph(text).setFont(font))
                                .setTextAlignment(TextAlignment.CENTER)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                                .setBorder(new SolidBorder(1))
                                .setHeight(30)
                                .setFontSize(10);

                        table.addCell(valueCell);
                        cellsAdded.incrementAndGet();
                    }


                    index += 5;
                }

                table.addCell(new Cell(1, 1).add(new Paragraph("Tədarükçünün adı və imzası")
                                .setFont(font).setBold().setFontSize(10))
                        .setBackgroundColor(yellowColor)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30));

                table.addCell(new Cell(1, 2).add(new Paragraph("Tərəzi operatorun adı və imzası")
                                .setFont(font).setBold().setFontSize(10))
                        .setBackgroundColor(yellowColor)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30));

                table.addCell(new Cell(1, 2).add(new Paragraph("Mühafizəsi adı və imzası")
                                .setFont(font).setBold().setFontSize(10))
                        .setBackgroundColor(yellowColor)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30));

                table.addCell(new Cell(1, 1).add(new Paragraph(p.getTedarukcu())
                                .setFont(font).setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30));

                table.addCell(new Cell(1, 2).add(new Paragraph(p.getLoggedInUser())
                                .setFont(font).setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30));

                table.addCell(new Cell(1, 2).add(new Paragraph("")
                                .setFont(font).setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30));
                table.addCell(new Cell(1, 1).add(new Paragraph("")
                                .setFont(font).setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30));
                table.addCell(new Cell(1, 2).add(new Paragraph("")
                                .setFont(font).setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30));
                table.addCell(new Cell(1, 2).add(new Paragraph("")
                                .setFont(font).setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30));



                Cell qeydTitle = new Cell(1, 2).add(new Paragraph("Qeyd").setFont(font).setFontSize(10).setBold())
                        .setBackgroundColor(yellowColor)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30);
                table.addCell(qeydTitle);
                cellsAdded.addAndGet(5);

                Cell surucu = new Cell(1, 2)
                        .add(new Paragraph("Sürücü adı").setFont(font).setBold().setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBackgroundColor(yellowColor)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30);
                table.addCell(surucu);
                cellsAdded.addAndGet(5);

                Cell surucuValue = new Cell(1, 2)
                        .add(new Paragraph("Sürücü imzası").setFont(font).setBold().setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBackgroundColor(yellowColor)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30);
                table.addCell(surucuValue);

                Cell qeydValue = new Cell(1, 2).add(new Paragraph(p.getQeyd()).setFont(font).setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30);
                table.addCell(qeydValue);
                cellsAdded.addAndGet(5);

                Cell empty = new Cell(1, 2)
                        .add(new Paragraph("").setFont(font).setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30);
                table.addCell(empty);
                cellsAdded.addAndGet(5);

                Cell empty1 = new Cell(1, 1)
                        .add(new Paragraph("").setFont(font).setFontSize(10))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(new SolidBorder(1))
                        .setHeight(30);
                table.addCell(empty1);
                cellsAdded.addAndGet(5);

                cellsAdded.addAndGet(5);

                while (cellsAdded.get() < 25) {
                    Cell emptyCell = new Cell().add(new Paragraph("").setFont(font))
                            .setBorder(new SolidBorder(1))
                            .setHeight(30);
                    table.addCell(emptyCell);
                    cellsAdded.incrementAndGet();
                }

                document.add(table);

                if ("QS".equalsIgnoreCase(p.getRegionBag())) {
                    document.add(table);
                }

                document.add(new AreaBreak());

            }

            document.close();
            return baos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    private void refreshComboBoxes(HttpClient client) {
        try {
            HttpRequest regionRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/region/all"))
                    .build();
            String regionJson = client.send(regionRequest, HttpResponse.BodyHandlers.ofString()).body();
            Region[] regions = mapper.readValue(regionJson, Region[].class);
            Platform.runLater(() -> {
                regionBox.getItems().setAll(
                        Arrays.stream(regions).map(Region::getRegion).toList()
                );
            });

            HttpRequest menteqeRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/menteqe/all"))
                    .build();
            String menteqeJson = client.send(menteqeRequest, HttpResponse.BodyHandlers.ofString()).body();
            Menteqe[] menteqes = mapper.readValue(menteqeJson, Menteqe[].class);
            Platform.runLater(() -> {
                menteqeBox.getItems().setAll(
                        Arrays.stream(menteqes).map(Menteqe::getMenteqe).toList()
                );
            });

            HttpRequest anbarRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/anbar/all"))
                    .build();
            String anbarJson = client.send(anbarRequest, HttpResponse.BodyHandlers.ofString()).body();
            Anbar[] anbars = mapper.readValue(anbarJson, Anbar[].class);
            Platform.runLater(() -> {
                anbarBox.getItems().setAll(
                        Arrays.stream(anbars).map(Anbar::getAnbar).toList()
                );
            });

            HttpRequest supplierRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/tedarukcu/all"))
                    .build();
            String supplierJson = client.send(supplierRequest, HttpResponse.BodyHandlers.ofString()).body();
            Tedarukcu[] suppliers = mapper.readValue(supplierJson, Tedarukcu[].class);
            Platform.runLater(() -> {
                tedarukcuBox.getItems().setAll(
                        Arrays.stream(suppliers).map(Tedarukcu::getTedarukcu).toList()
                );
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void startScaleReader() {
        scaleReader = new CASScaleReader("COM3", CASScaleReader.ScaleMode.CI200A);
        if (scaleReader.connect()) {
            scaleReader.addWeightListener(weight ->
                    Platform.runLater(() -> doluCekiAuto.setText(weight))
            );
            scaleReader.startReading(100);
        }

    }


    @Override
    public void stop() {
        if (scaleReader != null) {
            scaleReader.disconnect();
        }
    }

    private void showPDFViewer(byte[] pdfBytes, Stage stage) throws IOException {
        BorderPane root = new BorderPane();

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ccc;");

        Button printBtn = new Button("Çap Et");
        Button saveBtn = new Button("Yadda Saxla");
        styleButtonGreen(saveBtn);
        styleButtonGrey(printBtn);

        topBar.getChildren().addAll(printBtn, saveBtn);

        PDDocument document = PDDocument.load(pdfBytes);
        PDFRenderer renderer = new PDFRenderer(document);
        int totalPages = document.getNumberOfPages();

        VBox pdfContainer = new VBox(20);
        pdfContainer.setAlignment(Pos.TOP_CENTER);
        pdfContainer.setPadding(new Insets(15));

        for (int i = 0; i < totalPages-1; i++) {
            BufferedImage bim = renderer.renderImageWithDPI(i, 150, ImageType.RGB);
            WritableImage fxImage = SwingFXUtils.toFXImage(bim, null);
            ImageView imageView = new ImageView(fxImage);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(800);
            pdfContainer.getChildren().add(imageView);
        }

        ScrollPane scrollPane = new ScrollPane(pdfContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background: #eaeaea;");

        root.setTop(topBar);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root);
        Stage pdfStage = new Stage();
        pdfStage.setMaximized(true);
        pdfStage.setTitle("Çap");
        pdfStage.setScene(scene);
        pdfStage.show();

        saveBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("purchases_report.pdf");
            File file = fileChooser.showSaveDialog(pdfStage);
            if (file != null) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(pdfBytes);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        printBtn.setOnAction(e -> {
            try {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPageable(new PDFPageable(document));
                if (job.printDialog()) {
                    job.print();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        pdfStage.setOnCloseRequest(event -> {
            try {
                document.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void updateNetCeki(TextField doluField, TextField bosField, TextField netField, TextField kiseSayiField, TextField birKiseField,TextField paletSayiField, TextField birPaletField) {
        double dolu = parseDoubleSafe(doluField.getText());
        double bos = parseDoubleSafe(bosField.getText());
        double birkise = parseDoubleSafe(birKiseField.getText());
        double kisesayi = parseDoubleSafe(kiseSayiField.getText());
        double paletsayi = parseDoubleSafe(paletSayiField.getText());
        double birpalet = parseDoubleSafe(birPaletField.getText());
        netField.setText(dolu - (bos + kisesayi*(birkise/1000) + paletsayi*(birpalet/1000)) + " kg");
    }
    private void updateNetCekiReverse(TextField doluField, TextField bosField, TextField netField, TextField kiseSayiField, TextField birKiseField,TextField paletSayiField, TextField birPaletField) {
        double dolu = parseDoubleSafe(doluField.getText());
        double bos = parseDoubleSafe(bosField.getText());
        double birkise = parseDoubleSafe(birKiseField.getText());
        double kisesayi = parseDoubleSafe(kiseSayiField.getText());
        double paletsayi = parseDoubleSafe(paletSayiField.getText());
        double birpalet = parseDoubleSafe(birPaletField.getText());

        netField.setText(bos - (dolu + kisesayi*(birkise/1000) + paletsayi*(birpalet/1000)) + " kg");
    }
    private double parseDoubleSafe(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    private void bindNetCeki(TextField doluField, TextField bosField, TextField netField, TextField kiseSayiField, TextField birKiseField,TextField paletSayiField, TextField birPaletField) {
        doluField.textProperty().addListener((obs, oldVal, newVal) -> calculateNet(doluField, bosField, netField,birKiseField,kiseSayiField,birPaletField,paletSayiField));
        bosField.textProperty().addListener((obs, oldVal, newVal) -> calculateNet(doluField, bosField, netField,birKiseField,kiseSayiField,birPaletField,paletSayiField));
    }
    private void bindNetCekiReverse(TextField doluField, TextField bosField, TextField netField, TextField kiseSayiField, TextField birKiseField,TextField paletSayiField, TextField birPaletField) {
        doluField.textProperty().addListener((obs, oldVal, newVal) -> calculateNetReverse(doluField, bosField, netField,birKiseField,kiseSayiField,birPaletField,paletSayiField));
        bosField.textProperty().addListener((obs, oldVal, newVal) -> calculateNetReverse(doluField, bosField, netField,birKiseField,kiseSayiField,birPaletField,paletSayiField));
    }
    private void loadPurchasesFromApi() {
        Task<List<Purchase>> task = new Task<>() {
            @Override
            protected List<Purchase> call() throws Exception {
                URL url = new URL("http://localhost:8080/api/purchases");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                List<Purchase> purchases = new ArrayList<>();

                if (conn.getResponseCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    JavaTimeModule module = new JavaTimeModule();
                    module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
                    module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
                    mapper.registerModule(module);
                    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                    purchases = Arrays.asList(mapper.readValue(conn.getInputStream(), Purchase[].class));
                }

                conn.disconnect();
                return purchases;
            }
        };

        task.setOnSucceeded(e -> {
            purchaseList.setAll(task.getValue());
        });

        task.setOnFailed(e -> {
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }
    private String trimZeros(double v) {
        if (Math.floor(v) == v) return String.valueOf((long) v);
        BigDecimal bd = new BigDecimal(v).setScale(2, RoundingMode.HALF_UP);
        return bd.stripTrailingZeros().toPlainString();
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




    private void calculateNet(TextField doluField, TextField bosField, TextField netField, TextField birKiseField, TextField kiseSayiField, TextField paletSayiField,TextField birPaletField) {
        double dolu = parseDoubleSafe(doluField.getText());
        double bos = parseDoubleSafe(bosField.getText());
        double birkise = parseDoubleSafe(birKiseField.getText());
        double kisesayi = parseDoubleSafe(kiseSayiField.getText());
        double paletsayi = parseDoubleSafe(paletSayiField.getText());
        double birpalet = parseDoubleSafe(birPaletField.getText());
        double net = dolu - (bos + kisesayi*(birkise/1000) + paletsayi*(birpalet/1000));

        BigDecimal rounded = new BigDecimal(net).setScale(2, RoundingMode.HALF_UP);

        netField.setText(rounded.toString());
    }

    private void calculateNetReverse(TextField doluField, TextField bosField, TextField netField, TextField birKiseField, TextField kiseSayiField, TextField paletSayiField,TextField birPaletField) {
        double dolu = parseDoubleSafe(doluField.getText());
        double bos = parseDoubleSafe(bosField.getText());
        double birkise = parseDoubleSafe(birKiseField.getText());
        double kisesayi = parseDoubleSafe(kiseSayiField.getText());
        double paletsayi = parseDoubleSafe(paletSayiField.getText());
        double birpalet = parseDoubleSafe(birPaletField.getText());
        double net = bos - (dolu + kisesayi*(birkise/1000) + paletsayi*(birpalet/1000));

        BigDecimal rounded = new BigDecimal(net).setScale(2, RoundingMode.HALF_UP);

        netField.setText(rounded.toString());
    }




    // ------------------ INLINE CLEARABLE FIELD ------------------
    public static StackPane createClearableField(TextInputControl field) {
        Button clearBtn = new Button("✕");
        clearBtn.setOnAction(e -> field.clear());
        clearBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #999;" +
                        "-fx-font-size: 14;" +
                        "-fx-cursor: hand;"
        );
        clearBtn.setFocusTraversable(false);

        StackPane stack = new StackPane(field, clearBtn);
        StackPane.setAlignment(clearBtn, Pos.CENTER_RIGHT);
        StackPane.setMargin(clearBtn, new Insets(0, 8, 0, 0));
        return stack;
    }

    // ------------------ INPUT STYLING ------------------
    public static void styleTextInput(TextInputControl tf) {
        tf.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #323232;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 35 10 14;" +
                        "-fx-font-size: 15;" +
                        "-fx-text-fill: black;" +
                        "-fx-background-insets: 0;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );
        tf.setMinHeight(45);
    }

    public static void styleTextArea(TextArea area) {
        area.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #323232;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 14 10 14;" +
                        "-fx-font-size: 15;" +
                        "-fx-text-fill: black;" +
                        "-fx-background-insets: 0;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );
        area.setMinHeight(80);
    }

    public static void styleReadOnly(TextField tf) {
        tf.setEditable(false);
        tf.setStyle(
                "-fx-background-color: #F0F0F2;" +
                        "-fx-border-color: #323232;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 10 14 10 14;" +
                        "-fx-font-size: 15;" +
                        "-fx-text-fill: black;" +
                        "-fx-background-insets: 0;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );
        tf.setMinHeight(45);
        tf.setMinWidth(221);
    }

    // ------------------ COMBOBOX STYLING ------------------
    public static void styleComboBox(ComboBox<String> comboBox) {
        comboBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #323232;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 4 8 4 8;" +
                        "-fx-font-size: 13;" +
                        "-fx-background-insets: 0;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );
        comboBox.setPrefWidth(240);
        comboBox.setPrefHeight(45);
        comboBox.setMinHeight(45);
        comboBox.setMaxHeight(45);


        Callback<ListView<String>, ListCell<String>> cellFactory = listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setTextFill(Color.BLACK);
                }
                setStyle(
                        "-fx-background-color: white;" +
                                "-fx-border-color: transparent;" +
                                "-fx-font-size: 15;" +
                                "-fx-padding: 8 12 8 12;"
                );
            }
        };

        comboBox.setCellFactory(cellFactory);
        comboBox.setButtonCell(cellFactory.call(null));
    }

    // ------------------ BUTTON STYLING ------------------
    public static void styleButtonBase(Button button, String bg, String textColor, String hoverBg, String hoverTextColor) {
        button.setFont(Font.font("Arial", 18));
        button.setMinHeight(45);
        button.setEffect(new DropShadow(3, Color.GRAY));

        button.setBackground(new Background(new BackgroundFill(Color.web(bg), new CornerRadii(10), Insets.EMPTY)));
        button.setTextFill(Color.web(textColor));

        button.setOnMouseEntered(e -> {
            button.setBackground(new Background(new BackgroundFill(Color.web(hoverBg), new CornerRadii(10), Insets.EMPTY)));
            button.setTextFill(Color.web(hoverTextColor));
        });

        button.setOnMouseExited(e -> {
            button.setBackground(new Background(new BackgroundFill(Color.web(bg), new CornerRadii(10), Insets.EMPTY)));
            button.setTextFill(Color.web(textColor));
        });
    }

    public static void styleButtonGreen(Button button) {
        styleButtonBase(button, "#56ab2f", "white", "#499128", "white");
    }

    public static void styleButtonCyan(Button button) {
        styleButtonBase(button, "#12E3D0", "white", "#10C4B4", "white");
    }

    public static void styleButtonRed(Button button) {
        styleButtonBase(button, "#D12700", "white", "#991D00", "white");
    }
    public static void styleButtonLightGreen(Button button) {
        styleButtonBase(button, "#3BC158", "white", "#31A149", "white");
    }
    public static void styleButtonGrey(Button button) {
        styleButtonBase(button, "#E3E3E3", "#3e3e3e", "#C7C7C7", "#3e3e3e");
    }
}
