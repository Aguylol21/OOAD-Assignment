package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private DatabaseInitializer() {
        // Prevent object creation
    }

    public static void initialize() {
        createEquipmentTable();
        createRentalTable();
    }

    private static void createEquipmentTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS equipment (
                equipment_id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                daily_rate INTEGER NOT NULL CHECK (daily_rate > 0),
                category TEXT NOT NULL,
                status TEXT NOT NULL
            )
            """;

        try (
            Connection connection = DatabaseManager.getConnection();
            Statement statement = connection.createStatement()
        ) {
            statement.execute(sql);
            System.out.println("Equipment table created successfully.");

        } catch (SQLException e) {
            System.out.println("Error creating equipment table.");
            e.printStackTrace();
        }
    }

    private static void createRentalTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS rental (
                rental_id TEXT PRIMARY KEY,
                user_id TEXT NOT NULL,
                user_name TEXT NOT NULL,
                user_type TEXT NOT NULL,
                equipment_id TEXT NOT NULL,
                equipment_name TEXT NOT NULL,
                rental_date TEXT NOT NULL,
                expected_return_date TEXT NOT NULL,
                actual_return_date TEXT,
                status TEXT NOT NULL,
                total_fee INTEGER NOT NULL
            )
            """;

        try (
            Connection connection = DatabaseManager.getConnection();
            Statement statement = connection.createStatement()
        ) {
            statement.execute(sql);
            System.out.println("Rental table created successfully.");

        } catch (SQLException e) {
            System.out.println("Error creating rental table.");
            e.printStackTrace();
        }
    }
}
