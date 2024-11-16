import java.sql.*;
import java.util.Scanner;

public class CourierManagementSystem {
    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            createTableIfNotExists(connection);

            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
                System.out.println("\nCourier Management System");
                System.out.println("1. Enter Details");
                System.out.println("2. Fetch Details by Date");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        enterDetails(connection, scanner);
                        break;
                    case 2:
                        fetchDetailsByDate(connection, scanner);
                        break;
                    case 3:
                        System.out.println("Exiting the program. Thank you!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }
            } while (choice != 3);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Load the MySQL JDBC Driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // JDBC URL, username, and password
        String jdbcURL = "jdbc:mysql://localhost:3306/courier?useSSL=false&serverTimezone=UTC";
        String dbUsername = "root"; // Replace with your MySQL username
        String dbPassword = "root"; // Replace with your MySQL password

        // Return the connection object
        return DriverManager.getConnection(jdbcURL, dbUsername, dbPassword);
    }

    private static void createTableIfNotExists(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS couriers ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "sender_name VARCHAR(255) NOT NULL, "
                + "receiver_name VARCHAR(255) NOT NULL, "
                + "departure_place VARCHAR(255) NOT NULL, "
                + "arrival_place VARCHAR(255) NOT NULL, "
                + "date DATE NOT NULL"
                + ")";
        try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
            preparedStatement.executeUpdate();
        }
    }

    private static void enterDetails(Connection connection, Scanner scanner) throws SQLException {
        scanner.nextLine(); // Consume the newline character
        System.out.println("\nEnter Courier Details");

        System.out.print("Enter Sender's Name: ");
        String senderName = scanner.nextLine();

        System.out.print("Enter Receiver's Name: ");
        String receiverName = scanner.nextLine();

        System.out.print("Enter Departure Place: ");
        String departurePlace = scanner.nextLine();

        System.out.print("Enter Arrival Place: ");
        String arrivalPlace = scanner.nextLine();

        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = scanner.next();

        String insertSQL = "INSERT INTO couriers (sender_name, receiver_name, departure_place, arrival_place, date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, senderName);
            preparedStatement.setString(2, receiverName);
            preparedStatement.setString(3, departurePlace);
            preparedStatement.setString(4, arrivalPlace);
            preparedStatement.setDate(5, Date.valueOf(date));

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Courier details entered successfully!");
            } else {
                System.out.println("Failed to enter courier details.");
            }
        }
    }

    private static void fetchDetailsByDate(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("\nFetch Courier Details by Date");
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = scanner.next();

        String selectSQL = "SELECT * FROM couriers WHERE date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setDate(1, Date.valueOf(date));
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("\nCourier Details on " + date + ":");
                System.out.printf("%-20s %-20s %-20s %-20s %-10s\n",
                        "Sender's Name", "Receiver's Name", "Departure Place", "Arrival Place", "Date");

                do {
                    System.out.printf("%-20s %-20s %-20s %-20s %-10s\n",
                            resultSet.getString("sender_name"),
                            resultSet.getString("receiver_name"),
                            resultSet.getString("departure_place"),
                            resultSet.getString("arrival_place"),
                            resultSet.getString("date"));
                } while (resultSet.next());

            } else {
                System.out.println("No courier details found on " + date);
            }
        }
    }
}