package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartamentosService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemFuncionarios;
	@FXML
	private MenuItem menuItemDepartamentos;
	@FXML
	private MenuItem menuItemSobre;

	@FXML
	public void onMenuItemFuncionariosAction() {

		System.out.println("onMenuItemFuncionariosAction");

	}

	@FXML
	public void onMenuItemDepartamentosAction() {

		loadView("/gui/ListaDepartamentos.fxml", (ListaDepartamentosController controller) ->{
			
			controller.setDepartamentosService(new DepartamentosService());
			controller.updateTableView();
			
		});
		
	}

	@FXML
	public void onMenuItemSobreAction() {

		loadView("/gui/Sobre.fxml",x -> {} );

	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	private synchronized <T> void loadView(String nomeAbsoluto, Consumer<T> acaoDeInicializacao) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			VBox newVBox = loader.load();

			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			T controller = loader.getController();
			acaoDeInicializacao.accept(controller);
			
		} catch (IOException e) {

			Alerts.showAlert("IO Exception", "Erro Carregando a pagina", e.getMessage(), AlertType.ERROR);
		}

	}

}
