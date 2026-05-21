package ui;

import service.StockService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class InRestantPanel extends JPanel {

    private final String nomEntite;
    private final StockService stockService;
    private DynamicTablePanel tablePanel;
    private JLabel valeurStockLabel;

    public InRestantPanel(String nomEntite, StockService stockService) {
        this.nomEntite = nomEntite;
        this.stockService = stockService;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton btnRefresh = new JButton("Rafraichir");
        btnRefresh.addActionListener(e -> refreshTable());
        topPanel.add(btnRefresh);

        String[] cols = {"id", "datelistin", "quantiterestante", "prixmoyen", "valeurglo"};
        tablePanel = new DynamicTablePanel(cols);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        valeurStockLabel = new JLabel("Valeur stock: 0.00");
        bottomPanel.add(valeurStockLabel);

        add(topPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        try {
            List<Map<String, Object>> data = stockService.getInRestantDetails(nomEntite);
            tablePanel.refresh(data);
            double totalValeur = 0;
            for (Map<String, Object> row : data) {
                Double valeur = getNumber(row, "valeurglo");
                if (valeur == null) {
                    Double q = getNumber(row, "quantiterestante");
                    Double p = getNumber(row, "prixmoyen");
                    if (q != null && p != null) {
                        valeur = q * p;
                    }
                }
                if (valeur != null) {
                    totalValeur += valeur;
                }
            }
            valeurStockLabel.setText(String.format("Valeur stock: %.2f", totalValeur));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Double getNumber(Map<String, Object> row, String key) {
        Object val = row.get(key);
        if (val == null) {
            val = row.get(key.toLowerCase());
        }
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        if (val != null) {
            try {
                return Double.parseDouble(val.toString());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
