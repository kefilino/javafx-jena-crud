package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML private TabPane tabPane;
    // Inject tab content.
    @FXML private Tab mahasiswaPane;
    // Inject controller
    @FXML private MahasiswaController mahasiswaController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //
    }
}
