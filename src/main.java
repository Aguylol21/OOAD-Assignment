import database.DatabaseInitializer;
import repository.EquipmentRepository;
import repository.SQLiteEquipmentRepository;
import service.EquipmentService;
import view.EquipmentPanel;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        EquipmentRepository equipmentRepository = new SQLiteEquipmentRepository();
        EquipmentService equipmentService = new EquipmentService(equipmentRepository);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smart Equipment Rental System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.add(new EquipmentPanel(equipmentService));
            frame.setVisible(true);
        });
    }
}