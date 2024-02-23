package com.triosstudent.csd214_test2_johncarlo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginController {

    public TextField emailTF;
    public PasswordField passwordTF;
    public Label messageLbl;

    @FXML
    protected void onLogin() {
        // validate email and password
        if(emailTF.getText().isEmpty() || passwordTF.getText().isEmpty()) {
            messageLbl.setTextFill(Color.RED);
            messageLbl.setText("Please provide email and password");
            return;
        }
        // connect to database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/csd214_test2_johncarlo", "admin", "admin")) {
            String readQuery = "SELECT * FROM account_info WHERE email='%s' AND password='%s'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format(readQuery, emailTF.getText(), passwordTF.getText()));
            if (resultSet.next()) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("order-view.fxml"));
                Parent root = fxmlLoader.load();
                OrderController inventoryController = fxmlLoader.getController();
                inventoryController.setData(new AccountInfo(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password")));
                Stage inventoryStage = new Stage();
                inventoryStage.setScene(new Scene(root));
                inventoryStage.setTitle("CSD214_Test2 John Carlo - Order Management System");
                inventoryStage.show();
                System.out.printf("INFO: %s logged in.%n", resultSet.getString("username"));

                Stage loginStage = (Stage) emailTF.getScene().getWindow();
                loginStage.close();
            } else {
                messageLbl.setTextFill(Color.RED);
                messageLbl.setText("Login failed.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("FXML Error: " + e.getMessage());
        }
    }
}