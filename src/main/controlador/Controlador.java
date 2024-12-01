package main.controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import main.model.ConnectionDb;
import main.model.Model;
import main.vista.VistaAdmin;
import main.vista.VistaClient;
import main.vista.VistaLogin;

public class Controlador {
	Model model;
	VistaLogin vistaLogin;
	VistaClient vistaClient;
	VistaAdmin vistaAdmin;
	
	public Controlador(Model model, VistaLogin vistaLogin, VistaClient vistaClient, VistaAdmin vistaAdmin) {
		this.model = model;
		this.vistaLogin = vistaLogin;
		this.vistaClient = vistaClient;
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
					
					vistaLogin.getTxtUsername().setText("");
					vistaLogin.getPwdfPassword().setText("");
					
					vistaLogin.setVisible(false);
					vistaClient.setVisible(true);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		vistaClient.getBtnLogout().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.logout();
				
				vistaClient.getTxtQuery().setText("");
				vistaClient.getTblQuery().setModel(new DefaultTableModel());
				
				vistaClient.setVisible(false);
				vistaLogin.setVisible(true);
			}
		});
		vistaClient.getBtnExportCsv().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(model.getLastQuery() == null) {
						JOptionPane.showMessageDialog(null, "No se ha realizado ninguna consulta todavia", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					model.exportCsvFromLastQuery();
					JOptionPane.showMessageDialog(null, "Datos exportados con exito", "Exportacion CSV", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, exception.getMessage(), "Error en la exportacion", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		vistaClient.getBtnGoToAdmin().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(model.getUserType() != Model.ADMIN) {
					JOptionPane.showMessageDialog(null, "No tienes permisos de administrador", "Permisos insuficientes", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				model.setLastQuery(null);
				
				vistaClient.getTxtQuery().setText("");
				vistaClient.getTblQuery().setModel(new DefaultTableModel());
				
				vistaClient.setVisible(false);
				vistaAdmin.setVisible(true);
			}
		});
		vistaClient.getBtnQuery().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String query = vistaClient.getTxtQuery().getText();
					
					if(query.equals("")) {
						JOptionPane.showMessageDialog(null, "Debes introducir una consulta", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					vistaClient.getTblQuery().setModel(model.transformQueryToTableModel(query));
					model.setLastQuery(query);
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
					String repeatPassword = new String(vistaAdmin.getPwdfRepeatPassword().getPassword());
					
					if(username.equals("")) {
						JOptionPane.showMessageDialog(null, "Debes introducir un nombre de usuario", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(password.equals("")) {
						JOptionPane.showMessageDialog(null, "Debes introducir una contraseña", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(repeatPassword.equals("")) {
						JOptionPane.showMessageDialog(null, "Debes repetir la contraseña", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(!password.equals(repeatPassword)) {
						JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					model.createNewUser(username, password, Model.CLIENT);
					JOptionPane.showMessageDialog(null, "Usuario creado con exito", "Creacion usuario", JOptionPane.INFORMATION_MESSAGE);
					vistaAdmin.getTxtUsername().setText("");
					vistaAdmin.getPwdfPassword().setText("");
					vistaAdmin.getPwdfRepeatPassword().setText("");
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
		vistaAdmin.getBtnGoBack().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vistaAdmin.getTxtUsername().setText("");
				vistaAdmin.getPwdfPassword().setText("");
				vistaAdmin.getPwdfRepeatPassword().setText("");
				vistaAdmin.getTxtCsvRoute().setText("");
				vistaAdmin.getTxaDataCsv().setText("");
				
				vistaAdmin.setVisible(false);
				vistaClient.setVisible(true);
			}
		});
	}
}
