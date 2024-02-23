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
import javafx.scene.control.cell.PropertyValueFactory;
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
    protected void initialize() {
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        orderTbl.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton().equals(javafx.scene.input.MouseButton.PRIMARY)) {
                OrderDetails selectedItem = orderTbl.getSelectionModel().getSelectedItem();
                orderIdTF.setText(String.valueOf(selectedItem.getOrderId()));
                productNameTF.setText(selectedItem.getProductName());
                quantityTF.setText(String.valueOf(selectedItem.getQuantity()));
                totalPriceTF.setText(String.valueOf(selectedItem.getTotalPrice()));
            }
        });


        //restrict idTF to accept only integer values
        orderIdTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^([1-9]\\d*)?$")) {
                orderIdTF.setText(oldValue);
            }
        });

        //restrict quantityTF to accept only integer values
        quantityTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^([1-9]\\d*)?$")) {
                quantityTF.setText(oldValue);
            }
        });

        //restrict totalPriceTF to accept only decimal values
        totalPriceTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^([0-9]+([,.][0-9]{0,2})?)?$")) {
                totalPriceTF.setText(oldValue);
            }
        });


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
        PreparedStatement preparedStatement = connection.prepareStatement(createQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, productNameTF.getText());
        preparedStatement.setInt(2, Integer.parseInt(quantityTF.getText()));
        preparedStatement.setDouble(3, Double.parseDouble(totalPriceTF.getText()));
        preparedStatement.executeUpdate();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        if (generatedKeys.next()) {
            orderTbl.getItems().add(new OrderDetails(
                    generatedKeys.getLong(1),
                    productNameTF.getText(),
                    Integer.parseInt(quantityTF.getText()),
                    Double.parseDouble(totalPriceTF.getText())
            ));
        }
        messageLbl.setTextFill(Color.GREEN);
        messageLbl.setText("Item was created successfully.");
        clearFields();
    }

    private void updateItem(Connection connection) throws SQLException {
        // validate if id is empty
        if (orderIdTF.getText().isEmpty()) {
            messageLbl.setTextFill(Color.RED);
            messageLbl.setText("'Id' should not be empty when updating order detail.");
            return;
        }
        // validate if name, price, and quantity are empty
        if (productNameTF.getText().isEmpty() || quantityTF.getText().isEmpty() || totalPriceTF.getText().isEmpty()) {
            messageLbl.setTextFill(Color.RED);
            messageLbl.setText("'Name', 'Price', and 'Quantity' are required to update order detail.");
            return;
        }
        String updateQuery = "UPDATE order_details SET product_name = ?, quantity = ?, total_price = ? WHERE order_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
        preparedStatement.setString(1, productNameTF.getText());
        preparedStatement.setInt(2, Integer.parseInt(quantityTF.getText()));
        preparedStatement.setDouble(3, Double.parseDouble(totalPriceTF.getText()));
        preparedStatement.setLong(4, Long.parseLong(orderIdTF.getText()));
        preparedStatement.executeUpdate();

        orderTbl.getItems().stream()
                .filter(item -> item.getOrderId() == Long.parseLong(orderIdTF.getText()))
                .forEach(item -> {
                    item.setProductName(productNameTF.getText());
                    item.setQuantity(Integer.parseInt(quantityTF.getText()));
                    item.setTotalPrice(Double.parseDouble(totalPriceTF.getText()));
                    orderTbl.refresh();
                });

        messageLbl.setTextFill(Color.GREEN);
        messageLbl.setText("Item was updated successfully.");
    }

    private void deleteItem(Connection connection) throws SQLException {
        // validate if id is empty
        if (orderIdTF.getText().isEmpty()) {
            messageLbl.setTextFill(Color.RED);
            messageLbl.setText("'Id' should not be empty when deleting order detail.");
            return;
        }
        String deleteQuery = "DELETE FROM order_details WHERE order_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
        preparedStatement.setLong(1, Long.parseLong(orderIdTF.getText()));
        preparedStatement.executeUpdate();

        orderTbl.getItems().removeIf(item -> item.getOrderId() == Long.parseLong(orderIdTF.getText()));
        messageLbl.setTextFill(Color.GREEN);
        messageLbl.setText("Item was deleted successfully.");
        clearFields();
    }

    private void clearFields() {
        orderIdTF.clear();
        productNameTF.clear();
        quantityTF.clear();
        totalPriceTF.clear();
    }
}
