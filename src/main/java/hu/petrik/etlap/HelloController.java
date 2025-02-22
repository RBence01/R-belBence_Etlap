package hu.petrik.etlap;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class HelloController {
    @FXML public TableColumn<Etel, String> nameCol;
    @FXML public TableColumn<Etel, String> categoryCol;
    @FXML public TableColumn<Etel, Integer> priceCol;
    @FXML public TableView<Etel> etlapTable;
    @FXML public Label leiras;
    @FXML public Spinner<Integer> ftIncField;
    @FXML public Spinner<Integer> perIncField;

    private EtelService service;

    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("nev"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("kategoria"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("ar"));
        ftIncField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(50, 3000, 50, 50));
        perIncField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 5, 5));

        etlapTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldEtel, newEtel) -> {
            if (newEtel != null) leiras.setText(newEtel.getLeiras());
            else leiras.setText("");
        });

        try {
            service = new EtelService();
            ListEtel();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba!");
            alert.setHeaderText("Nem sikerült kapcsolodni az adatbázishoz.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            Platform.exit();
        }
    }

    public void ListEtel() throws SQLException {
        etlapTable.getItems().clear();
        etlapTable.getItems().addAll(service.getAll());
    }

    @FXML
    public void addButton(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("add-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 300);
            AddEtelController controller = fxmlLoader.getController();
            controller.setService(service);
            stage.setTitle("Új étel hozzáadása");
            stage.setScene(scene);
            stage.setOnHiding(e -> {
                try {
                    ListEtel();
                } catch (SQLException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Hiba!");
                    alert.setHeaderText("Nem sikerült lekérni az adatbázisból.");
                    alert.setContentText(ex.getMessage());
                    Platform.exit();
                }
            });
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba!");
            alert.setHeaderText("Nem sikerült megnyitni az ablakot.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void deleteButton(ActionEvent actionEvent)  {
        Etel selected = etlapTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Nincs kiválasztott étel.");
            alert.showAndWait();
            return;
        }
        try {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Törlés");
            confirmation.setHeaderText("Biztosan törölni szeretné a kiválasztott ételt?");
            confirmation.setContentText(selected.getNev());
            if (confirmation.showAndWait().get() != ButtonType.OK) return;
            if (!service.delete(selected.getId())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hiba!");
                alert.setHeaderText("Sikertelen törlés.");
            } else {
                ListEtel();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba!");
            alert.setHeaderText("Sikertelen törlés.");
            alert.setContentText(e.getMessage());
        }

    }

    public void ftInc(ActionEvent actionEvent) {
        Etel selected = etlapTable.getSelectionModel().getSelectedItem();
        try {
            if (selected == null) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Áremelés");
                confirmation.setHeaderText("Biztosan áremelést szeretne végrehajtani az összes ételen?");
                confirmation.setContentText("Az áremelés mértéke: " + ftIncField.getValue() + " Ft");
                if (confirmation.showAndWait().get() != ButtonType.OK) return;
                List<Etel> list = etlapTable.getItems();
                for (Etel etel : list) {
                    etel.setAr(etel.getAr() + ftIncField.getValue());
                }
                service.changeList(list);
            } else {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Áremelés");
                confirmation.setHeaderText("Biztosan áremelést szeretne végrehajtani az ételen? (" + selected.getNev() + ")");
                confirmation.setContentText("Az áremelés mértéke: " + ftIncField.getValue() + " Ft");
                if (confirmation.showAndWait().get() != ButtonType.OK) return;
                selected.setAr(selected.getAr() + ftIncField.getValue());
                service.change(selected);
            }
            ListEtel();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba!");
            alert.setHeaderText("Sikertelen áremelés.");
            alert.setContentText(e.getMessage());
        }
    }

    public void perInc(ActionEvent actionEvent) {
        Etel selected = etlapTable.getSelectionModel().getSelectedItem();
        try {
            if (selected == null) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Áremelés");
                confirmation.setHeaderText("Biztosan áremelést szeretne végrehajtani az összes ételen?");
                confirmation.setContentText("Az áremelés mértéke: " + perIncField.getValue() + "%");
                if (confirmation.showAndWait().get() != ButtonType.OK) return;
                List<Etel> list = etlapTable.getItems();
                for (Etel etel : list) {
                    etel.setAr((int)(etel.getAr() * (1 + ((float)perIncField.getValue() / 100))));
                }
                service.changeList(list);
            } else {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Áremelés");
                confirmation.setHeaderText("Biztosan áremelést szeretne végrehajtani az ételen? (" + selected.getNev() + ")");
                confirmation.setContentText("Az áremelés mértéke: " + perIncField.getValue() + "%");
                if (confirmation.showAndWait().get() != ButtonType.OK) return;
                selected.setAr((int)(selected.getAr() * (1 + ((float)perIncField.getValue() / 100))));
                service.change(selected);
            }
            ListEtel();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba!");
            alert.setHeaderText("Sikertelen áremelés.");
            alert.setContentText(e.getMessage());
        }
    }
}