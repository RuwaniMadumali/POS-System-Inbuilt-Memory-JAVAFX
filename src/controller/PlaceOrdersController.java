package controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.*;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;


public class PlaceOrdersController {
    public AnchorPane root;
    public Button btnAdd;
    public Button btnPlaceOrder;
    public Button btnPCHome;
    public TableView<PlaceOrderTM> tblOrders;
    public TextField txtOrderID;
    public TextField txtOrderDate;
    public TextField txtCusName;
    public TextField txtDescription;
    public TextField txtQOH;
    public TextField txtUnitPrice;
    public TextField txtQuantity;
    public ComboBox<ItemTM> cmbItemCode;
    public ComboBox<String> cmbCustID;
    public TextField txtNetTotal;
    public Button btnDelete;
    private boolean readOnly = false;


    static ArrayList<Order> ordersDB = new ArrayList<>();


    public void initialize() {
        txtOrderID.setEditable(false);
        txtOrderDate.setEditable(false);
        txtCusName.setEditable(false);
        txtDescription.setEditable(false);
        txtQuantity.setEditable(false);
        txtUnitPrice.setEditable(false);
        btnAdd.setDisable(true);
        btnDelete.setDisable(true);
        cmbCustID.requestFocus();


        //Order
        ObservableList<PlaceOrderTM> orders = tblOrders.getItems();
        if (orders.size() == 0) {
            txtOrderID.setText("OD001");
        }

        //date

        LocalDate today = LocalDate.now();
        txtOrderDate.setText(today.toString());


        ObservableList<String> cmbID = cmbCustID.getItems();
        for (CustomerTM customer : CustomerContoller.customerDB) {
            cmbID.add(customer.getId());
        }
      /*  for (int i = 0; i <CustomerContoller.customerDB.size()-1 ; i++) {
            CustomerTM customer = CustomerContoller.customerDB.get(i);//customer object
            cmbID.add(customer.getId());
        }*/


        cmbCustID.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                for (CustomerTM customer : CustomerContoller.customerDB) {
                    if (customer.getId().equals(newValue)) {
                        txtCusName.setText(customer.getName());

                    }
                } //one method

               /* CustomerTM selectedIndex = CustomerContoller.customerDB.get(cmbCustID.getSelectionModel().getSelectedIndex());
            txtCusName.setText(selectedIndex.getName());*/
            }
        }) ;

        ObservableList<ItemTM> cmbItem = cmbItemCode.getItems();
        for (ItemTM itemTM : MainItemController.itemDB) {
            cmbItem.add(itemTM);
        }

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemTM>() {
            @Override
            public void changed(ObservableValue<? extends ItemTM> observable, ItemTM oldValue, ItemTM newValue) {
                if (newValue == null) {
                    btnAdd.setDisable(true);
                    txtDescription.clear();
                    txtQOH.clear();
                    txtUnitPrice.clear();
                    return;
                }

                txtDescription.setText(newValue.getDescription());
                calculation(newValue);
                txtUnitPrice.setText(String.valueOf(newValue.getUnitPrice()));

                btnAdd.setDisable(false);
                txtQuantity.setEditable(true);
                txtQuantity.requestFocus();

            }
        });

        tblOrders.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblOrders.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrders.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tblOrders.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrders.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));
        tblOrders.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("orderID"));

        tblOrders.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PlaceOrderTM>() {
            @Override
            public void changed(ObservableValue<? extends PlaceOrderTM> observable, PlaceOrderTM oldValue, PlaceOrderTM newValue) {
                PlaceOrderTM selectedItem = tblOrders.getSelectionModel().getSelectedItem();
                if (selectedItem == null) {
                    cmbCustID.setDisable(false);
                    cmbCustID.requestFocus();
                    btnDelete.setDisable(true);
                    btnAdd.setText("ADD");
                    btnAdd.setDisable(true);
                    txtCusName.clear();
                   txtNetTotal.clear();

                    return;

                } else {
//cmbItem kiyanne cmbItemcode eke combo box eka
                    for (ItemTM item : cmbItem) {
                        if (item.getItemCode().equals(selectedItem.getItemCode())) {
                            cmbItemCode.getSelectionModel().select(item);
                            txtDescription.setText(selectedItem.getDescription());
                            updateQOH(selectedItem);
                            txtUnitPrice.setText(String.valueOf(selectedItem.getUnitPrice()));
                            txtQuantity.setText(String.valueOf(selectedItem.getQuantity()));
                            if (!readOnly) {
                                btnAdd.setText("UPDATE");
                                btnDelete.setDisable(false);
                            }
                            if (readOnly) {
                                btnAdd.setDisable(false);
                                txtQOH.setEditable(false);

                            }
                            txtUnitPrice.setEditable(false);
                            Platform.runLater(() -> {
                                txtQuantity.requestFocus();
                            });
                            break;
                        }
                    }


                }
            }
        });


    }

    public void btnAdd_OnAction(ActionEvent actionEvent) {

        ObservableList<PlaceOrderTM> orders = tblOrders.getItems();
        ItemTM selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();

        if (txtQuantity.getText().trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Can not process with empty field", ButtonType.OK).show();
            txtQuantity.requestFocus();
            return;
        }
        String quantity = txtQuantity.getText();
        if (1 > Integer.parseInt(quantity) || Integer.parseInt(quantity) > Integer.parseInt(txtQOH.getText())) {
            new Alert(Alert.AlertType.ERROR, "Invalid Value", ButtonType.OK).show();
            return;
        }

        String selectedCustomers = cmbCustID.getSelectionModel().getSelectedItem();
        if (selectedCustomers == null) {
            new Alert(Alert.AlertType.ERROR, "Please select a customer").show();
            orders.clear();
            cmbCustID.setDisable(false);
            btnDelete.setDisable(false);
            cmbCustID.requestFocus();

            return;
        }


     /*   ItemTM selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();
        ObservableList<PlaceOrderTM> order = tblOrders.getItems();
        String itemCode = selectedItem.getItemCode();
        System.out.println(itemCode);
        String description = selectedItem.getDescription();
        double unitPrice = selectedItem.getUnitPrice();
        double total = Double.parseDouble(quantity) * unitPrice;
        String orderID = txtOrderID.getText();*/

        if (btnAdd.getText().equals("ADD")) {

            ObservableList<PlaceOrderTM> order = tblOrders.getItems();
            boolean exit = false;
            for (PlaceOrderTM ordersDetails : order) {
                if (ordersDetails.getItemCode().equals(selectedItem.getItemCode())) {
                    exit = true;
                    ordersDetails.setQuantity(ordersDetails.getQuantity() + Integer.parseInt(quantity));
                    break;
                }
            }

            if (!exit) {
                order.add(new PlaceOrderTM(selectedItem.getItemCode(), selectedItem.getDescription(), Integer.parseInt(quantity), selectedItem.getUnitPrice(), (Double.parseDouble(quantity) * selectedItem.getUnitPrice()), txtOrderID.getText()));
            }
            calculateTotal();
            tblOrders.refresh();
            cmbItemCode.getSelectionModel().clearSelection();
            cmbCustID.setDisable(true);
            txtNetTotal.setEditable(false);
            txtQuantity.clear();
            cmbItemCode.requestFocus();

        } else {

            int selectedIndex = tblOrders.getSelectionModel().getSelectedIndex();
            if (orders.size() == 0) {
                return;
            }
            orders.get(selectedIndex).setQuantity(Integer.parseInt(txtQuantity.getText()));
            orders.get(selectedIndex).setTotal((Double.parseDouble(txtUnitPrice.getText())) * (Integer.parseInt(txtQuantity.getText())));
            calculateTotal();

            tblOrders.refresh();
            tblOrders.getSelectionModel().clearSelection();
            cmbItemCode.getSelectionModel().clearSelection();
            txtQuantity.clear();
            btnAdd.setText("SAVE");
            btnAdd.setDisable(true);
            btnDelete.setDisable(false);
            cmbItemCode.requestFocus();


        }
    }


    public void btnPlaceOrder_OnAction(ActionEvent actionEvent) {
        ObservableList<PlaceOrderTM> orders = tblOrders.getItems();
        PlaceOrderTM orderDetail = tblOrders.getSelectionModel().getSelectedItem();

        //validation
        if (orders.size() == 0) {
            new Alert(Alert.AlertType.ERROR, "Please enter a one or more item", ButtonType.OK).show();
            cmbItemCode.getSelectionModel().clearSelection();
            txtQuantity.clear();
            cmbCustID.requestFocus();
            return;
        }

        ArrayList<OrderDetail> alOrderDetails = new ArrayList<>();
        ObservableList<PlaceOrderTM> olOrderDetails = tblOrders.getItems();
        for (PlaceOrderTM orderDetails : olOrderDetails) {
            // Let's update the stock
            updateStockQty(orderDetails.getItemCode(), orderDetails.getQuantity());
            alOrderDetails.add(new OrderDetail(orderDetails.getItemCode(), orderDetails.getQuantity(), orderDetails.getUnitPrice()));

        }

        String selectedCustomers = cmbCustID.getSelectionModel().getSelectedItem();
        for (CustomerTM customerDetails : CustomerContoller.customerDB) {
            if (customerDetails.getId().equals(selectedCustomers)) {
                Order newOrder = new Order(txtOrderID.getText(), LocalDate.now(), customerDetails.getId(), alOrderDetails);
                ordersDB.add(newOrder);
            }
        }


        PlaceOrderTM lastOrder = orders.get(orders.size() - 1);
        String lastOrderID = lastOrder.getOrderId();
        String substring = lastOrderID.substring(2, 5);
        int id = Integer.parseInt(substring) + 1;
        if (id < 10) {
            txtOrderID.setText("OD00" + id);
        } else if (id < 100) {
            txtOrderID.setText("OD0" + id);
        } else {
            txtOrderID.setText("OD" + id);
        }


        new Alert(Alert.AlertType.INFORMATION, "Your order is successfully created", ButtonType.OK).showAndWait();


        orders.clear();
        tblOrders.getSelectionModel().clearSelection();
        cmbCustID.getSelectionModel().clearSelection();
        txtCusName.clear();
        txtNetTotal.clear();
        cmbCustID.setDisable(false);
        cmbCustID.requestFocus();

    }

    private void updateStockQty(String itemCode, int qty) {
        for (ItemTM item : MainItemController.itemDB) {
            if (item.getItemCode().equals(itemCode)) {
                item.setQtyOnHand(item.getQtyOnHand() - qty);
                return;
            }
        }
    }


    public void cmbItemCode_OnAction(ActionEvent actionEvent) {
    }

    public void cmbCustID_OnAction(ActionEvent actionEvent) {
    }


    void calculation(ItemTM itemTM) {
        txtQOH.setText(String.valueOf(itemTM.getQtyOnHand()));
        for (PlaceOrderTM item : tblOrders.getItems()) {
            if (item.getItemCode().equals(itemTM.getItemCode())) {
                txtQOH.setText(String.valueOf(itemTM.getQtyOnHand() - item.getQuantity()));


                break;
            }
        }

    }

    void updateQOH(PlaceOrderTM value) {
        ObservableList<ItemTM> items = cmbItemCode.getItems();
        for (ItemTM item : MainItemController.itemDB) {
            if (item.getItemCode().equals(value.getItemCode())) {
                txtQOH.setText(String.valueOf(item.getQtyOnHand()));
                break;
            }
        }

    }

    public void calculateTotal() {
        ObservableList<PlaceOrderTM> orderDetails = tblOrders.getItems();
        double netTotal = 0;
        for (PlaceOrderTM orderDetail : orderDetails) {
            netTotal += orderDetail.getTotal();
        }
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(2);
        numberInstance.setMinimumFractionDigits(2);
        numberInstance.setGroupingUsed(false);
        String formattedText = numberInstance.format(netTotal);
        txtNetTotal.setText(formattedText);
    }


    public void btnPCHome_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/WelcomeMenu.fxml"));
        Scene itemScene = new Scene(root);
        Stage newStage = (Stage) (this.root.getScene().getWindow());
        newStage.setScene(itemScene);
        newStage.centerOnScreen();
        newStage.show();
    }


    public void quantity_OnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            btnAdd.requestFocus();
            if (txtQuantity.getText().trim().isEmpty()) {
                new Alert(Alert.AlertType.ERROR, "Can not process with empty field", ButtonType.OK).show();
                return;
            }
            String quantity = txtQuantity.getText();
            if (1 > Integer.parseInt(quantity) || Integer.parseInt(quantity) > Integer.parseInt(txtQOH.getText())) {
                new Alert(Alert.AlertType.ERROR, "Invalid Value", ButtonType.OK).show();
                return;
            }

            ItemTM selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();
            ObservableList<PlaceOrderTM> order = tblOrders.getItems();
            boolean exit = false;
            for (PlaceOrderTM orders : order) {
                if (orders.getItemCode().equals(selectedItem.getItemCode())) {
                    exit = true;
                    orders.setQuantity(orders.getQuantity() + Integer.parseInt(txtQuantity.getText()));
                    break;
                }
            }

            if (!exit) {
                order.add(new PlaceOrderTM(selectedItem.getItemCode(), selectedItem.getDescription(), Integer.parseInt(quantity), selectedItem.getUnitPrice(), (Double.parseDouble(quantity) * selectedItem.getUnitPrice()), txtOrderID.getText()));
            }
            calculateTotal();
            tblOrders.refresh();
            cmbItemCode.getSelectionModel().clearSelection();
            cmbCustID.setDisable(true);
            txtQuantity.clear();
            cmbItemCode.requestFocus();
        }
    }

    void initializeWithSearchOrderForm(String orderId) {

        txtOrderID.setText(orderId);
        readOnly = true;
//       cmbCustID.getSelectionModel().select(customerId);

        for (Order order : ordersDB) {
            if (order.getId().equals(orderId)) {
                txtOrderDate.setText(order.getDate().toString());
            }


            String customer = order.getCustomerId();
            for (String item : cmbCustID.getItems()) {
                if (item.equals(customer)) {
                    cmbCustID.getSelectionModel().select(item);
                    break;

                }

            }
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                String description = null;
                for (ItemTM item : cmbItemCode.getItems()) {
                    if (item.getItemCode().equals(orderDetail.getCode())) {
                        description = item.getDescription();
                        break;
                    }
                }

                PlaceOrderTM orderDetails = new PlaceOrderTM(order.getId(),
                        description,
                        orderDetail.getQty(),
                        orderDetail.getUnitPrice(),
                        (orderDetail.getQty() * orderDetail.getUnitPrice()),
                        orderId);
                tblOrders.getItems().add(orderDetails);
                calculateTotal();
            }
            cmbItemCode.setDisable(true);
            cmbCustID.setDisable(true);
            btnAdd.setDisable(true);
            btnPlaceOrder.setVisible(false);
            txtNetTotal.setEditable(false);
            break;


        }
    }


    public void btnDelete_OnAction(ActionEvent actionEvent) {
        ObservableList<PlaceOrderTM> orderedItems = tblOrders.getItems();
        PlaceOrderTM selectedItem = tblOrders.getSelectionModel().getSelectedItem();
        if(selectedItem==null){
            btnDelete.setDisable(true);
            return;
        }else{
            orderedItems.remove(selectedItem);
        cmbCustID.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
        txtQuantity.clear();


        }



    }
}


