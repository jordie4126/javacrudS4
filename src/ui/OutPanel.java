package ui;

import dao.DynamicDAO;
import service.StockService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Read-only panel for Out entries.
 */
public class OutPanel extends JPanel {

    private String nomEntite;
    private StockService stockService;
    private DynamicTablePanel tablePanel;

    public OutPanel(String nomEntite, StockService stockService) {
        this.nomEntite = nomEntite;
        this.stockService = stockService;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(new JLabel("Details des sorties (calculees automatiquement)"), BorderLayout.WEST);

        JButton btnRefresh = new JButton("Rafraichir");
        btnRefresh.addActionListener(e -> refreshTable());
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        String[] cols = {"id", "idlistout", "idlistinsource", "quantite", "prixunitaire"};
        tablePanel = new DynamicTablePanel(cols);

        JLabel lblInfo = new JLabel("Les sorties sont generees automatiquement depuis l'onglet Liste Sorties.");

        add(headerPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(lblInfo, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        try {
            DynamicDAO dao = new DynamicDAO();
            List<Map<String, Object>> data = dao.findAll(nomEntite + "_Out");
            tablePanel.refresh(data);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
