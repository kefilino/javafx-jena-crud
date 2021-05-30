package controller;

import library.Enrollment;
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

public class EnrollmentController implements Initializable {
    private RDFConnectionRemoteBuilder builder = RDFConnectionFuseki.create()
            .destination("http://localhost:3030/enrollment");

    private final String prefix = "PREFIX enrollment: <http://kefilino.me/ns/enrollment#>\n"
        + "PREFIX mahasiswa: <http://kefilino.me/ns/mahasiswa#>\n"
        + "PREFIX matakuliah: <http://kefilino.me/ns/mahasiswa#>";

    @FXML
    private TextField idField;

    @FXML
    private TextField npmField;

    @FXML
    private TextField kdmatkulField;

    @FXML
    private TextField tahunField;

    @FXML
    private Button insertButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<Enrollment> TableView;

    @FXML
    private TableColumn<Enrollment, String> idColumn;

    @FXML
    private TableColumn<Enrollment, String> npmColumn;

    @FXML
    private TableColumn<Enrollment, String> kdmatkulColumn;

    @FXML
    private TableColumn<Enrollment, Integer> tahunColumn;

    @FXML
    private void insertButton() {
        RDFConnectionFuseki connection = getConnection();
        String query = prefix.concat(
                " INSERT DATA { "
                + "enrollment:" + idField.getText() + "  mahasiswa:npm \"" + npmField.getText() + "\" ; "
                + "matakuliah:kdmatkul \"" + kdmatkulField.getText() + "\" ; "
                + "enrollment:tahun " + tahunField.getText() + " . } ");
        UpdateRequest request = UpdateFactory.create();
        System.out.println(query);

        request.add(query);
        connection.update(request);

        showEnrollment();
    }

    @FXML
    private void updateButton() {
        RDFConnectionFuseki connection = getConnection();
        String query = prefix.concat(
                " DELETE WHERE { enrollment:" + idField.getText() + " ?p ?o . } ; "
                + "INSERT DATA { "
                + "enrollment:" + idField.getText() + "  mahasiswa:npm \"" + npmField.getText() + "\" ; "
                + "matakuliah:kdmatkul \"" + kdmatkulField.getText() + "\" ; "
                + "enrollment:tahun " + tahunField.getText() + " . } ");
        UpdateRequest request = UpdateFactory.create();
        System.out.println(query);

        request.add(query);
        connection.update(request);

        showEnrollment();
    }

    @FXML
    private void deleteButton() {
        RDFConnectionFuseki connection = getConnection();
        String query = prefix.concat(
                "DELETE WHERE { enrollment:" + idField.getText() + " ?p ?o . } ");
        UpdateRequest request = UpdateFactory.create();

        request.add(query);
        connection.update(request);

        showEnrollment();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showEnrollment();

        TableView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Enrollment>() {
            @Override
            public void onChanged(Change<? extends Enrollment> change) {
                try {
                    idField.setText(change.getList().get(0).getId());
                    npmField.setText(change.getList().get(0).getNpm());
                    kdmatkulField.setText(change.getList().get(0).getKdmatkul());
                    tahunField.setText(String.valueOf(change.getList().get(0).getTahun()));
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
    
    public ObservableList<Enrollment> getEnrollmentList() {
        ObservableList<Enrollment> enrollmentList = FXCollections.observableArrayList();
        RDFConnectionFuseki connection = getConnection();
        String query = prefix.concat(
                " SELECT (strafter(str(?s),'#') as ?id) ?npm ?kdmatkul (str(?tahun) as ?tahunStr) "
                + "WHERE { ?s mahasiswa:npm ?npm ;" + "matakuliah:kdmatkul ?kdmatkul ;"
                + "enrollment:tahun ?tahun . }");
        QueryExecution qExec;
        ResultSet rs;

        try {
            qExec = connection.query(query);
            rs = qExec.execSelect();
            Enrollment enrollment;

            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                enrollment = new Enrollment(soln.get("?id").toString(), soln.get("?npm").toString(),
                        soln.get("?kdmatkul").toString(), Integer.parseInt(soln.get("?tahunStr").toString()));
                enrollmentList.add(enrollment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enrollmentList;
    }

    public void showEnrollment() {
        ObservableList<Enrollment> list = getEnrollmentList();

        idColumn.setCellValueFactory(new PropertyValueFactory<Enrollment, String>("id"));
        npmColumn.setCellValueFactory(new PropertyValueFactory<Enrollment, String>("npm"));
        kdmatkulColumn.setCellValueFactory(new PropertyValueFactory<Enrollment, String>("kdmatkul"));
        tahunColumn.setCellValueFactory(new PropertyValueFactory<Enrollment, Integer>("tahun"));

        TableView.getItems().clear();
        TableView.setItems(list);
    }
}
