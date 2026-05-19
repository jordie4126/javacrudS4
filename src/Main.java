import db.DBConnection;
import ui.MainWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            DBConnection.getInstance().initMetaTable();
            System.out.println("Application initialisee.");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Impossible de se connecter a PostgreSQL.\n" +
                    "Verifiez que le serveur est lance et que la base 'gene' existe.\n\n" +
                    "Erreur: " + e.getMessage(),
                    "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
