package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeMenuController {
    public Button btnCustomer;
    public Button btnItems;
    public Button btnOrders;
    public Button btnSearchOrders;
    public AnchorPane root;

    public void btnCustomer_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(this.getClass().getResource("/view/CustomerForm.fxml"));
        Scene customerScene = new Scene(root);
        Stage newStage = (Stage)(this.root.getScene().getWindow());
        newStage.setScene(customerScene);
        newStage.centerOnScreen();
        newStage.show();

    }

    public void btnItems_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(this.getClass().getResource("/view/ItemForm.fxml"));
        Scene itemScene = new Scene(root);
        Stage newStage = (Stage)(this.root.getScene().getWindow());
        newStage.setScene(itemScene);
        newStage.centerOnScreen();
        newStage.show();
    }

    public void btnOrders_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(this.getClass().getResource("/view/PlaceOrders.fxml"));
        Scene itemScene = new Scene(root);
        Stage newStage = (Stage)(this.root.getScene().getWindow());
        newStage.setScene(itemScene);
        newStage.centerOnScreen();
        newStage.show();
    }

    public void btnSearchOrders_OnAction(ActionEvent actionEvent) throws IOException {
        Parent root= FXMLLoader.load(this.getClass().getResource("/view/SearchOrder.fxml"));
        Scene itemScene = new Scene(root);
        Stage newStage = (Stage)(this.root.getScene().getWindow());
        newStage.setScene(itemScene);
        newStage.centerOnScreen();
        newStage.show();
    }
}
