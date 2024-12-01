package main.vista;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.JScrollPane;

public class VistaClient extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton btnLogout;
	private JButton btnExportCsv;
	private JButton btnGoToAdmin;
	private JButton btnQuery;
	private JTextField txtQuery;
	private JTable tblQuery;
	private JScrollPane scrollPane;

	public JButton getBtnLogout() {
		return btnLogout;
	}
	
	public JButton getBtnExportCsv() {
		return btnExportCsv;
	}
	
	public JButton getBtnGoToAdmin() {
		return btnGoToAdmin;
	}
	
	public JButton getBtnQuery() {
		return btnQuery;
	}
	
	public JTextField getTxtQuery() {
		return txtQuery;
	}
	
	public JTable getTblQuery() {
		return tblQuery;
	}
	
	public VistaClient() {
		initComponents();
		setVisible(false);
	}
	
	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 787, 491);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnLogout = new JButton("Logout");
		btnLogout.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnLogout.setBounds(30, 392, 89, 37);
		contentPane.add(btnLogout);
		
		btnExportCsv = new JButton("Exportar CSV");
		btnExportCsv.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnExportCsv.setBounds(609, 392, 131, 37);
		contentPane.add(btnExportCsv);
		
		btnGoToAdmin = new JButton("Panel Administrador");
		btnGoToAdmin.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnGoToAdmin.setBounds(439, 392, 153, 37);
		contentPane.add(btnGoToAdmin);
		
		btnQuery = new JButton("Buscar");
		btnQuery.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnQuery.setBounds(651, 65, 89, 31);
		contentPane.add(btnQuery);
		
		txtQuery = new JTextField();
		txtQuery.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtQuery.setBounds(30, 65, 611, 31);
		contentPane.add(txtQuery);
		txtQuery.setColumns(10);
		
		JLabel lblQuery = new JLabel("Consulta SELECT a realizar");
		lblQuery.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblQuery.setBounds(30, 40, 170, 14);
		contentPane.add(lblQuery);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(30, 120, 710, 251);
		contentPane.add(scrollPane);
		
		tblQuery = new JTable();
		scrollPane.setViewportView(tblQuery);
	}
}
