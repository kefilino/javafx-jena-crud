package controller;

import library.Mahasiswa;
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

public class MainController implements Initializable {
    private RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create()
            .destination("http://localhost:3030/mahasiswa");

    private final String prefix = "PREFIX mahasiswa: <http://kefilino.me/ns/mahasiswa#>";

    @FXML
    private TextField npmField;

    @FXML
    private TextField namaField;

    @FXML
    private TextField angkatanField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telpField;

    @FXML
    private Button insertButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<Mahasiswa> TableView;

    @FXML
    private TableColumn<Mahasiswa, String> npmColumn;

    @FXML
    private TableColumn<Mahasiswa, String> namaColumn;

    @FXML
    private TableColumn<Mahasiswa, Integer> angkatanColumn;

    @FXML
    private TableColumn<Mahasiswa, String> emailColumn;

    @FXML
    private TableColumn<Mahasiswa, String> telpColumn;

    @FXML
    private void insertButton() {
        RDFConnectionFuseki connection = getConnection();
        String query = prefix.concat(
                " INSERT DATA { "
                + "mahasiswa:" + npmField.getText() + "  mahasiswa:nama \"" + namaField.getText() + "\" ;"
                + "mahasiswa:angkatan " + angkatanField.getText() + " ;"
                + "mahasiswa:email \"" + emailField.getText() + "\" ;"
                + "mahasiswa:telp \"" + telpField.getText() + "\" ; } ");
        UpdateRequest request = UpdateFactory.create();

        request.add(query);
        connection.update(request);

        showMahasiswa();
    }

    @FXML
    private void updateButton() {
        RDFConnectionFuseki connection = getConnection();
        String query = "PREFIX mahasiswa: <http://kefilino.me/ns/mahasiswa#> "
                + "DELETE WHERE { mahasiswa:" + npmField.getText() + " ?p ?o . } ; "
                + "INSERT DATA { "
                + "mahasiswa:" + npmField.getText() + "  mahasiswa:nama \"" + namaField.getText() + "\" ;"
                + "mahasiswa:angkatan " + angkatanField.getText() + " ;"
                + "mahasiswa:email \"" + emailField.getText() + "\" ;"
                + "mahasiswa:telp \"" + telpField.getText() + "\" . } ";
        UpdateRequest request = UpdateFactory.create();

        request.add(query);
        connection.update(request);

        showMahasiswa();
    }

    @FXML
    private void deleteButton() {
        RDFConnectionFuseki connection = getConnection();
        String query = "PREFIX mahasiswa: <http://kefilino.me/ns/mahasiswa#> "
                + "DELETE WHERE { mahasiswa:" + npmField.getText() + " ?p ?o . } ";
        UpdateRequest request = UpdateFactory.create();

        request.add(query);
        connection.update(request);

        showMahasiswa();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showMahasiswa();

        TableView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Mahasiswa>() {
            @Override
            public void onChanged(Change<? extends Mahasiswa> change) {
                try {
                    npmField.setText(change.getList().get(0).getNpm());
                    namaField.setText(change.getList().get(0).getNama());
                    angkatanField.setText(String.valueOf(change.getList().get(0).getAngkatan()));
                    emailField.setText(change.getList().get(0).getEmail());
                    telpField.setText(change.getList().get(0).getTelp());
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Selected item not found.");
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

    public ObservableList<Mahasiswa> getMahasiswaList() {
        ObservableList<Mahasiswa> mahasiswaList = FXCollections.observableArrayList();
        RDFConnectionFuseki connection = getConnection();
        String query = prefix.concat(
                " SELECT (strafter(str(?s),'#') as ?npm) ?nama (str(?angkatan) as ?tahun) ?email ?telp " + "WHERE { "
                + "?s mahasiswa:nama ?nama ;" + "mahasiswa:angkatan ?angkatan ;"
                + "mahasiswa:email ?email ;" + "mahasiswa:telp ?telp ." + " }");
        QueryExecution qExec;
        ResultSet rs;

        try {
            qExec = connection.query(query);
            rs = qExec.execSelect();
            Mahasiswa mahasiswa;

            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                mahasiswa = new Mahasiswa(soln.get("?npm").toString(), soln.get("?nama").toString(),
                        Integer.parseInt(soln.get("?tahun").toString()), soln.get("?email").toString(),
                        soln.get("?telp").toString());
                mahasiswaList.add(mahasiswa);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mahasiswaList;
    }

    public void showMahasiswa() {
        ObservableList<Mahasiswa> list = getMahasiswaList();

        npmColumn.setCellValueFactory(new PropertyValueFactory<Mahasiswa, String>("npm"));
        namaColumn.setCellValueFactory(new PropertyValueFactory<Mahasiswa, String>("nama"));
        angkatanColumn.setCellValueFactory(new PropertyValueFactory<Mahasiswa, Integer>("angkatan"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<Mahasiswa, String>("email"));
        telpColumn.setCellValueFactory(new PropertyValueFactory<Mahasiswa, String>("telp"));

        TableView.getItems().clear();
        TableView.setItems(list);
    }
}
