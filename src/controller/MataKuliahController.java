package controller;

import library.MataKuliah;
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

public class MataKuliahController implements Initializable {
    private RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create()
            .destination("http://localhost:3030/matakuliah");

    private final String prefix = "PREFIX matakuliah: <http://kefilino.me/ns/matakuliah#>";

    @FXML
    private TextField kdmatkulField;

    @FXML
    private TextField namaField;

    @FXML
    private TextField pengajarField;

    @FXML
    private TextField sksField;

    @FXML
    private Button insertButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<MataKuliah> TableView;

    @FXML
    private TableColumn<MataKuliah, String> kdmatkulColumn;

    @FXML
    private TableColumn<MataKuliah, String> namaColumn;

    @FXML
    private TableColumn<MataKuliah, String> pengajarColumn;

    @FXML
    private TableColumn<MataKuliah, Integer> sksColumn;

    @FXML
    private void insertButton() {
        RDFConnectionFuseki connection = getConnection();
        String query = prefix.concat(
                " INSERT DATA { "
                + "matakuliah:" + kdmatkulField.getText() + "  matakuliah:nama \"" + namaField.getText() + "\" ; "
                + "matakuliah:pengajar \"" + pengajarField.getText() + "\" ; "
                + "matakuliah:sks " + sksField.getText() + " . } ");
        UpdateRequest request = UpdateFactory.create();
        System.out.println(query);

        request.add(query);
        connection.update(request);

        showMataKuliah();
    }

    @FXML
    private void updateButton() {
        RDFConnectionFuseki connection = getConnection();
        String query = prefix.concat(
                " DELETE WHERE { matakuliah:" + kdmatkulField.getText() + " ?p ?o . } ; "
                + "INSERT DATA { "
                + "matakuliah:" + kdmatkulField.getText() + "  matakuliah:nama \"" + namaField.getText() + "\" ; "
                + "matakuliah:pengajar \"" + pengajarField.getText() + "\" ; "
                + "matakuliah:sks " + sksField.getText() + " . } ");
        UpdateRequest request = UpdateFactory.create();
        System.out.println(query);

        request.add(query);
        connection.update(request);

        showMataKuliah();
    }

    @FXML
    private void deleteButton() {
        RDFConnectionFuseki connection = getConnection();
        String query = prefix.concat(
                "DELETE WHERE { matakuliah:" + kdmatkulField.getText() + " ?p ?o . } ");
        UpdateRequest request = UpdateFactory.create();

        request.add(query);
        connection.update(request);

        showMataKuliah();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showMataKuliah();

        TableView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<MataKuliah>() {
            @Override
            public void onChanged(Change<? extends MataKuliah> change) {
                try {
                    kdmatkulField.setText(change.getList().get(0).getKdmatkul());
                    namaField.setText(change.getList().get(0).getNama());
                    pengajarField.setText(change.getList().get(0).getPengajar());
                    sksField.setText(String.valueOf(change.getList().get(0).getSks()));
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

    public ObservableList<MataKuliah> getMataKuliahList() {
        ObservableList<MataKuliah> mahasiswaList = FXCollections.observableArrayList();
        RDFConnectionFuseki connection = getConnection();
        String query = prefix.concat(
                " SELECT (strafter(str(?s),'#') as ?kdmatkul) ?nama ?pengajar (str(?sks) as ?sksStr) "
                + "WHERE { ?s matakuliah:nama ?nama ;" + "matakuliah:pengajar ?pengajar ;"
                + "matakuliah:sks ?sks . }");
        QueryExecution qExec;
        ResultSet rs;

        try {
            qExec = connection.query(query);
            rs = qExec.execSelect();
            MataKuliah mahasiswa;

            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                mahasiswa = new MataKuliah(soln.get("?kdmatkul").toString(), soln.get("?nama").toString(),
                        soln.get("?pengajar").toString(), Integer.parseInt(soln.get("?sksStr").toString()));
                mahasiswaList.add(mahasiswa);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mahasiswaList;
    }

    public void showMataKuliah() {
        ObservableList<MataKuliah> list = getMataKuliahList();

        kdmatkulColumn.setCellValueFactory(new PropertyValueFactory<MataKuliah, String>("kdmatkul"));
        namaColumn.setCellValueFactory(new PropertyValueFactory<MataKuliah, String>("nama"));
        pengajarColumn.setCellValueFactory(new PropertyValueFactory<MataKuliah, String>("pengajar"));
        sksColumn.setCellValueFactory(new PropertyValueFactory<MataKuliah, Integer>("sks"));

        TableView.getItems().clear();
        TableView.setItems(list);
    }
}
