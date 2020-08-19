import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static final String basicPath = "src\\main\\java\\";
    static final String pathToCSV = basicPath + "data.csv";
    static final String pathToJSON = basicPath + "data.json";
    static final String pathToXML = basicPath + "data.xml";

    public static void main(String[] args)  {

        //Задание 1
        System.out.println("== Преобразуем CSV в JSON ==");
        convertEmployeesToCSV(pathToCSV, createOriginalList());
        convertEmployeesToJSON(pathToJSON, convertCSVToEmployees(pathToCSV));
        printEmployeesList(convertJSONToEmployees(pathToJSON));
        System.out.println();

        //Задание 2
        System.out.println("== Преобразуем XML в JSON ==");
        convertEmployeesToXML(pathToXML, createOriginalList());
        convertEmployeesToJSON(pathToJSON, convertXMLToEmployees(pathToXML));
        printEmployeesList(convertJSONToEmployees(pathToJSON));
        System.out.println();

        //Задание 3
        System.out.println("== Преобразуем объекты в JSON и обратно ==");
        convertEmployeesToJSON(pathToJSON, createOriginalList());
        printEmployeesList(convertJSONToEmployees(pathToJSON));
        System.out.println();

    }

    public static List<Employee> createOriginalList() {
        Employee one = new Employee(1,"John","Smith","USA",25);
        Employee two = new Employee(2,"Inav","Petrov","RU",23);
        List<Employee> originalList = new ArrayList<>();
        originalList.add(one);
        originalList.add(two);
        return originalList;
    }

    public static void convertEmployeesToCSV(String filePath, List<Employee> employeeList) {
        try (Writer writer = new FileWriter(filePath)) {
            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
            beanToCsv.write(employeeList);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> convertCSVToEmployees(String filePath) {
        List<Employee> employeeList = new ArrayList<>();
        try (FileReader fr = new FileReader(filePath)) {
            employeeList = new CsvToBeanBuilder(fr)
                    .withType(Employee.class).build().parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    public static void convertEmployeesToJSON(String filePath, List<Employee> employeeList) {
        try (Writer writer = new FileWriter(filePath)) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            String json = gson.toJson(employeeList);
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> convertJSONToEmployees(String filePath) {
        List<Employee> employeeList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String json = reader.readLine();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            Type collectionType = new TypeToken<List<Employee>>(){}.getType();
            employeeList = gson.fromJson(json, collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    public static void convertEmployeesToXML(String filePath, List<Employee> employeeList) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element staff = document.createElement("staff");
            document.appendChild(staff);

            for (Employee e : employeeList) {
                Element employee = document.createElement("employee");
                staff.appendChild(employee);

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode(String.valueOf(e.getId())));
                employee.appendChild(id);

                Element firstName = document.createElement("firstName");
                firstName.appendChild(document.createTextNode(e.getFirstName()));
                employee.appendChild(firstName);

                Element lastName = document.createElement("lastName");
                lastName.appendChild(document.createTextNode(e.getLastName()));
                employee.appendChild(lastName);

                Element country = document.createElement("country");
                country.appendChild(document.createTextNode(e.getCountry()));
                employee.appendChild(country);

                Element age = document.createElement("age");
                age.appendChild(document.createTextNode(String.valueOf(e.getAge())));
                employee.appendChild(age);
            }

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(filePath));
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(domSource, streamResult);
        } catch (TransformerException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> convertXMLToEmployees(String filePath) {
        List<Employee> employeeList = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));

            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;
                    Employee employee = new Employee(
                            Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                            element.getElementsByTagName("firstName").item(0).getTextContent(),
                            element.getElementsByTagName("lastName").item(0).getTextContent(),
                            element.getElementsByTagName("country").item(0).getTextContent(),
                            Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())
                    );
                    employeeList.add(employee);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return employeeList;
    }

    public static void printEmployeesList(List<Employee> employeeList) {
        for (Employee e : employeeList) {
            System.out.println(e);
        }
    }
}