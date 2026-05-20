package ui;

import dao.DynamicDAO;
import service.StockService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * Panel for ListOut entries with stock exit trigger.
 */
public class ListOutPanel extends JPanel {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String nomEntite;
    private StockService stockService;
    private DynamicTablePanel tablePanel;
    private JLabel lblStockDispo;

    public ListOutPanel(String nomEntite, StockService stockService) {
        this.nomEntite = nomEntite;
        this.stockService = stockService;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        // Sortie form
        JPanel sortiePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        sortiePanel.setBorder(BorderFactory.createTitledBorder("Nouvelle Sortie"));

        sortiePanel.add(new JLabel("Date & heure (yyyy-MM-dd HH:mm):"));
        JTextField txtDateTime = new JTextField(14);
        txtDateTime.setText(LocalDateTime.now().format(DATE_TIME_FORMAT));
        sortiePanel.add(txtDateTime);

        sortiePanel.add(new JLabel("Quantite a sortir:"));
        JTextField txtQuantite = new JTextField(10);
        sortiePanel.add(txtQuantite);

        JButton btnSortie = new JButton("Effectuer la Sortie");
        btnSortie.addActionListener(e -> {
            try {
                LocalDateTime dateListOut = LocalDateTime.parse(txtDateTime.getText().trim(), DATE_TIME_FORMAT);
                double q = Double.parseDouble(txtQuantite.getText().trim());
                if (q <= 0) throw new IllegalArgumentException("Quantite doit etre positive.");

                int confirm = JOptionPane.showConfirmDialog(this,
                        String.format("Sortie de %.2f unites de %s ?", q, nomEntite),
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    stockService.sortie(nomEntite, q, dateListOut);
                    txtDateTime.setText(LocalDateTime.now().format(DATE_TIME_FORMAT));
                    txtQuantite.setText("");
                    refreshTable();
                    updateStockDispo();
                    JOptionPane.showMessageDialog(this, "Sortie effectuee!", "Succes", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Date & heure invalide (format: yyyy-MM-dd HH:mm).",
                        "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Nombre invalide.", "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Stock Insuffisant", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        sortiePanel.add(btnSortie);

        lblStockDispo = new JLabel("");
        sortiePanel.add(Box.createHorizontalStrut(20));
        sortiePanel.add(lblStockDispo);

        // Table
        String[] cols = {"id", "datelistout", "quantitetotale", "prixmoyenunitaire"};
        tablePanel = new DynamicTablePanel(cols);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton btnRefresh = new JButton("Rafraichir");
        btnRefresh.addActionListener(e -> { refreshTable(); updateStockDispo(); });
        btnPanel.add(btnRefresh);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(sortiePanel, BorderLayout.NORTH);
        topContainer.add(btnPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);

        updateStockDispo();
    }

    private void refreshTable() {
        try {
            DynamicDAO dao = new DynamicDAO();
            List<Map<String, Object>> data = dao.findAll(nomEntite + "_ListOut");
            tablePanel.refresh(data);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStockDispo() {
        try {
            double stock = stockService.getStockDisponible(nomEntite);
            lblStockDispo.setText(String.format("Stock disponible: %.2f", stock));
        } catch (Exception e) {
            lblStockDispo.setText("Stock: N/A");
        }
    }
}
