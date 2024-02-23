package com.triosstudent.csd214_test2_johncarlo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderController {

    @FXML
    private Button readBtn;
    @FXML
    private Button createBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private Button deleteBtn;

    @FXML
    private Label messageLbl;
    @FXML
    private Label userNameLbl;

    @FXML
    private TextField orderIdTF;
    @FXML
    private TextField productNameTF;
    @FXML
    private TextField quantityTF;
    @FXML
    private TextField totalPriceTF;

    public TableView<OrderDetails> orderTbl;
    public TableColumn<OrderDetails, Long> orderIdCol;
    public TableColumn<OrderDetails, String> productNameCol;
    public TableColumn<OrderDetails, Integer> quantityCol;
    public TableColumn<OrderDetails, Double> totalPriceCol;

    @FXML
    private Hyperlink logoutHL;

    private AccountInfo accountInfo;

    // receive the data from login stage
    public void setData(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
        userNameLbl.setText(accountInfo.getUsername());
    }

    @FXML
    protected void executeQuery(ActionEvent event) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/csd214_test2_johncarlo", "admin", "admin")) {
            if (event.getSource() == readBtn) {
                readItems(connection);
            } else if (event.getSource() == createBtn) {
                createItem(connection);
            } else if (event.getSource() == updateBtn) {
                updateItem(connection);
            } else if (event.getSource() == deleteBtn) {
                deleteItem(connection);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
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
        String readQuery = "SELECT * FROM order_details";
        PreparedStatement preparedStatement = connection.prepareStatement(readQuery);
        ResultSet resultSet = preparedStatement.executeQuery();
        orderTbl.getItems().clear();
        while (resultSet.next()) {
            orderTbl.getItems().add(new OrderDetails(
                    resultSet.getLong("order_id"),
                    resultSet.getString("product_name"),
                    resultSet.getInt("quantity"),
                    resultSet.getDouble("total_price")
            ));
        }
        messageLbl.setTextFill(Color.GREEN);
        messageLbl.setText("Items were read successfully.");
        clearFields();

    }

    private void createItem(Connection connection) throws SQLException {
        // validate if id is empty
        if (!orderIdTF.getText().isEmpty()) {
            messageLbl.setTextFill(Color.RED);
            messageLbl.setText("'Id' should be empty when adding order detail.");
            return;
        }
        // validate if name, price, and quantity are empty
        if (productNameTF.getText().isEmpty() || quantityTF.getText().isEmpty() || totalPriceTF.getText().isEmpty()) {
            messageLbl.setTextFill(Color.RED);
            messageLbl.setText("'Name', 'Price', and 'Quantity' are required to add order detail.");
            return;
        }
        String createQuery = "INSERT INTO order_details (product_name, quantity, total_price) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(createQuery);
        preparedStatement.setString(1, productNameTF.getText());
        preparedStatement.setInt(2, Integer.parseInt(quantityTF.getText()));
        preparedStatement.setDouble(3, Double.parseDouble(totalPriceTF.getText()));
        preparedStatement.executeUpdate();
        messageLbl.setTextFill(Color.GREEN);
        messageLbl.setText("Item was created successfully.");
        clearFields();
    }

    private void updateItem(Connection connection) throws SQLException {
    }

    private void deleteItem(Connection connection) throws SQLException {
    }

    private void clearFields() {
        orderIdTF.clear();
        productNameTF.clear();
        quantityTF.clear();
        totalPriceTF.clear();
    }
}
