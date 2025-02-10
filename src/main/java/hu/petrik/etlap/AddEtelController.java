package hu.petrik.etlap;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class AddEtelController {

    public TextField kategoriaInput;
    public Spinner<Integer> arInput;
    public TextField leirasInput;
    public TextField nevInput;
    private EtelService service;

    public void initialize() {
        arInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 1000, 100));
    }

    public void setService(EtelService service) {
        this.service = service;
    }

    public void onAddButton(ActionEvent actionEvent) {
        String nev = nevInput.getText();
        String leiras = leirasInput.getText();
        int ar = arInput.getValue();
        String kategoria = kategoriaInput.getText();
        Etel etel = new Etel(-1, nev, leiras, ar, kategoria);
        try {
            service.create(etel);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Siekres felvétel!");
            alert.show();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba!");
            alert.setHeaderText("Hiba történt a felvétel során.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
