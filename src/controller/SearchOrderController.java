package controller;


import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.CustomerTM;
import util.Order;
import util.OrderDetail;
import util.OrderTM;
import  util.*;
import java.io.IOException;
import java.util.ArrayList;


public class SearchOrderController {
    public TextField txtSearch;
    public Button btnHome;
    public TableView<OrderTM> tblSearchDetails;
    public AnchorPane root;

    ArrayList<OrderTM> SearchOrderCopy = new ArrayList<>();


    public void initialize() {

        for (Order order : PlaceOrdersController.ordersDB) {
            OrderTM searchOrders = new OrderTM(order.getId(), order.getDate(), order.getCustomerId(), getCustomerName(order.getCustomerId()), getNetTotal(order.getOrderDetails()));
            SearchOrderCopy.add(searchOrders);
            tblSearchDetails.getItems().add(searchOrders);
          tblSearchDetails.refresh();
        }

        tblSearchDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("orderId"));
        tblSearchDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        tblSearchDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("CustomerId"));
        tblSearchDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("CustomerName"));
        tblSearchDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("orderTotal"));

       /* ObservableList<SearchTM> searchList = tblSearchDetails.getItems();
        searchList.add(new SearchTM("CD001","2020-02-29","C001","ASANKA",700.00));*/

        txtSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ObservableList<OrderTM> searchOrders = tblSearchDetails.getItems();
                searchOrders.clear();
                for (OrderTM order : SearchOrderCopy) {
                    if ((order.getOrderId().contains(newValue)||
                            order.getCustomerId().contains(newValue) ||
                            order.getCustomerName().contains(newValue) ||
                            order.getOrderDate().toString().contains(newValue)) ||
                    String.valueOf(order.getOrderTotal()).contains(newValue)){
                        searchOrders.add(order);
                    }
                }
            }
        });

    }



    private String getCustomerName(String customerId) {
        for (CustomerTM customerDetails : CustomerContoller.customerDB) {
            if (customerDetails.getId().equals(customerId)) {
                return customerDetails.getName();
            }
        }
        return null;
    }

    private double getNetTotal(ArrayList<OrderDetail> orderDetails) {
        double total = 0;
        for (OrderDetail orderDetail : orderDetails) {
            total += (orderDetail.getQty() * orderDetail.getUnitPrice());

        }
        return total;
    }


    public void btnHome_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/WelcomeMenu.fxml"));
        Scene itemScene = new Scene(root);
        Stage newStage = (Stage) (this.root.getScene().getWindow());
        newStage.setScene(itemScene);
        newStage.centerOnScreen();
        newStage.show();
    }


    public void tblSearchDetails_OnMouseClicked(MouseEvent mouseEvent) throws IOException {
        OrderTM doubleClickedOrder = tblSearchDetails.getSelectionModel().getSelectedItem();


        if(doubleClickedOrder==null){
            return;
        }if(mouseEvent.getClickCount()==2){
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/PlaceOrders.fxml"));
            Parent root=fxmlLoader.load();
            PlaceOrdersController controller = (PlaceOrdersController) fxmlLoader.getController();
            controller.initializeWithSearchOrderForm(doubleClickedOrder.getOrderId());
            Scene searchScene=new Scene(root);
            Stage searchStage=new Stage();
            searchStage.setScene(searchScene);
            searchStage.isResizable();
            searchStage.show();





        }

    }
}
