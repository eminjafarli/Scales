package com.agrarco.agrovers;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginPage extends Application {

    private VBox notificationBox;
    private Stage loginStage;

    @Override
    public void start(Stage stage) {
        this.loginStage = stage;

        BorderPane content = new BorderPane();
        content.setStyle("-fx-background-color: #ffffff;");
        content.setPrefSize(400, 500);

        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(30));
        formBox.setPrefWidth(400);
        formBox.setMaxWidth(400);
        formBox.setStyle("-fx-background-color: #F7F7F9; -fx-background-radius: 10; -fx-padding: 30;");
        formBox.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.1)));

        Label title = new Label("Daxil ol");
        title.setFont(Font.font("Arial", 24));

        TextField usernameField = new TextField();
        usernameField.setPromptText("İstifadəçi adı");
        usernameField.setFont(Font.font(15));
        usernameField.setPrefWidth(260);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Şifrə");
        passwordField.setFont(Font.font(15));
        passwordField.setPrefWidth(260);

        Button loginButton = new Button("Giriş");
        loginButton.setPrefWidth(130);
        loginButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 15;");

        formBox.getChildren().addAll(title, usernameField, passwordField, loginButton);

        VBox centerBox = new VBox(formBox);
        centerBox.setAlignment(Pos.CENTER);
        content.setCenter(centerBox);

        notificationBox = new VBox();
        notificationBox.setAlignment(Pos.TOP_CENTER);
        notificationBox.setMouseTransparent(true);
        notificationBox.setSpacing(10);

        StackPane root = new StackPane(content, notificationBox);
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

        Scene scene = new Scene(root);
        stage.setTitle("Giriş");
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
                    System.out.println("HTTP status: " + status);

                    InputStream is = (status == 200) ? conn.getInputStream() : conn.getErrorStream();
                    String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("Cavab: " + response);

                    JSONObject jsonResponse = new JSONObject(response);
                    String name;
                    String role;
                    if (status == 200) {
                        if (jsonResponse.has("name")) {
                            name = jsonResponse.getString("name");
                        } else {
                            name = null;
                        }
                        if (jsonResponse.has("role")) {
                            role = jsonResponse.getString("role");
                        } else {
                            role = null;
                        }
                        System.out.println("User name: " + name);
                        System.out.println("User role: " + role);
                    } else {
                        name = null;
                        role = null;
                    }

                    conn.disconnect();

                    Platform.runLater(() -> {
                        String displayMessage;

                        if (status == 200) {
                            displayMessage = "Giriş uğurla tamamlandı";
                        } else {
                            if (response.contains("Invalid username") || response.contains("Bad credentials")) {
                                displayMessage = "İstifadəçi adı və ya şifrə yanlışdır";
                            } else if (response.contains("User not found")) {
                                displayMessage = "İstifadəçi tapılmadı";
                            } else {
                                displayMessage = "İstifadəçi adı və ya şifrə yanlışdır";
                            }
                        }

                        showNotification(displayMessage, status == 200);


                        if (status == 200) {
                            HelloApplication mainApp = new HelloApplication(name,role);
                            try {
                                Stage primaryStage = new Stage();
                                mainApp.start(primaryStage);
                                loginStage.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                                showNotification("Əsas tətbiqi başladmaq mümkün olmadı", false);
                            }
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
        notification.setAlignment(Pos.TOP_CENTER);
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
