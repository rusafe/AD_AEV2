package main.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
	public static final int ADMIN = 0;
	public static final int CLIENT = 1;
	private static final String XML_DIRECTORY = "resources/xml";
	
	public void createNewUser(String username, String password, int type) throws SQLException {
		final String[] userType = new String[] {"admin", "client"};
		
		if(!ConnectionDb.instantiateConnection("root", ""))
			throw new SQLException("Ha habido un problema conectando con la base de datos");
		
		String hashedPassword = Utils.passwordHash(password);
		
		try {
//			Statement createDbUser = ConnectionDb.getConnection().createStatement();
//			createDbUser.execute(String.format("CREATE USER %s IDENTIFIED BY %s;", username, hashedPassword));
			
			PreparedStatement createDbUser = ConnectionDb.getConnection().prepareStatement("CREATE USER ? IDENTIFIED BY ?");
			createDbUser.setString(1, username);
			createDbUser.setString(2, hashedPassword);
			createDbUser.execute();
			createDbUser.close();
			
//			Statement grantUserPermissions = ConnectionDb.getConnection().createStatement();
//			grantUserPermissions.execute(String.format("GRANT SELECT on population.population TO %s;", username));
			
			PreparedStatement grantUserPermissions = ConnectionDb.getConnection().prepareStatement("GRANT SELECT on population.population TO ?");
			grantUserPermissions.setString(1, username);
			grantUserPermissions.execute();
			grantUserPermissions.close();
		} catch (SQLException e) {
//			throw new SQLException("No se ha creado todavia la tabla population");
			throw e;
		}
		
		try {
			PreparedStatement insertUserStmt = ConnectionDb.getConnection().prepareStatement("INSERT INTO users (login, password, type) VALUES (?, ?, ?);");
			insertUserStmt.setString(1, username);
			insertUserStmt.setString(2, hashedPassword);
			insertUserStmt.setString(3, userType[type]);
			insertUserStmt.executeUpdate();
			insertUserStmt.close();
		} catch (SQLException e) {
			throw e;
		}
	}
	
	public String importCsv(String csvRoute) throws FileNotFoundException, SQLException {
		File csvFile = new File(csvRoute);
		
		if(!csvFile.exists())
			throw new FileNotFoundException("El archivo introducido no existe");
		
		String csvData = null;
		
		try {
			createDbTable("population", getHeaderFromCsv(csvFile));
			csvData =  createCountriesXmlFromCsv(csvFile, XML_DIRECTORY);
			insertFromXmlToDb("population", XML_DIRECTORY);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return csvData;
	}
	
	private void createDbTable(String name, String[] columns) throws SQLException {
		if(!ConnectionDb.instantiateConnection("root", ""))
			throw new SQLException("Ha habido un problema conectando con la base de datos");
		
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
	}
	
	private String createCountriesXmlFromCsv(File csv, String destinationDirectory) {
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
			e.printStackTrace();
			return null;
		}
	}
	
	private void insertFromXmlToDb(String dbTable, String xmlDirectory) throws FileNotFoundException {
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
			e.printStackTrace();
		}
	}
	
	private String[] getHeaderFromCsv(File csv) {
		try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
			String[] header = br.readLine().split(";");
			return header;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
