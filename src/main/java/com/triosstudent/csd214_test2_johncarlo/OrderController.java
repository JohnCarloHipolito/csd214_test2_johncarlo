package com.triosstudent.csd214_test2_johncarlo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class OrderController {

    @FXML private Hyperlink logoutHL;
    private AccountInfo accountInfo;

    // receive the data from login stage
    public void setData(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    @FXML
    protected void executeQuery(ActionEvent event) {
    }

    @FXML
    protected void onLogout() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(OrderMgmtApplication.class.getResource("login-view.fxml"));
        Stage orderStage = (Stage) logoutHL.getScene().getWindow();
        Stage stage = new Stage();
        stage.setTitle("CSD214 Test2: John Carlo - Inventory Management System");
        stage.setScene(new Scene(fxmlLoader.load()));
        orderStage.close();
        stage.show();
    }

    private void readItems(Connection connection) throws SQLException {
    }

    private void createItem(Connection connection) throws SQLException {
    }

    private void updateItem(Connection connection) throws SQLException {
    }

    private void deleteItem(Connection connection) throws SQLException {
    }
}
