import database.DatabaseInitializer;
import facade.RentalFacade;
import repository.EquipmentRepository;
import repository.RentalRepository;
import repository.SQLiteEquipmentRepository;
import repository.SQLiteRentalRepository;
import service.BillingService;
import service.DiscountPolicy;
import service.EquipmentService;
import service.PenaltyPolicy;
import service.RentalManager;
import service.StandardDiscountPolicy;
import service.StandardPenaltyPolicy;
import view.BillingPanel;
import view.EquipmentPanel;
import view.RentalPanel;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        EquipmentRepository equipmentRepository = new SQLiteEquipmentRepository();
        EquipmentService equipmentService = new EquipmentService(equipmentRepository);
        RentalRepository rentalRepository = new SQLiteRentalRepository();
        RentalManager rentalManager = new RentalManager(rentalRepository, equipmentService);
        DiscountPolicy discountPolicy = new StandardDiscountPolicy();
        PenaltyPolicy penaltyPolicy = new StandardPenaltyPolicy();
        BillingService billingService = new BillingService(
                rentalManager,
                equipmentService,
                discountPolicy,
                penaltyPolicy
        );
        RentalFacade rentalFacade = new RentalFacade(equipmentService, rentalManager, billingService);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smart Equipment Rental System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 650);
            frame.setLocationRelativeTo(null);

            EquipmentPanel equipmentPanel = new EquipmentPanel(rentalFacade);
            RentalPanel rentalPanel = new RentalPanel(rentalFacade);
            BillingPanel billingPanel = new BillingPanel(rentalFacade);

            JTabbedPane mainTabbedPane = new JTabbedPane();
            mainTabbedPane.addTab("Equipment Management", equipmentPanel);
            mainTabbedPane.addTab("Rental & Return", rentalPanel);
            mainTabbedPane.addTab("Billing & Payment", billingPanel);
            mainTabbedPane.addChangeListener(e -> {
                if (mainTabbedPane.getSelectedComponent() == equipmentPanel) {
                    equipmentPanel.refreshEquipmentData();
                }

                if (mainTabbedPane.getSelectedComponent() == rentalPanel) {
                    rentalPanel.refreshRentalData();
                }

                if (mainTabbedPane.getSelectedComponent() == billingPanel) {
                    billingPanel.refreshBillingData();
                }
            });

            frame.add(mainTabbedPane);
            frame.setVisible(true);
        });
    }
}
