package main;

import main.controlador.Controlador;
import main.model.Model;
import main.vista.VistaAdmin;
import main.vista.VistaClient;
import main.vista.VistaLogin;

public class Principal {

	public static void main(String[] args) {
		Model model = new Model();
		VistaLogin vistaLogin = new VistaLogin();
		VistaClient vistaClient = new VistaClient();
		VistaAdmin vistaAdmin = new VistaAdmin();
		Controlador controlador = new Controlador(model, vistaLogin, vistaClient, vistaAdmin);
	}

}
