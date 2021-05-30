package controller;

import library.Dosen;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;

public class DosenController implements Initializable {
    private RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create()
            .destination("http://localhost:3030/dosen");

    private final String prefix = "PREFIX dosen: <http://kefilino.me/ns/dosen#>";

    @FXML
    private TextField nipField;

    @FXML
    private TextField namaField;

    @FXML
    private Button insertButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<Dosen> TableView;

    @FXML
    private TableColumn<Dosen, String> nipColumn;

    @FXML
    private TableColumn<Dosen, String> namaColumn;

    @FXML
    private void insertButton() {
        RDFConnectionFuseki connection = getConnection();
        String query = prefix.concat(
                " INSERT DATA { "
                + "dosen:" + nipField.getText() + "  dosen:nama \"" + namaField.getText() + "\" . }");
        UpdateRequest request = UpdateFactory.create();

        request.add(query);
        connection.update(request);

        connection.close();
        showDosen();
    }

    @FXML
    private void updateButton() {
        RDFConnectionFuseki connection = getConnection();
        String query = prefix.concat(
                " DELETE WHERE { dosen:" + nipField.getText() + " ?p ?o . } ; "
                + "INSERT DATA { "
                + "dosen:" + nipField.getText() + "  dosen:nama \"" + namaField.getText() + "\" . } ");
        UpdateRequest request = UpdateFactory.create();

        request.add(query);
        connection.update(request);

        connection.close();
        showDosen();
    }

    @FXML
    private void deleteButton() {
        RDFConnectionFuseki connection = getConnection();
        String query = prefix.concat(
                " DELETE WHERE { dosen:" + nipField.getText() + " ?p ?o . } ");
        UpdateRequest request = UpdateFactory.create();

        request.add(query);
        connection.update(request);

        connection.close();
        showDosen();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showDosen();

        TableView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Dosen>() {
            @Override
            public void onChanged(Change<? extends Dosen> change) {
                try {
                    nipField.setText(change.getList().get(0).getNip());
                    namaField.setText(change.getList().get(0).getNama());
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Selected item not found.");
                    nipField.setText("");
                    namaField.setText("");
                }
            }
        });
    }

    public RDFConnectionFuseki getConnection() {
        try {
            return (RDFConnectionFuseki) builder.build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ObservableList<Dosen> getDosenList() {
        ObservableList<Dosen> dosenList = FXCollections.observableArrayList();
        RDFConnectionFuseki connection = getConnection();
        String query = prefix.concat(
                " SELECT (strafter(str(?s),'#') as ?nip) ?nama "
                + "WHERE { ?s dosen:nama ?nama . } ");
        QueryExecution qExec;
        ResultSet rs;

        try {
            qExec = connection.query(query);
            rs = qExec.execSelect();
            Dosen dosen;

            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                dosen = new Dosen(soln.get("?nip").toString(), soln.get("?nama").toString());
                dosenList.add(dosen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        connection.close();
        return dosenList;
    }

    public void showDosen() {
        ObservableList<Dosen> list = getDosenList();

        nipColumn.setCellValueFactory(new PropertyValueFactory<Dosen, String>("nip"));
        namaColumn.setCellValueFactory(new PropertyValueFactory<Dosen, String>("nama"));

        TableView.setItems(list);
    }
}
