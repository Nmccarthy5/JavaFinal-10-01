import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class CustomerDataGUI {

    private TextField firstNameField, lastNameField, emailField, phoneField;
    private TextArea displayArea;
    private Connection connection;

    public void showCustomerData() {
        connectToDatabase();

        Stage primaryStage = new Stage();

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        // Create labels and fields
        gridPane.add(new Label("First Name:"), 0, 0);
        firstNameField = new TextField();
        gridPane.add(firstNameField, 1, 0);

        gridPane.add(new Label("Last Name:"), 0, 1);
        lastNameField = new TextField();
        gridPane.add(lastNameField, 1, 1);

        gridPane.add(new Label("Email:"), 0, 2);
        emailField = new TextField();
        gridPane.add(emailField, 1, 2);

        gridPane.add(new Label("Phone number:"), 0, 3);
        phoneField = new TextField();
        gridPane.add(phoneField, 1, 3);

        Button submitButton = new Button("Add Customer to database");
        submitButton.setOnAction(event -> submit());
        gridPane.add(submitButton, 0, 4);

        Button queryButton = new Button("Show Records");
        queryButton.setOnAction(event -> query());
        gridPane.add(queryButton, 1, 4);

        displayArea = new TextArea();
        displayArea.setEditable(false);

        VBox root = new VBox(10, gridPane, displayArea);
        primaryStage.setTitle("NATO PHOTOGRAPHY DATA");
        primaryStage.setScene(new Scene(root, 650, 400));
        primaryStage.show();
    }

    private void connectToDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Customer_Info.db");
            System.out.println("Connected to the database successfully!"); 
        } catch (Exception e) {
            System.out.println("Error while connecting to the database!");
            e.printStackTrace();
        }
    }

    private void submit() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO addresses (first_Name, last_Name, Email, Phone) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setString(4, phone);
            statement.executeUpdate();

            // Clear text boxes
            firstNameField.clear();
            lastNameField.clear();
            emailField.clear();
            phoneField.clear();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void query() {
        StringBuilder records = new StringBuilder();
    
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM addresses")) {
    
            int count = 0; // To count the number of records processed
    
            while (resultSet.next()) {
                records.append(resultSet.getString("first_Name")).append(" ")
                       .append(resultSet.getString("last_Name")).append(" - ")
                       .append(resultSet.getString("Email")).append(" - ")
                       .append(resultSet.getString("Phone")).append("\n");
                count++;
            }
    
            // Set the text of displayArea and print to console
            String data = records.toString();
            displayArea.setText(data);
            displayArea.layout();  // Ensure UI updates
            System.out.println(data);
    
            System.out.println("Query executed successfully! Processed " + count + " records.");
    
        } catch (SQLException e) {
            System.out.println("Error during query execution!");
            e.printStackTrace();
        }
    }
}