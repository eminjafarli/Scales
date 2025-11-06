package com.agrarco.agrovers;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class LoginPage extends Application {

    private VBox notificationBox;
    private Stage loginStage;

    @Override
    public void start(Stage stage) {
        this.loginStage = stage;

        loginStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("agrarco.png"))));
        BorderPane rootLayout = new BorderPane();

        VBox formBox = new VBox(18);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(40));
        formBox.setPrefWidth(450);
        formBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 0 1 0 0;");
        formBox.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.05)));

        ImageView logo = new ImageView(new Image(getClass().getResource("agrarco.png").toExternalForm()));
        logo.setFitWidth(80);
        logo.setPreserveRatio(true);

        Label title = new Label("Xoş Gəlmisiniz");
        title.setFont(Font.font("Arial", 28));
        title.setTextFill(Color.web("#333"));

        Label subtitle = new Label("Davam etmək üçün daxil olun");
        subtitle.setFont(Font.font("Arial", 15));
        subtitle.setTextFill(Color.web("#666"));

        TextField usernameField = new TextField();
        usernameField.setPromptText("İstifadəçi adı");
        usernameField.setFont(Font.font(15));
        usernameField.setPrefWidth(280);
        usernameField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 10;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Şifrə");
        passwordField.setFont(Font.font(15));
        passwordField.setPrefWidth(280);
        passwordField.setStyle("""
    -fx-background-radius: 10;
    -fx-border-radius: 10;
    -fx-padding: 10 35 10 10;
""");

        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Şifrə");
        visiblePasswordField.setFont(Font.font(15));
        visiblePasswordField.setPrefWidth(280);
        visiblePasswordField.setStyle("""
    -fx-background-radius: 10;
    -fx-border-radius: 10;
    -fx-padding: 10 35 10 10;
""");
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);


        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        javafx.scene.image.Image eyeOpen = new javafx.scene.image.Image(
                Objects.requireNonNull(getClass().getResourceAsStream("eye.png"))
        );
        javafx.scene.image.Image eyeClosed = new javafx.scene.image.Image(
                Objects.requireNonNull(getClass().getResourceAsStream("eye-off.png"))
        );

        javafx.scene.control.Button eyeButton = new javafx.scene.control.Button();
        eyeButton.setGraphic(new javafx.scene.image.ImageView(eyeOpen));
        eyeButton.setStyle("""
    -fx-background-color: transparent;
    -fx-cursor: hand;
    -fx-padding: 0;
""");
        eyeButton.setFocusTraversable(false);

        StackPane eyePane = new StackPane(eyeButton);
        eyePane.setAlignment(Pos.CENTER_RIGHT);
        eyePane.setPadding(new Insets(0, 10, 0, 0));

        StackPane passwordStack = new StackPane(passwordField, visiblePasswordField, eyePane);

        eyeButton.setOnAction(e -> {
            boolean showing = visiblePasswordField.isVisible();
            if (showing) {
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                passwordField.requestFocus();
                passwordField.positionCaret(passwordField.getText().length());
                ((javafx.scene.image.ImageView) eyeButton.getGraphic()).setImage(eyeOpen);
            } else {
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                visiblePasswordField.requestFocus();
                visiblePasswordField.positionCaret(visiblePasswordField.getText().length());
                ((javafx.scene.image.ImageView) eyeButton.getGraphic()).setImage(eyeClosed);
            }
        });


        Button loginButton = new Button("Daxil ol");
        loginButton.setPrefWidth(200);
        loginButton.setStyle("""
                -fx-background-color: linear-gradient(to right, #28a745, #218838);
                -fx-text-fill: white;
                -fx-font-size: 16;
                -fx-background-radius: 12;
                -fx-cursor: hand;
                """);

        loginButton.setOnMouseEntered(e -> loginButton.setStyle("""
                -fx-background-color: linear-gradient(to right, #34ce57, #28a745);
                -fx-text-fill: white;
                -fx-font-size: 16;
                -fx-background-radius: 12;
                -fx-cursor: hand;
                """));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("""
                -fx-background-color: linear-gradient(to right, #28a745, #218838);
                -fx-text-fill: white;
                -fx-font-size: 16;
                -fx-background-radius: 12;
                -fx-cursor: hand;
                """));

        formBox.getChildren().addAll(logo,title, subtitle, usernameField, passwordStack, loginButton);

        formBox.setTranslateX(-100);
        FadeTransition fadeInForm = new FadeTransition(Duration.millis(800), formBox);
        fadeInForm.setFromValue(0);
        fadeInForm.setToValue(1);
        TranslateTransition slideForm = new TranslateTransition(Duration.millis(800), formBox);
        slideForm.setFromX(-100);
        slideForm.setToX(0);
        fadeInForm.play();
        slideForm.play();

        StackPane imagePane = new StackPane();

        ImageView background;
        try {
            URL imageUrl = getClass().getResource("img.png");
            if (imageUrl != null) {
                background = new ImageView(new Image(imageUrl.toExternalForm()));
            } else {
                System.err.println("⚠️ Image not found: img.png — using placeholder color");
                background = new ImageView();
                background.setStyle("-fx-background-color: #28a745;");
            }
        } catch (Exception ex) {
            background = new ImageView();
            background.setStyle("-fx-background-color: #28a745;");
        }

        background.setPreserveRatio(false);
        background.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight());
        background.setFitWidth(Screen.getPrimary().getVisualBounds().getWidth() * 0.75);
        background.setOpacity(0);

        FadeTransition fadeImage = new FadeTransition(Duration.millis(1200), background);
        fadeImage.setFromValue(0);
        fadeImage.setToValue(1);
        fadeImage.play();

        Label overlayText = new Label("AGRARCO TƏRƏZİ OPERATORU");
        overlayText.setFont(Font.font("Arial", 34));
        overlayText.setTextFill(Color.WHITE);
        overlayText.setStyle("-fx-font-weight: bold;");
        overlayText.setOpacity(0.9);

        VBox overlay = new VBox(overlayText);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        imagePane.getChildren().addAll(background, overlay);

        HBox mainContainer = new HBox();
        mainContainer.getChildren().addAll(formBox, imagePane);
        HBox.setHgrow(imagePane, Priority.ALWAYS);

        notificationBox = new VBox();
        notificationBox.setAlignment(Pos.TOP_CENTER);
        notificationBox.setMouseTransparent(true);
        notificationBox.setSpacing(10);

        StackPane mainStack = new StackPane(mainContainer, notificationBox);
        StackPane.setAlignment(notificationBox, Pos.TOP_CENTER);
        StackPane.setMargin(notificationBox, new Insets(30, 0, 0, 0));

        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> loginButton.fire());

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (username.isEmpty() && password.isEmpty()) {
                showNotification("İstifadəçi adı və şifrə daxil edin", false);
            } else if (username.isEmpty()) {
                showNotification("İstifadəçi adını daxil edin", false);
            } else if (password.isEmpty()) {
                showNotification("Şifrəni daxil edin", false);
            } else {
                login(username, password);
            }
        });

        Scene scene = new Scene(mainStack,
                Screen.getPrimary().getVisualBounds().getWidth(),
                Screen.getPrimary().getVisualBounds().getHeight());

        stage.setTitle("Agrarco | Giriş");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void login(String username, String password) {
        System.out.println("Login yoxlanılır: " + username + " / " + password);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    URL url = new URL("http://localhost:8080/api/auth/login");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    JSONObject json = new JSONObject();
                    json.put("username", username);
                    json.put("password", password);

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                    }

                    int status = conn.getResponseCode();
                    InputStream is = (status == 200) ? conn.getInputStream() : conn.getErrorStream();
                    String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    JSONObject jsonResponse = new JSONObject(response);

                    String name = jsonResponse.optString("name", null);
                    String role = jsonResponse.optString("role", null);

                    Platform.runLater(() -> {
                        if (status == 200) {
                            showNotification("Giriş uğurla tamamlandı ✅", true);
                            try {
                                HelloApplication mainApp = new HelloApplication(name, role);
                                Stage primaryStage = new Stage();
                                mainApp.start(primaryStage);
                                loginStage.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                                showNotification("Əsas tətbiqi başladmaq mümkün olmadı", false);
                            }
                        } else {
                            showNotification("İstifadəçi adı və ya şifrə yanlışdır", false);
                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Platform.runLater(() -> showNotification("Xəta baş verdi", false));
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void showNotification(String message, boolean success) {
        notificationBox.getChildren().clear();

        Label notification = new Label(message);
        notification.setStyle("-fx-background-color: " + (success ? "#28a745" : "#dc3545") +
                "; -fx-text-fill: white; -fx-padding: 14 28; -fx-background-radius: 12; -fx-font-size: 16;");
        notification.setWrapText(true);
        notification.setMaxWidth(400);
        notification.setAlignment(Pos.CENTER);
        notification.setOpacity(0);

        notificationBox.getChildren().add(notification);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), notification);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        new Thread(() -> {
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(400), notification);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(e -> notificationBox.getChildren().remove(notification));
                fadeOut.play();
            });
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
