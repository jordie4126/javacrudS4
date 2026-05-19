package ui;

import model.EntiteChamp;
import model.StockMethode;
import service.StockService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NewEntiteDialog extends JDialog {

    private boolean created = false;
    private String createdName = null;

    private JTextField txtNom;
    private JComboBox<StockMethode> cboMethode;
    private DefaultTableModel champsModel;
    private JTable champsTable;
    private StockService stockService;

    private static final String[] TYPES_DISPONIBLES = {"String", "double", "int", "boolean"};

    public NewEntiteDialog(JFrame parent, StockService stockService) {
        super(parent, "Nouvelle Entite", true);
        this.stockService = stockService;
        setSize(550, 500);
        setLocationRelativeTo(parent);
        setResizable(true);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top form
        JPanel topForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        topForm.add(new JLabel("Nom de l'entite:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNom = new JTextField(20);
        topForm.add(txtNom, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        topForm.add(new JLabel("Methode de stock:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cboMethode = new JComboBox<>(StockMethode.values());
        topForm.add(cboMethode, gbc);

        mainPanel.add(topForm, BorderLayout.NORTH);

        // Fields table
        JPanel champsPanel = new JPanel(new BorderLayout(5, 5));
        champsPanel.setBorder(BorderFactory.createTitledBorder("Attributs specifiques"));

        champsModel = new DefaultTableModel(new String[]{"Nom du champ", "Type", "Label (affichage)"}, 0);
        champsTable = new JTable(champsModel);

        // Set type column as combo box
        JComboBox<String> typeCombo = new JComboBox<>(TYPES_DISPONIBLES);
        champsTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(typeCombo));

        champsPanel.add(new JScrollPane(champsTable), BorderLayout.CENTER);

        // Buttons for add/remove fields
        JPanel champsBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JButton btnAjouterChamp = new JButton("+ Ajouter attribut");
        btnAjouterChamp.addActionListener(e -> {
            champsModel.addRow(new Object[]{"", "String", ""});
        });

        JButton btnSupprimerChamp = new JButton("- Supprimer");
        btnSupprimerChamp.addActionListener(e -> {
            int row = champsTable.getSelectedRow();
            if (row >= 0) champsModel.removeRow(row);
        });

        champsBtnPanel.add(btnAjouterChamp);
        champsBtnPanel.add(btnSupprimerChamp);
        champsPanel.add(champsBtnPanel, BorderLayout.SOUTH);

        mainPanel.add(champsPanel, BorderLayout.CENTER);

        // Bottom buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(e -> dispose());

        JButton btnValider = new JButton("Creer");
        btnValider.addActionListener(e -> valider());

        btnPanel.add(btnAnnuler);
        btnPanel.add(btnValider);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void valider() {
        // Stop any cell editing
        if (champsTable.isEditing()) {
            champsTable.getCellEditor().stopCellEditing();
        }

        String nom = txtNom.getText().trim();
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom est obligatoire.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!nom.matches("[A-Za-z][A-Za-z0-9_]*")) {
            JOptionPane.showMessageDialog(this,
                    "Le nom doit commencer par une lettre et contenir uniquement lettres, chiffres, underscores.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Collect fields
        List<EntiteChamp> champs = new ArrayList<>();
        for (int i = 0; i < champsModel.getRowCount(); i++) {
            String nomChamp = ((String) champsModel.getValueAt(i, 0)).trim();
            String type = ((String) champsModel.getValueAt(i, 1)).trim();
            String label = ((String) champsModel.getValueAt(i, 2)).trim();

            if (nomChamp.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Le nom du champ a la ligne " + (i + 1) + " est vide.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!nomChamp.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
                JOptionPane.showMessageDialog(this,
                        "Le nom du champ \"" + nomChamp + "\" est invalide.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (label.isEmpty()) label = nomChamp;

            champs.add(new EntiteChamp(nom, nomChamp, type, label, i));
        }

        StockMethode methode = (StockMethode) cboMethode.getSelectedItem();

        try {
            stockService.enregistrerEntite(nom, methode, champs);
            this.created = true;
            this.createdName = nom;
            JOptionPane.showMessageDialog(this,
                    "Entite \"" + nom + "\" creee!\nMethode: " + methode.name() +
                    "\nAttributs: " + champs.size() + "\n4 tables generees.",
                    "Succes", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur:\n" + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isCreated() { return created; }
    public String getCreatedName() { return createdName; }
}
