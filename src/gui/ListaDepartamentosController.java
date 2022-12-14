package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Departamentos;
import model.services.DepartamentosService;

public class ListaDepartamentosController implements Initializable, DataChangeListener {

	private DepartamentosService service;

	@FXML
	private TableView<Departamentos> tableViewDepartamentos;
	@FXML
	private TableColumn<Departamentos, Integer> tableColumnId;
	@FXML
	private TableColumn<Departamentos, String> tableColumnName;
	@FXML
	private TableColumn<Departamentos, Departamentos> tableColumnEDIT;
	@FXML
	private TableColumn<Departamentos, Departamentos> tableColumnREMOVE;
	@FXML
	private Button btNovo;

	private ObservableList<Departamentos> obsList;

	public void setDepartamentosService(DepartamentosService service) {

		this.service = service;

	}

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Departamentos obj = new Departamentos();
		createDialogForm(obj, "/gui/FormDepartamentos.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		initializeNodes();

	}

	// Iniciar Table View
	private void initializeNodes() {

		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// metodo para fazer Table View acompanhar tamanho da janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartamentos.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {

		if (service == null) {
			throw new IllegalStateException("Servi?o n?o Encontrado");
		}
		List<Departamentos> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartamentos.setItems(obsList);
		initEditButtons();
		initRemoveButtons();

	}

	private void createDialogForm(Departamentos obj, String absoluteName, Stage parentStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			DepartamentosFormController controller = loader.getController();
			controller.setDepartamentos(obj);
			controller.setDepartamentosService(new DepartamentosService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entrar com Departamento");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {

			Alerts.showAlert("IO Exception", "Erro loading view", e.getMessage(), AlertType.ERROR);

		}

	}

	@Override
	public void onDataChanged() {

		updateTableView();

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Departamentos, Departamentos>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Departamentos obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/FormDepartamentos.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Departamentos, Departamentos>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Departamentos obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Departamentos obj) {

		Optional<ButtonType> result = Alerts.showConfirmation("Confirma??o", "Confirma a exclus?o?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}

			try {
				service.remove(obj);
				updateTableView();

			} catch (DbIntegrityException e) {

				Alerts.showAlert("Erro ao remover o objeto", null, e.getMessage(), AlertType.ERROR);
			}

		}

	}
	
	
	

}
