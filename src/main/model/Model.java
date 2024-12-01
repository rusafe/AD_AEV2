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

/**
 * Clase que contiene toda la logica de los datos de la aplicacion
 */
public class Model {
	/**
	 * Constante que hace referencia a que el usuario es de tipo desconocido
	 */
	public static final int UNKNOWN = -1;
	/**
	 * Constante que hace referencia a que el usuario es de tipo administrador
	 */
	public static final int ADMIN = 0;
	/**
	 * Constante que hace referencia a que el usuario es de tipo cliente
	 */
	public static final int CLIENT = 1;
	/**
	 * Array que contiene los tipos de usuarios como strings
	 */
	private static final String[] USER_TYPES = new String[] {"admin", "client"};
	/**
	 * Ruta al directorio donde se guardan los ficheros CSV
	 */
	private static final String CSV_DIRECTORY = "resources/csv";
	/**
	 * Ruta al directorio donde se guardan los ficheros XML
	 */
	private static final String XML_DIRECTORY = "resources/xml";
	
	/**
	 * Guarda el tipo de usuario que se conecta
	 */
	private int userType = UNKNOWN;
	/**
	 * Guarda la ultima consulta realizada por el usuario
	 */
	private String lastQuery = null;
	
	/**
	 * Getter que devuelve el tipo de usuario que ha iniciado sesion
	 * @return El tipo de usuario
	 */
	public int getUserType() {
		return userType;
	}
	
	/**
	 * Getter que devuelve la ultima consulta realizada por el usuario
	 * @return La ultima consulta realizada
	 */
	public String getLastQuery() {
		return lastQuery;
	}
	
	/**
	 * Setter que guarda la ultima consulta realizada por el usuario
	 * @param query Ultima consulta realizada
	 */
	public void setLastQuery(String query) {
		lastQuery = query;
	}
	
	/**
	 * Metodo que inicia la conexion con la base de datos
	 * @param username Usuario de la base de datos
	 * @param password Contraseña del usario
	 * @throws SQLException Si no existe el usuario con contraseña especificado
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
	 * Metodo que cierra la conexion con la base de datos y cierra la sesion del usuario
	 */
	public void logout() {
		ConnectionDb.closeConnection();
		userType = UNKNOWN;
		lastQuery = null;
	}
	
	/**
	 * Metodo que obtiene informacion de una consulta SQL y la transforma en un formato valido para un JTable
	 * @param query Consulta SQL
	 * @return Estructura de datos compatible con JTable con la informacion de la consulta
	 * @throws SQLException Si ocurre algun problema con la consulta SQL
	 */
	public DefaultTableModel transformQueryToTableModel(String query) throws SQLException {
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
		} catch (SQLException e) {
			throw new SQLException("La consulta introducida no es correcta");
		}
	}
	
	/**
	 * Metodo que exporta la informacion de la ultima consulta realizada a un fichero CSV
	 * @throws Exception Si ocurre algun error en el proceso de exportado
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
	 * Metodo que crea un nuevo usuario en la base de datos y lo inserta en la tabla users
	 * @param username Nombre del usuario
	 * @param password Contraseña del usuario
	 * @param type Tipo de usuario
	 * @throws SQLException Si ocurre algun error en el proceso de creacion o insercion
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
			PreparedStatement deleteUser = ConnectionDb.getConnection().prepareStatement("DROP USER ?;");
			deleteUser.setString(1, username);
			deleteUser.executeUpdate();
			deleteUser.close();
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
	 * Metodo que importa un crea una tabla en la BBDD en base a un fichero CSV, inserta su contenido en dicha tabla y crea un fichero XML por cada fila de informacion
	 * @param csvRoute Ruta al fichero CSV
	 * @return Informacion de cada una de las filas del fichero CSV concatenadas
	 * @throws FileNotFoundException Si no existe el fichero en la ruta indicada
	 * @throws Exception Si ocurre algun error durante el proceso de importacion
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
	 * Metodo que crea una tabla en la base de datos
	 * @param name Nombre de la tabla
	 * @param columns Columnas de la tabla
	 * @throws Exception Si ocurre algun error en el proceso de creacion
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
	 * Metodo que lee un fichero CSV y crea un fichero XML por cada una de sus filas
	 * @param csv Objeto File con el fichero CSV
	 * @param destinationDirectory Directorio donde se van a guardar los ficheros XML
	 * @return Informacion de cada una de las filas del fichero CSV concatenadas
	 * @throws Exception Si ocurre algun error durante el proceso de creacion de los ficheos XML
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
	 * Metodo que lee los ficheros XML de un directorio e inserta sus datos en una tabla de la base de datos
	 * @param dbTable Nombre de la tabla de la base de datos
	 * @param xmlDirectory Directorio donde estan almacenados los ficheros XML
	 * @throws FileNotFoundException Si el directorio introducido no existe
	 * @throws Exception Si ocurre algun error en el proceso de lectura o insercion
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
	 * Metodo que obtiene el encabezado de un fichero CSV
	 * @param csv Objeto File con el fichero CSV
	 * @return El encabezado del fichero CSV
	 * @throws Exception Si ocurre algun error con la lectura del fichero
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
