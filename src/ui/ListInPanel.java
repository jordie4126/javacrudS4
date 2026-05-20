package ui;

import dao.DynamicDAO;
import model.ListIn;
import service.StockService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * Panel for managing ListIn entries (grouped stock entries).
 */
public class ListInPanel extends JPanel {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private String nomEntite;
    private StockService stockService;
    private DynamicTablePanel tablePanel;

    public ListInPanel(String nomEntite, StockService stockService) {
        this.nomEntite = nomEntite;
        this.stockService = stockService;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        // Input form
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Nouvelle Entree Groupee"));

        inputPanel.add(new JLabel("Date & heure (yyyy-MM-dd HH:mm):"));
        JTextField txtDateTime = new JTextField(14);
        txtDateTime.setText(LocalDateTime.now().format(DATE_TIME_FORMAT));
        inputPanel.add(txtDateTime);

        inputPanel.add(new JLabel("Quantite:"));
        JTextField txtQuantite = new JTextField(10);
        inputPanel.add(txtQuantite);

        inputPanel.add(new JLabel("Prix Moyen:"));
        JTextField txtPrixMoyen = new JTextField(10);
        inputPanel.add(txtPrixMoyen);

        JButton btnAjouter = new JButton("Ajouter ListIn");
        btnAjouter.addActionListener(e -> {
            try {
                LocalDateTime dateListIn = LocalDateTime.parse(txtDateTime.getText().trim(), DATE_TIME_FORMAT);
                double q = Double.parseDouble(txtQuantite.getText().trim());
                double p = Double.parseDouble(txtPrixMoyen.getText().trim());
                if (q <= 0) throw new IllegalArgumentException("Quantite doit etre positive.");
                stockService.creerListIn(nomEntite, q, p, dateListIn);
                txtDateTime.setText(LocalDateTime.now().format(DATE_TIME_FORMAT));
                txtQuantite.setText("");
                txtPrixMoyen.setText("");
                refreshTable();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Date & heure invalide (format: yyyy-MM-dd HH:mm).",
                        "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Nombres invalides.", "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        inputPanel.add(btnAjouter);

        // Table
        String[] cols = {"id", "datelistin", "quantitetotale", "prixmoyen"};
        tablePanel = new DynamicTablePanel(cols);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(e -> supprimer());
        JButton btnRefresh = new JButton("Rafraichir");
        btnRefresh.addActionListener(e -> refreshTable());
        btnPanel.add(btnSupprimer);
        btnPanel.add(btnRefresh);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(inputPanel, BorderLayout.NORTH);
        topContainer.add(btnPanel, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void refreshTable() {
        try {
            DynamicDAO dao = new DynamicDAO();
            List<Map<String, Object>> data = dao.findAll(nomEntite + "_ListIn");
            tablePanel.refresh(data);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimer() {
        int selectedId = tablePanel.getSelectedId();
        if (selectedId < 0) {
            JOptionPane.showMessageDialog(this, "Selectionnez une ligne.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer ListIn id=" + selectedId + " et ses entrees In ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                stockService.deleteListIn(nomEntite, selectedId);
                refreshTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
