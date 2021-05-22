package controller;

import library.Mahasiswa;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import java.util.ResourceBundle;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdfconnection.RDFConnectionFuseki;
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder;

public class MainController implements Initializable {
    private RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create()
            .destination("http://localhost:3030/mahasiswa");

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
        // String query = "insert into mahasiswa values(" + npmField.getText() + ",'" +
        // namaField.getText() + "','"
        // + angkatanField.getText() + "'," + emailField.getText() + "," +
        // telpField.getText() + ")";
        // executeQuery(query);
        // showMahasiswa();
    }

    @FXML
    private void updateButton() {
        // String query = "UPDATE mahasiswa SET Title='" + namaField.getText() +
        // "',Author='" + angkatanField.getText()
        // + "',Year=" + emailField.getText() + ",Pages=" + telpField.getText() + "
        // WHERE ID=" + npmField.getText()
        // + "";
        // executeQuery(query);
        // showMahasiswa();
    }

    @FXML
    private void deleteButton() {
        // String query = "DELETE FROM mahasiswa WHERE ID=" + npmField.getText() + "";
        // executeQuery(query);
        // showMahasiswa();
    }

    // public void executeQuery(String query) {
    // Connection conn = getConnection();
    // Statement st;
    // try {
    // st = conn.createStatement();
    // st.executeUpdate(query);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showMahasiswa();
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
        String query = "PREFIX mahasiswa: <http://kefilino.me/ns/mahasiswa#> "
                + "SELECT ?npm ?nama (str(?angkatan) as ?tahun) ?email ?telp " + "WHERE { "
                + "?subject mahasiswa:npm ?npm ;" + "mahasiswa:nama ?nama ;" + "mahasiswa:angkatan ?angkatan ;"
                + "mahasiswa:email ?email ;" + "mahasiswa:telp ?telp ." + " }";
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

    // I had to change ArrayList to ObservableList I didn't find another option to
    // do this but this works :)
    public void showMahasiswa() {
        ObservableList<Mahasiswa> list = getMahasiswaList();

        npmColumn.setCellValueFactory(new PropertyValueFactory<Mahasiswa, String>("npm"));
        namaColumn.setCellValueFactory(new PropertyValueFactory<Mahasiswa, String>("nama"));
        angkatanColumn.setCellValueFactory(new PropertyValueFactory<Mahasiswa, Integer>("angkatan"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<Mahasiswa, String>("email"));
        telpColumn.setCellValueFactory(new PropertyValueFactory<Mahasiswa, String>("telp"));

        TableView.setItems(list);
    }

}
