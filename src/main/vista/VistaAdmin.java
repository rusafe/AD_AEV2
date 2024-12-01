package main.vista;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JPasswordField;

public class VistaAdmin extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnImportCsv;
	private JTextArea txaDataCsv;
	private JScrollPane scrollPane;
	private JTextField txtCsvRoute;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JTextField txtUsername;
	private JPasswordField pwdfPassword;
	private JButton btnCreateUser;
	private JPasswordField pwdfRepeatPassword;
	private JButton btnGoBack;
	
	public JButton getBtnImportCsv() {
		return btnImportCsv;
	}
	
	public JTextArea getTxaDataCsv() {
		return txaDataCsv;
	}
	
	public JTextField getTxtCsvRoute() {
		return txtCsvRoute;
	}
	
	public JTextField getTxtUsername() {
		return txtUsername;
	}
	
	public JPasswordField getPwdfPassword() {
		return pwdfPassword;
	}
	
	public JPasswordField getPwdfRepeatPassword() {
		return pwdfRepeatPassword;
	}
	
	public JButton getBtnCreateUser() {
		return btnCreateUser;
	}
	
	public JButton getBtnGoBack() {
		return btnGoBack;
	}
	
	public VistaAdmin() {
		initComponents();
		setVisible(false);
	}
	
	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 830, 745);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnImportCsv = new JButton("Importar CSV");
		btnImportCsv.setBounds(614, 182, 154, 25);
		contentPane.add(btnImportCsv);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(36, 219, 732, 396);
		contentPane.add(scrollPane);
		
		txaDataCsv = new JTextArea();
		scrollPane.setViewportView(txaDataCsv);
		txaDataCsv.setEditable(false);
		
		txtCsvRoute = new JTextField();
		txtCsvRoute.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtCsvRoute.setBounds(36, 183, 564, 25);
		contentPane.add(txtCsvRoute);
		txtCsvRoute.setColumns(10);
		
		JLabel lblCsvRoute = new JLabel("Ruta al archivo CSV");
		lblCsvRoute.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCsvRoute.setBounds(36, 162, 133, 14);
		contentPane.add(lblCsvRoute);
		
		lblUsername = new JLabel("Nombre");
		lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblUsername.setBounds(36, 56, 47, 14);
		contentPane.add(lblUsername);
		
		lblPassword = new JLabel("Contraseña");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblPassword.setBounds(220, 56, 69, 14);
		contentPane.add(lblPassword);
		
		txtUsername = new JTextField();
		txtUsername.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtUsername.setBounds(36, 78, 154, 20);
		contentPane.add(txtUsername);
		txtUsername.setColumns(10);
		
		pwdfPassword = new JPasswordField();
		pwdfPassword.setFont(new Font("Tahoma", Font.PLAIN, 12));
		pwdfPassword.setBounds(220, 78, 154, 20);
		contentPane.add(pwdfPassword);
		
		btnCreateUser = new JButton("Crear Usuario");
		btnCreateUser.setBounds(592, 75, 122, 23);
		contentPane.add(btnCreateUser);
		
		JLabel lblCreateUser = new JLabel("Creación de nuevo usuario");
		lblCreateUser.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblCreateUser.setBounds(36, 31, 214, 14);
		contentPane.add(lblCreateUser);
		
		JLabel lblImportCsv = new JLabel("Importación de archivo CSV");
		lblImportCsv.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblImportCsv.setBounds(36, 131, 206, 20);
		contentPane.add(lblImportCsv);
		
		pwdfRepeatPassword = new JPasswordField();
		pwdfRepeatPassword.setFont(new Font("Tahoma", Font.PLAIN, 12));
		pwdfRepeatPassword.setBounds(407, 77, 154, 20);
		contentPane.add(pwdfRepeatPassword);
		
		JLabel lblRepeatPassword = new JLabel("Repetir Contraseña");
		lblRepeatPassword.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblRepeatPassword.setBounds(407, 56, 154, 14);
		contentPane.add(lblRepeatPassword);
		
		btnGoBack = new JButton("Volver");
		btnGoBack.setBounds(36, 631, 116, 44);
		contentPane.add(btnGoBack);
	}
}
