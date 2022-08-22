package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Departamentos;

public class ListaDepartamentosController implements Initializable{

	@FXML
	private TableView<Departamentos> tableViewDepartamentos;
	@FXML
	private TableColumn<Departamentos, Integer> tableColumnId;
	@FXML
	private TableColumn<Departamentos, String> tableColumnName;
	@FXML
	private Button btNovo;
	
	@FXML
	public void onBtNewAction() {
		System.out.println("OKBT");
	}
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		initializeNodes();
		
	}


		// Iniciar Table View
	private void initializeNodes() {
		
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("nome"));
		
		//metodo para fazer Table View acompanhar tamanho da janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartamentos.prefHeightProperty().bind(stage.heightProperty());
	}

	
	
	
}
