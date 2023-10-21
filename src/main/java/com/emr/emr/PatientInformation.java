package com.emr.emr;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Objects;

public class PatientInformation extends Application {

    private static final String DB_URL = "jdbc:ucanaccess://" + Objects.requireNonNull(Login.class.getClassLoader().getResource("com/emr/emr/Logins.accdb")).getPath();
    private final String username;

    public PatientInformation(String u) {
        username = u;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Image logo = new Image(Objects.requireNonNull(Login.class.getClassLoader().getResourceAsStream("com/emr/emr/image.png")));
        primaryStage.setTitle("Patient Information");
        primaryStage.getIcons().add(logo);

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        // To hold the logo and gridPane
        BorderPane bPane = new BorderPane();

        // Labels and Fields setup
        Font tahoma30 = new Font("Tahoma", 30);

        Font tahomaTitle = new Font("Tahoma", 50);

        Label title = new Label("Patient Information");
        title.setFont(tahomaTitle);

        Label ageLabel = new Label("Age");
        ageLabel.setFont(tahoma30);
        NumberField ageField = new NumberField();
        ageField.setFont(tahoma30);

        Label genderLabel = new Label("Gender");
        genderLabel.setFont(tahoma30);
        TextField genderField = new TextField();
        genderField.setFont(tahoma30);

        Label bloodGroupLabel = new Label("Blood Group");
        bloodGroupLabel.setFont(tahoma30);
        TextField bloodGroupField = new TextField();
        bloodGroupField.setFont(tahoma30);

        Label allergiesLabel = new Label("Allergies");
        allergiesLabel.setFont(tahoma30);
        TextField allergiesField = new TextField();
        allergiesField.setFont(tahoma30);

        Label medicationLabel = new Label("Medication");
        medicationLabel.setFont(tahoma30);
        TextField medicationField = new TextField();
        medicationField.setFont(tahoma30);

        Label medicalHistoryLabel = new Label("Medical History");
        medicalHistoryLabel.setFont(tahoma30);
        TextArea medicalHistoryField = new TextArea();
        medicalHistoryField.setFont(tahoma30);

        // Autofill
        try (Connection con = DriverManager.getConnection(DB_URL);
             PreparedStatement checkStatement = con.prepareStatement("SELECT * FROM patientMedicalInfoT WHERE username=?")) {
            checkStatement.setString(1, username);
            try (ResultSet checkResult = checkStatement.executeQuery()) {
                if (checkResult.next()) {
                    ageField.setText(checkResult.getString(2));
                    genderField.setText(checkResult.getString(3));
                    bloodGroupField.setText(checkResult.getString(4));
                    allergiesField.setText(checkResult.getString(5));
                    medicationField.setText(checkResult.getString(6));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Submit button setup
        Button submitButton = new Button("Submit");
        submitButton.setFont(tahoma30);
        submitButton.setOnAction(e -> {
            try {
                Connection con2 = DriverManager.getConnection(DB_URL);
                // Check if the username already exists
                String checkQuery2 = "SELECT COUNT(*) FROM loginT WHERE username=?";
                PreparedStatement checkStatement2 = con2.prepareStatement(checkQuery2);
                checkStatement2.setString(1, username);
                ResultSet checkResult2 = checkStatement2.executeQuery();
                checkResult2.next();
                int count = checkResult2.getInt(1);
                if (count > 0) {
                    // Update the existing user's information
                    String updateQuery = "UPDATE patientMedicalInfoT SET age=?, gender=?, blood_group=?, allergies=?, medications=?, medical_history=? WHERE username=?";
                    PreparedStatement updateStatement = con2.prepareStatement(updateQuery);
                    updateStatement.setDouble(1, Double.parseDouble(ageField.getText()));
                    updateStatement.setString(2, genderField.getText());
                    updateStatement.setString(3, bloodGroupField.getText());
                    updateStatement.setString(4, allergiesField.getText());
                    updateStatement.setString(5, medicationField.getText());
                    updateStatement.setString(6, medicalHistoryField.getText());
                    updateStatement.setString(7, username);
                    updateStatement.executeUpdate();
                    showAlert(Alert.AlertType.INFORMATION, "Update Successful",
                            "User information updated successfully.");
                } else {
                    // Register if not already
                    String insertQuery = "INSERT INTO patientMedicalInfoT (username) VALUES (?)";
                    PreparedStatement statement = con2.prepareStatement(insertQuery);
                    statement.setString(1, username);
                    statement.executeUpdate();
                    showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "User registered successfully.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showErrorAlert(ex.toString());
            }
        });

        // Return button setup
        Button returnButton = new Button("Return");
        returnButton.setFont(tahoma30);
        returnButton.setOnAction(e -> {
            // Return to Login page
            new Login().start(new Stage());
            primaryStage.hide();
        });

        // Add Labels and Fields to gridpane
        gridPane.getColumnConstraints().setAll(new ColumnConstraints(250), new ColumnConstraints(250));
        gridPane.addRow(0, title);
        GridPane.setColumnSpan(title, GridPane.REMAINING);
        gridPane.addRow(1, ageLabel, ageField);
        gridPane.addRow(2, genderLabel, genderField);
        gridPane.addRow(3, bloodGroupLabel, bloodGroupField);
        gridPane.addRow(4, allergiesLabel, allergiesField);
        gridPane.addRow(5, medicationLabel, medicationField);
        gridPane.addRow(6, medicalHistoryLabel, medicalHistoryField);
        gridPane.addRow(7, submitButton, returnButton);

        // Add logo and gridpane to borderpane
        bPane.setCenter(gridPane);
        bPane.setRight(new ImageViewSize(logo));

        // VBox setup
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().add(bPane);

        // Show application
        primaryStage.setScene(new CSSScene(vbox, 625, 600, Login.class.getClassLoader().getResource("com/emr/emr/styles.css")));
        primaryStage.show();
    }

    // Extra alert commands
    private void showErrorAlert(String message) {
        showAlert(Alert.AlertType.ERROR, "Database Error", message);
    }

    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
