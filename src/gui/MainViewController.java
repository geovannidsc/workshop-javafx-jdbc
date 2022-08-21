package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

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
		
		System.out.println("onMenuItemDepartamentosAction");
		
	}
	
	
	@FXML
	public void onMenuItemSobreAction() {
		
		System.out.println("onMenuItemSobreAction");
		
	}
	
	
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		

	}

}
