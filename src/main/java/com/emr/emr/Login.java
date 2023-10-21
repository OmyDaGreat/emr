package com.emr.emr;

import com.google.common.base.Strings;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Objects;

public class Login extends Application {

	private static final String DB_URL = "jdbc:ucanaccess://" + Objects.requireNonNull(Login.class.getClassLoader().getResource("com/emr/emr/Logins.accdb")).getPath();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/emr/emr/image.png")));
		primaryStage.setTitle("Registration Page");
		primaryStage.getIcons().add(logo);

		GridPane gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);

		// To hold the logo and gridPane
		BorderPane bPane = new BorderPane();

		// Labels and Fields setup
		Font tahoma30 = new Font("Tahoma", 30);

		Font tahomaTitle = new Font("Tahoma", 50);

		Label title = new Label("Registration");
		title.setFont(tahomaTitle);

		Label userLabel = new Label("Username");
		userLabel.setFont(tahoma30);
		TextField userField = new TextField();
		userField.setFont(tahoma30);

		Label passLabel = new Label("Password");
		passLabel.setFont(tahoma30);
		PasswordField passField = new PasswordField();
		passField.setFont(tahoma30);

		// Submit button setup
		Button submitButton = new Button("Submit");
	    submitButton.setFont(tahoma30);
	    submitButton.setOnAction(e -> handleLogin(userField.getText(), passField.getText(), primaryStage));

	    // Register button setup
	    Button registerButton = new Button("Register");
	    registerButton.setFont(tahoma30);
	    registerButton.setOnAction(e -> handleRegistration(userField.getText(), passField.getText()));


		// Add Labels and Fields to gridpane
		gridPane.addRow(0, title);
		GridPane.setColumnSpan(title, GridPane.REMAINING);
		gridPane.addRow(1, userLabel, userField);
		gridPane.addRow(2, passLabel, passField);
		gridPane.addRow(3, submitButton, registerButton);

		// Add logo and gridpane to borderpane
		bPane.setCenter(gridPane);
		bPane.setRight(new ImageViewSize(logo));

		// VBox setup
		VBox vbox = new VBox(20);
		vbox.setAlignment(Pos.CENTER);
		vbox.setPadding(new Insets(10));
		vbox.getChildren().add(bPane);

		// Show application
	    primaryStage.setScene(new CSSScene(vbox, 525, 400, Login.class.getResource("/com/emr/emr/styles.css")));
	    primaryStage.show();
	}
	
	private void handleLogin(String username, String password, Stage primaryStage) {
	    if (isNullOrEmpty(username, password)) {
	        showEmptyFieldsAlert();
	        return;
	    }

		String queryString = "SELECT * FROM loginT WHERE username=?";
	    try (Connection con = DriverManager.getConnection(DB_URL);
			 PreparedStatement preparedStatement = con.prepareStatement(queryString)) {
	        preparedStatement.setString(1, username);
	        ResultSet rs = preparedStatement.executeQuery();

	        if (rs.next()) {
	            String hashedPassword = rs.getString("password");
	            if (BCrypt.checkpw(password, hashedPassword)) {
	                showAlert(Alert.AlertType.INFORMATION, "Login Successful", "You have successfully logged in.");
	                new PatientInformation(username).start(new Stage());
	                primaryStage.hide();
	            } else {
	                showInvalidCredentialsAlert();
	            }
	        } else {
	            showInvalidCredentialsAlert();
	        }
	    } catch (Exception e2) {
	        e2.printStackTrace();
	        showErrorAlert(e2.toString());
	    }
	}

	private void handleRegistration(String username, String password) {
	    if (isNullOrEmpty(username, password)) {
	        showEmptyFieldsAlert();
	        return;
	    }

		String checkQuery = "SELECT COUNT(*) FROM loginT WHERE username=?";
	    try (Connection con = DriverManager.getConnection(DB_URL);PreparedStatement checkStatement = con.prepareStatement(checkQuery)){
	        checkStatement.setString(1, username);
	        ResultSet checkResult = checkStatement.executeQuery();
	        checkResult.next();
	        int count = checkResult.getInt(1);

	        if (count > 0) {
	            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username already exists.");
	        } else {
	            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
	            String insertQuery = "INSERT INTO loginT (username, password) VALUES (?, ?)";
				try (PreparedStatement statement = con.prepareStatement(insertQuery)) {
					statement.setString(1, username);
					statement.setString(2, hashedPassword);
					statement.executeUpdate();
				} catch (Exception ex) {
					ex.printStackTrace();
					showErrorAlert(ex.toString());
				}
				insertQuery = "INSERT INTO patientMedicalInfoT (username) VALUES (?)";
				try (PreparedStatement statement = con.prepareStatement(insertQuery)) {
					statement.setString(1, username);
					statement.executeUpdate();
				} catch (Exception ex) {
					ex.printStackTrace();
					showErrorAlert(ex.toString());
				}
	            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "User registered successfully.");
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        showErrorAlert(ex.toString());
	    }
	}

	private static boolean isNullOrEmpty(String... s) {
	    return Arrays.stream(s).anyMatch(Strings::isNullOrEmpty);
	}
	
	// Extra alert commands
	private void showInvalidCredentialsAlert() {
		showAlert(Alert.AlertType.ERROR, "Invalid Credentials", "The username and password are not valid.");
	}

	private void showEmptyFieldsAlert() {
		showAlert(Alert.AlertType.WARNING, "Empty Fields",
				"Did you seriously think that you could get around this security by leaving it blank?");
	}

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
