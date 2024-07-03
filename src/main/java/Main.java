import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import javax.xml.parsers.ParserConfigurationException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");
        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> list = csv.parse();
            return list;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String name) {
        try (FileWriter fileWriter = new FileWriter(name)) {
            fileWriter.write(json);
            System.out.println(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Employee> parseXML(String path) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(path));
        Node root = document.getDocumentElement();
        return read(root);
    }

    private static List<Employee> read(Node node) {
        NodeList nodeList = node.getChildNodes();
        List<Employee> a = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Employee employee = new Employee();
            Node node_ = nodeList.item(i);
            if (node_.getNodeType() == Node.ELEMENT_NODE) {
                NodeList childNode = node_.getChildNodes();
                for (int t = 0; t < childNode.getLength(); t++) {
                    Node element2 = childNode.item(t);
                    if (element2.getNodeName().equals("id")) {
                        employee.setId(Long.parseLong(element2.getTextContent()));
                    }
                    if (element2.getNodeName().equals("firstName")) {
                        employee.setFirstName(element2.getTextContent());
                    }
                    if (element2.getNodeName().equals("lastName")) {
                        employee.setLastName(element2.getTextContent());
                    }
                    if (element2.getNodeName().equals("country")) {
                        employee.setCountry(element2.getTextContent());
                    }
                    if (element2.getNodeName().equals("age")) {
                        employee.setAge(Integer.parseInt(element2.getTextContent()));
                    }
                }
                a.add(employee);
            }
        }
        return a;
    }
}
