package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import model.entities.Funcionarios;
import model.services.DepartamentosService;
import model.services.FuncionariosService;

public class ListaFuncionariosController implements Initializable, DataChangeListener {

	private FuncionariosService service;
		

	@FXML
	private TableView<Funcionarios> tableViewFuncionarios;
	@FXML
	private TableColumn<Funcionarios, Integer> tableColumnId;
	@FXML
	private TableColumn<Funcionarios, String> tableColumnName;
	@FXML
	private TableColumn<Funcionarios, String> tableColumnEmail;
	@FXML
	private TableColumn<Funcionarios, Date> tableColumnBirthDate;
	@FXML
	private TableColumn<Funcionarios, Double> tableColumnBaseSalary;
	@FXML
	private TableColumn<Funcionarios, Funcionarios> tableColumnEDIT;
	@FXML
	private TableColumn<Funcionarios, Funcionarios> tableColumnREMOVE;
	@FXML
	private TableColumn<Funcionarios, Integer> tableColumnDepartamento;
	@FXML
	private Button btNovo;

	
	private ObservableList<Funcionarios> obsList;

	public void setFuncionariosService(FuncionariosService service) {

		this.service = service;

	}

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Funcionarios obj = new Funcionarios();
		createDialogForm(obj, "/gui/FormFuncionarios.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		initializeNodes();

	}

	// Iniciar Table View
	private void initializeNodes() {

		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		
		//tableColumnDepartamento.setCellValueFactory(new PropertyValueFactory<>("nDep"));
		// metodo para fazer Table View acompanhar tamanho da janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewFuncionarios.prefHeightProperty().bind(stage.heightProperty());
		
		
	}

	public void updateTableView() {

		if (service == null) {
			throw new IllegalStateException("Serviço não Encontrado");
		}
		List<Funcionarios> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewFuncionarios.setItems(obsList);
		initEditButtons();
		initRemoveButtons();

	}

	private void createDialogForm(Funcionarios obj, String absoluteName, Stage parentStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			FuncionariosFormController controller = loader.getController();
			controller.setFuncionarios(obj);
			controller.setServices(new FuncionariosService(), new DepartamentosService());
			controller.loadAssocietedObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entrar com Funcionarios");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			
			
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Erro loading view", e.getMessage(), AlertType.ERROR);

		}

	}

	@Override
	public void onDataChanged() {

		updateTableView();

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Funcionarios, Funcionarios>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Funcionarios obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/FormFuncionarios.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Funcionarios, Funcionarios>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Funcionarios obj, boolean empty) {
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

	private void removeEntity(Funcionarios obj) {

		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Confirma a exclusão?");

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
