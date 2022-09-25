package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Departamentos;
import model.entities.Funcionarios;
import model.exception.ValidationException;
import model.services.DepartamentosService;
import model.services.FuncionariosService;

public class FuncionariosFormController implements Initializable {

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpBirthDate;
	@FXML
	private TextField txtBaseSalary;
	@FXML
	private ComboBox<Departamentos> comboBoxDepartamento;
	@FXML
	private Label labelErrorName;
	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;

	private ObservableList<Departamentos> obsList;
	
	
	private Funcionarios entity;
	private FuncionariosService service;
	private DepartamentosService departamentoService;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	public void setFuncionarios(Funcionarios entity) {
		this.entity = entity;
	}

	public void setServices(FuncionariosService service, DepartamentosService departamentoService) {
		this.service = service;
		this.departamentoService = departamentoService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");

		}
		if (service == null) {
			throw new IllegalStateException("Service was null");

		}

		try {

			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErros());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}

	}

	private void notifyDataChangeListeners() {

		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}

	}

	private Funcionarios getFormData() {

		Funcionarios obj = new Funcionarios();
		ValidationException exception = new ValidationException("Validation Error");

		obj.setId(Utils.tryParseToInt(txtId.getText()));
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "O campo não pode ser vazio");
		}
		obj.setName(txtName.getText());
		if (exception.getErros().size() > 0) {
			throw exception;
		}
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	private void initializeNodes() {

		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		Constraints.setTextFieldMaxLength(txtEmail, 50);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		Constraints.setTextFieldDouble(txtBaseSalary);
		initializeComboBoxDepartamentos();
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();

	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if(entity.getBirthDate() != null) {
		dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		if(entity.getDepartamento() == null) {
			comboBoxDepartamento.getSelectionModel().selectFirst();
		}else {
		comboBoxDepartamento.setValue(entity.getDepartamento());
		}
		
	}

	public void loadAssocietedObjects() {
		
		if(departamentoService == null) {
			throw new IllegalStateException("DepartametosService esta nulo");
		}
		List<Departamentos> list = departamentoService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartamento.setItems(obsList);
		
	}
	
	
	
	
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}

	}
	
	
	private void initializeComboBoxDepartamentos() {
		 Callback<ListView<Departamentos>, ListCell<Departamentos>> factory = lv -> new ListCell<Departamentos>() {
		 @Override
		 protected void updateItem(Departamentos item, boolean empty) {
		 super.updateItem(item, empty);
		 setText(empty ? "" : item.getName());
		 }
		 };
		comboBoxDepartamento.setCellFactory(factory);
		comboBoxDepartamento.setButtonCell(factory.call(null));
		} 

	

}
