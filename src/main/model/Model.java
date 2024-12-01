package main.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.Utils;

public class Model {
	/**
	 * 
	 */
	public static final int UNKNOWN = -1;
	/**
	 * 
	 */
	public static final int ADMIN = 0;
	/**
	 * 
	 */
	public static final int CLIENT = 1;
	/**
	 * 
	 */
	private static final String[] USER_TYPES = new String[] {"admin", "client"};
	/**
	 * 
	 */
	private static final String CSV_DIRECTORY = "resources/csv";
	/**
	 * 
	 */
	private static final String XML_DIRECTORY = "resources/xml";
	
	/**
	 * 
	 */
	private int userType = UNKNOWN;
	/**
	 * 
	 */
	private String lastQuery = null;
	
	/**
	 * 
	 * @return
	 */
	public int getUserType() {
		return userType;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getLastQuery() {
		return lastQuery;
	}
	
	/**
	 * 
	 * @param query
	 */
	public void setLastQuery(String query) {
		lastQuery = query;
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @throws SQLException
	 */
	public void login(String username, String password) throws SQLException {
		String hashedPassword = Utils.passwordHash(password);
		
		if(!ConnectionDb.instantiateConnection(username, hashedPassword))
			throw new SQLException("Usuario o contraseña incorrectos");
		
		try {
			Statement stmt = ConnectionDb.getConnection().createStatement();
			stmt.executeQuery("SELECT id FROM users;");
			userType = ADMIN;
		} catch (SQLException e) {
			userType = CLIENT;
		}
	}
	
	/**
	 * 
	 */
	public void logout() {
		ConnectionDb.closeConnection();
		userType = UNKNOWN;
	}
	
	/**
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public DefaultTableModel transformQueryToTableModel(String query) throws Exception {
		try {
			Statement stmt = ConnectionDb.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			DefaultTableModel tableModel = new DefaultTableModel();
			
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {
				tableModel.addColumn(rsmd.getColumnName(i));
			}
			while(rs.next()) {
				Object[] row = new Object[rsmd.getColumnCount()];
				for(int i = 0; i < rsmd.getColumnCount(); i++) {
					row[i] = rs.getObject(i + 1);
				}
				tableModel.addRow(row);
			}
			rs.close();
			stmt.close();
			
			return tableModel;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void exportCsvFromLastQuery() throws Exception {
		File csvDirectory = new File(CSV_DIRECTORY);
		if(!csvDirectory.exists())
			csvDirectory.mkdirs();
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("%s/exported.csv", CSV_DIRECTORY)))){
			Statement stmt = ConnectionDb.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(lastQuery);
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int i = 1; i <= rsmd.getColumnCount(); i++) {
				bw.write(rsmd.getColumnName(i));
				if(i != rsmd.getColumnCount())
					bw.write(";");
			}
			while(rs.next()) {
				bw.newLine();
				for(int i = 1; i <= rsmd.getColumnCount(); i++) {
					bw.write(rs.getString(i));
					if(i != rsmd.getColumnCount())
						bw.write(";");
				}
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @param type
	 * @throws SQLException
	 */
	public void createNewUser(String username, String password, int type) throws SQLException {
		String hashedPassword = Utils.passwordHash(password);
		
		try {
			PreparedStatement createDbUser = ConnectionDb.getConnection().prepareStatement("CREATE USER ? IDENTIFIED BY ?;");
			createDbUser.setString(1, username);
			createDbUser.setString(2, hashedPassword);
			createDbUser.executeUpdate();
			createDbUser.close();
		} catch (SQLException e) {
			throw new SQLException("El usuario introducido ya existe");
		}
		
		try {
			PreparedStatement grantUserPermissions = ConnectionDb.getConnection().prepareStatement("GRANT SELECT on population.population TO ?;");
			grantUserPermissions.setString(1, username);
			grantUserPermissions.executeUpdate();
			grantUserPermissions.close();
		} catch (SQLException e) {
			throw new SQLException("No se ha creado todavia la tabla population");
		}
		
		try {
			PreparedStatement insertUserStmt = ConnectionDb.getConnection().prepareStatement("INSERT INTO users (login, password, type) VALUES (?, ?, ?);");
			insertUserStmt.setString(1, username);
			insertUserStmt.setString(2, hashedPassword);
			insertUserStmt.setString(3, USER_TYPES[type]);
			insertUserStmt.executeUpdate();
			insertUserStmt.close();
		} catch (SQLException e) {
			throw new SQLException("Ha ocurrido un error al intentar crear el usuario");
		}
	}
	
	/**
	 * 
	 * @param csvRoute
	 * @return
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public String importCsv(String csvRoute) throws FileNotFoundException, Exception {
		File csvFile = new File(csvRoute);
		
		if(!csvFile.exists())
			throw new FileNotFoundException("El archivo introducido no existe");
		
		String csvData = null;
		
		try {
			createDbTable("population", getHeaderFromCsv(csvFile));
			csvData =  createCountriesXmlFromCsv(csvFile, XML_DIRECTORY);
			insertFromXmlToDb("population", XML_DIRECTORY);
		} catch (Exception e) {
			throw e;
		}
		
		return csvData;
	}
	
	/**
	 * 
	 * @param name
	 * @param columns
	 * @throws Exception
	 */
	private void createDbTable(String name, String[] columns) throws Exception {
		try {
			Statement deleteStmt = ConnectionDb.getConnection().createStatement();
			deleteStmt.execute(String.format("DROP TABLE IF EXISTS %s;", name));
			deleteStmt.close();
			
			String creationQueryString = String.format("CREATE TABLE %s(id INT PRIMARY KEY AUTO_INCREMENT,", name);
			for(String column : columns) {
				creationQueryString += String.format("%s VARCHAR(30),", column);
			}
			creationQueryString = creationQueryString.substring(0, creationQueryString.length() - 1);
			creationQueryString += ");";
			
			Statement createStmt = ConnectionDb.getConnection().createStatement();
			createStmt.execute(creationQueryString);
			createStmt.close();	
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @param csv
	 * @param destinationDirectory
	 * @return
	 * @throws Exception
	 */
	private String createCountriesXmlFromCsv(File csv, String destinationDirectory) throws Exception {
		File xmlDirectory = new File(destinationDirectory);
		if(!xmlDirectory.exists())
			xmlDirectory.mkdirs();
		
		try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			
			TransformerFactory tranFactory = TransformerFactory.newInstance();
			Transformer transformer = tranFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			String countriesDataString = "";
			String[] header = br.readLine().split(";");
			String line = br.readLine();
			while(line != null) {
				String[] data = line.split(";");
				
				Document document = dBuilder.newDocument();
				Element root = document.createElement("country");
				document.appendChild(root);
				for(int i = 0; i < header.length; i++) {
					Element element = document.createElement(header[i]);
					element.appendChild(document.createTextNode(String.valueOf(data[i])));
					root.appendChild(element);
					countriesDataString += String.format("%s: %s, ", header[i], data[i]);
				}
				countriesDataString = countriesDataString.substring(0, countriesDataString.length() - 2);
				countriesDataString += System.lineSeparator();
				
				DOMSource source = new DOMSource(document);
				FileWriter fw = new FileWriter(String.format("%s/%s.xml", destinationDirectory, data[0]));
				StreamResult result = new StreamResult(fw);
				transformer.transform(source, result);
				fw.close();
				
				line = br.readLine();
			}
			countriesDataString = countriesDataString.substring(0, countriesDataString.length() - 1);
			return countriesDataString;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @param dbTable
	 * @param xmlDirectory
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	private void insertFromXmlToDb(String dbTable, String xmlDirectory) throws FileNotFoundException, Exception {
		File directory = new File(xmlDirectory);
		if(!directory.exists())
			throw new FileNotFoundException("No existe el directorio de destino indicado");
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			
			File[] files = directory.listFiles();
			for(File file : files) {
				Document document = dBuilder.parse(file);
				Node country = document.getFirstChild();
				NodeList countryData = country.getChildNodes();
				String insertQueryString = String.format("INSERT INTO %s VALUES (null,", dbTable);
				for(int i = 0; i < countryData.getLength(); i++) {
					Node node = countryData.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE)
						insertQueryString += "?,";
				}
				insertQueryString = insertQueryString.substring(0, insertQueryString.length() - 1);
				insertQueryString += ");";
				PreparedStatement insertStmt = ConnectionDb.getConnection().prepareStatement(insertQueryString);
				int currentPlaceholder = 1;
				for(int i = 0; i < countryData.getLength(); i++) {
					Node node = countryData.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						insertStmt.setString(currentPlaceholder, countryData.item(i).getTextContent());
						currentPlaceholder++;
					}
				}
				insertStmt.executeUpdate();
				insertStmt.close();
			}	
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 * @param csv
	 * @return
	 * @throws Exception
	 */
	private String[] getHeaderFromCsv(File csv) throws Exception {
		try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
			String[] header = br.readLine().split(";");
			return header;
		} catch (Exception e) {
			throw e;
		}
	}
}
