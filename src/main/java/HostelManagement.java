
import java.sql.*;
import java.util.Scanner;

public class HostelManagement {

    // Database configuration
    static final String URL =
            "jdbc:mysql://localhost:3306/hostel_db";

    static final String USER = "root";

    static final String PASSWORD = "vaishali8#10";

    static Scanner sc = new Scanner(System.in);


    // DATABASE CONNECTION METHOD
    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                URL, USER, PASSWORD);
    }


    // (A) INSERT THREE STUDENT RECORDS
    public static void insertStudents() {

        String sql = "INSERT INTO HostelStudent " +
                "(student_id, student_name, roll_number, " +
                "hostel_name, room_number, fee) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            for (int i = 1; i <= 3; i++) {

                System.out.println("\nEnter details of Student " + i);

                System.out.print("Student ID: ");
                int id = sc.nextInt();
                sc.nextLine();

                System.out.print("Student Name: ");
                String name = sc.nextLine();

                System.out.print("Roll Number: ");
                String roll = sc.nextLine();

                System.out.print("Hostel Name: ");
                String hostel = sc.nextLine();

                System.out.print("Room Number: ");
                String room = sc.nextLine();

                System.out.print("Fee: ");
                double fee = sc.nextDouble();
                sc.nextLine();

                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setString(3, roll);
                ps.setString(4, hostel);
                ps.setString(5, room);
                ps.setDouble(6, fee);

                try {
                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        System.out.println(
                                "Student inserted successfully!");
                    }

                } catch (SQLIntegrityConstraintViolationException e) {
                    System.out.println(
                            "Duplicate Student ID! Try another ID.");
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // (B) CONDITIONAL RETRIEVAL AND SORTING
    public static void retrieveStudents() {

        String sql = "SELECT * FROM HostelStudent " +
                "WHERE hostel_name = ? AND fee > 50000 " +
                "ORDER BY fee DESC";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            System.out.print("Enter Hostel Name: ");
            String hostel = sc.nextLine();

            ps.setString(1, hostel);

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                while (rs.next()) {

                    found = true;

                    System.out.println(
                            "ID: " + rs.getInt("student_id") +
                                    " | Name: " + rs.getString("student_name") +
                                    " | Roll: " + rs.getString("roll_number") +
                                    " | Hostel: " + rs.getString("hostel_name") +
                                    " | Room: " + rs.getString("room_number") +
                                    " | Fee: " + rs.getDouble("fee"));
                }

                if (!found) {
                    System.out.println("No matching students found.");
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // (C) UPDATE HOSTEL FEE
    public static void updateFee() {

        String sql = "UPDATE HostelStudent " +
                "SET fee = ? WHERE student_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            System.out.print("Enter Student ID: ");
            int id = sc.nextInt();

            System.out.print("Enter New Fee: ");
            double fee = sc.nextDouble();
            sc.nextLine();

            ps.setDouble(1, fee);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Fee updated successfully!");
            } else {
                System.out.println("Student does not exist.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // (D) SEARCH AND MODIFY ROOM NUMBER
    public static void updateRoom() {

        String searchSQL =
                "SELECT * FROM HostelStudent WHERE student_id = ?";

        try (Connection con = getConnection();
             PreparedStatement searchPS =
                     con.prepareStatement(searchSQL)) {

            System.out.print("Enter Student ID: ");
            int id = sc.nextInt();
            sc.nextLine();

            searchPS.setInt(1, id);

            try (ResultSet rs = searchPS.executeQuery()) {

                if (rs.next()) {

                    System.out.println("Student Found!");
                    System.out.println(
                            "Name: " + rs.getString("student_name"));
                    System.out.println(
                            "Old Room: " + rs.getString("room_number"));

                    System.out.print("Enter New Room Number: ");
                    String room = sc.nextLine();

                    String updateSQL =
                            "UPDATE HostelStudent SET room_number = ? " +
                                    "WHERE student_id = ?";

                    try (PreparedStatement updatePS =
                                 con.prepareStatement(updateSQL)) {

                        updatePS.setString(1, room);
                        updatePS.setInt(2, id);

                        updatePS.executeUpdate();
                    }

                    System.out.println("Room updated successfully!");

                    String showSQL =
                            "SELECT * FROM HostelStudent WHERE student_id = ?";

                    try (PreparedStatement showPS =
                                 con.prepareStatement(showSQL)) {

                        showPS.setInt(1, id);

                        try (ResultSet updated = showPS.executeQuery()) {

                            if (updated.next()) {
                                System.out.println(
                                        "Updated Details: " +
                                                updated.getInt("student_id") + " | " +
                                                updated.getString("student_name") + " | " +
                                                updated.getString("roll_number") + " | " +
                                                updated.getString("hostel_name") + " | " +
                                                updated.getString("room_number") + " | " +
                                                updated.getDouble("fee"));
                            }
                        }
                    }

                } else {
                    System.out.println("Student not found!");
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // (E) AGGREGATE QUERY AND CONDITIONAL DELETION
    public static void totalFeeAndDelete() {

        try (Connection con = getConnection()) {

            String sumSQL =
                    "SELECT SUM(fee) AS total FROM HostelStudent";

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sumSQL)) {

                if (rs.next()) {
                    System.out.println(
                            "Total Hostel Fee Collected: " +
                                    rs.getDouble("total"));
                }
            }

            System.out.print("Enter Student ID to Delete: ");
            int id = sc.nextInt();
            sc.nextLine();

            String deleteSQL =
                    "DELETE FROM HostelStudent " +
                            "WHERE student_id = ? AND fee > 0";

            try (PreparedStatement ps =
                         con.prepareStatement(deleteSQL)) {

                ps.setInt(1, id);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    System.out.println("Student deleted successfully!");
                } else {
                    System.out.println(
                            "Cannot delete! Student not found or fee is not paid.");
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // MAIN METHOD - MENU DRIVEN APPLICATION
    public static void main(String[] args) {

        int choice;

        do {

            System.out.println("\n===== KIET HOSTEL MANAGEMENT SYSTEM =====");
            System.out.println("1. Insert Student Records");
            System.out.println("2. Retrieve Students by Hostel and Fee");
            System.out.println("3. Update Hostel Fee");
            System.out.println("4. Search and Update Room Number");
            System.out.println("5. Display Total Fee and Delete Student");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    insertStudents();
                    break;

                case 2:
                    retrieveStudents();
                    break;

                case 3:
                    updateFee();
                    break;

                case 4:
                    updateRoom();
                    break;

                case 5:
                    totalFeeAndDelete();
                    break;

                case 0:
                    System.out.println("Thank you!");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 0);

        sc.close();
    }
}
