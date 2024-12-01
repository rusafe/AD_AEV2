package main.controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import main.model.Model;
import main.vista.VistaAdmin;
import main.vista.VistaLogin;

public class Controlador {
	Model model;
	VistaLogin vistaLogin;
	VistaAdmin vistaAdmin;
	
	public Controlador(Model model, VistaLogin vistaLogin, VistaAdmin vistaAdmin) {
		this.model = model;
		this.vistaLogin = vistaLogin;
		this.vistaAdmin = vistaAdmin;
		initEventHandlers();
	}
	
	private void initEventHandlers() {
		vistaLogin.getBtnLogin().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String username = vistaLogin.getTxtUsername().getText();
					String password = new String(vistaLogin.getPwdfPassword().getPassword());
					
					if(username.equals("")) {
						JOptionPane.showMessageDialog(null, "Debes introducir un nombre de usuario", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(password.equals("")) {
						JOptionPane.showMessageDialog(null, "Debes introducir una contraseña", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					model.login(username, password);
					
					vistaLogin.setVisible(false);
					vistaAdmin.setVisible(true);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		vistaAdmin.getBtnCreateUser().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String username = vistaAdmin.getTxtUsername().getText();
					String password = new String(vistaAdmin.getPwdfPassword().getPassword());
					
					if(username.equals("")) {
						JOptionPane.showMessageDialog(null, "Debes introducir un nombre de usuario", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(password.equals("")) {
						JOptionPane.showMessageDialog(null, "Debes introducir una contraseña", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					model.createNewUser(username, password, Model.CLIENT);
					JOptionPane.showMessageDialog(null, "Usuario creado con exito", "Creacion usuario", JOptionPane.INFORMATION_MESSAGE);
					vistaAdmin.getTxtUsername().setText("");
					vistaAdmin.getPwdfPassword().setText("");
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		vistaAdmin.getBtnImportCsv().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String csvRoute = vistaAdmin.getTxtCsvRoute().getText();
					
					if(csvRoute.equals("")) {
						JOptionPane.showMessageDialog(null, "Debes introducir una ruta a un archivo csv", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					vistaAdmin.getTxaDataCsv().setText(model.importCsv(csvRoute));
					JOptionPane.showMessageDialog(null, "Fichero CSV importado con exito", "Importacion CSV", JOptionPane.INFORMATION_MESSAGE);
					vistaAdmin.getTxtCsvRoute().setText("");
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
}
