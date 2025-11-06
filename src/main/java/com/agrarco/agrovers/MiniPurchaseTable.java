package com.agrarco.agrovers;

import com.fasterxml.jackson.annotation.JsonFormat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MiniPurchaseTable {

    private final ObservableList<MiniPurchase> miniPurchasesList = FXCollections.observableArrayList();
    private final TableView<MiniPurchase> miniTable = new TableView<>(miniPurchasesList);

    public VBox createMiniPurchaseSection(TextField doluCekiManual,
                                          TextField kiseSayiField,
                                          TextField birKiseField,
                                          TextField paletSayiField,
                                          TextField birPaletField) {

        miniTable.setEditable(false);

        TableColumn<MiniPurchase, LocalDateTime> colTarix = new TableColumn<>("Tarix");
        colTarix.setCellValueFactory(new PropertyValueFactory<>("Tarix"));
        colTarix.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item));
            }
        });

        TableColumn<MiniPurchase, Double> colCeki = new TableColumn<>("Çəki");
        colCeki.setCellValueFactory(new PropertyValueFactory<>("Ceki"));

        TableColumn<MiniPurchase, Integer> colKise = new TableColumn<>("Kisə sayı");
        colKise.setCellValueFactory(new PropertyValueFactory<>("KiseSayi"));

        TableColumn<MiniPurchase, Double> colBirKise = new TableColumn<>("Bir kisənin çəkisi");
        colBirKise.setCellValueFactory(new PropertyValueFactory<>("BirKiseninCekisi"));

        TableColumn<MiniPurchase, Integer> colPalet = new TableColumn<>("Palet sayı");
        colPalet.setCellValueFactory(new PropertyValueFactory<>("PaletSayi"));

        TableColumn<MiniPurchase, Double> colBirPalet = new TableColumn<>("Bir paletin çəkisi");
        colBirPalet.setCellValueFactory(new PropertyValueFactory<>("BirPaletinSayi"));

        TableColumn<MiniPurchase, Void> colDelete = new TableColumn<>("Ləğv et");
        colDelete.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Ləğv et");
            {
                deleteBtn.setOnAction(event -> {
                    MiniPurchase data = getTableView().getItems().get(getIndex());
                    miniPurchasesList.remove(data);
                });
                deleteBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        miniTable.getColumns().addAll(colTarix, colCeki, colKise, colBirKise, colPalet, colBirPalet, colDelete);
        miniTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnAdd = new Button("Əlavə et");
        btnAdd.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;");
        btnAdd.setOnAction(event -> {
            try {

                MiniPurchase newMini = new MiniPurchase(
                        0,
                        0,
                        LocalDateTime.now().withNano(0),
                        Double.parseDouble(doluCekiManual.getText()),
                        Integer.parseInt(kiseSayiField.getText()),
                        Double.parseDouble(birKiseField.getText()),
                        Integer.parseInt(paletSayiField.getText()),
                        Double.parseDouble(birPaletField.getText())
                );
                System.out.println("===== MiniPurchase Data =====");
                System.out.println("id: " + newMini.getId());
                System.out.println("purchaseId: " + newMini.getPurchaseId());
                System.out.println("Tarix: " + newMini.getTarix());
                System.out.println("Ceki: " + newMini.getCeki());
                System.out.println("KiseSayi: " + newMini.getKiseSayi());
                System.out.println("BirKiseninCekisi: " + newMini.getBirKiseninCekisi());
                System.out.println("PaletSayi: " + newMini.getPaletSayi());
                System.out.println("BirPaletinSayi: " + newMini.getBirPaletinSayi());
                System.out.println("=============================");

                miniPurchasesList.add(newMini);

            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Zəhmət olmasa bütün sahələri düzgün doldurun!", ButtonType.OK);
                alert.showAndWait();
            }
        });


        VBox box = new VBox(10, btnAdd, miniTable);
        box.setStyle("-fx-padding: 10;");
        return box;
    }

    public ObservableList<MiniPurchase> getMiniPurchases() {
        return miniPurchasesList;
    }
}
