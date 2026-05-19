package ui;

import model.EntiteChamp;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates a form dynamically from EntiteChamp definitions.
 * Common fields (quantite, prixUnitaire) are always present.
 */
public class DynamicFormPanel extends JPanel {

    private Map<String, JTextField> fieldMap = new LinkedHashMap<>();
    private JTextField txtQuantite;
    private JTextField txtPrixUnitaire;
    private List<EntiteChamp> champs;

    public DynamicFormPanel(List<EntiteChamp> champs) {
        this.champs = champs;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Formulaire de saisie"));
        buildForm();
    }

    private void buildForm() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        // Common field: quantite
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        add(new JLabel("Quantite:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtQuantite = new JTextField(15);
        add(txtQuantite, gbc);
        row++;

        // Common field: prixUnitaire
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        add(new JLabel("Prix Unitaire:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPrixUnitaire = new JTextField(15);
        add(txtPrixUnitaire, gbc);
        row++;

        // Separator
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        add(new JSeparator(), gbc);
        gbc.gridwidth = 1;
        row++;

        // Dynamic fields from EntiteChamp
        for (EntiteChamp ch : champs) {
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
            add(new JLabel(ch.getLabel() + ":"), gbc);

            gbc.gridx = 1; gbc.weightx = 1.0;
            JTextField txt = new JTextField(15);
            add(txt, gbc);
            fieldMap.put(ch.getNomChamp(), txt);
            row++;
        }
    }

    /**
     * Read all values as a Map ready for DB insertion.
     * Includes common fields + dynamic fields.
     */
    public Map<String, Object> readValues() throws IllegalArgumentException {
        double quantite;
        double prixUnitaire;
        try {
            quantite = Double.parseDouble(txtQuantite.getText().trim());
            prixUnitaire = Double.parseDouble(txtPrixUnitaire.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantite et Prix Unitaire doivent etre des nombres.");
        }
        if (quantite <= 0) throw new IllegalArgumentException("La quantite doit etre positive.");
        if (prixUnitaire < 0) throw new IllegalArgumentException("Le prix ne peut pas etre negatif.");

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("quantite", quantite);
        data.put("prixUnitaire", prixUnitaire);

        // Dynamic fields
        for (EntiteChamp ch : champs) {
            JTextField txt = fieldMap.get(ch.getNomChamp());
            if (txt != null) {
                String value = txt.getText().trim();
                data.put(ch.getNomChamp(), ch.convertValue(value));
            }
        }

        return data;
    }

    public double getQuantite() {
        return Double.parseDouble(txtQuantite.getText().trim());
    }

    public double getPrixUnitaire() {
        return Double.parseDouble(txtPrixUnitaire.getText().trim());
    }

    public void clearForm() {
        txtQuantite.setText("");
        txtPrixUnitaire.setText("");
        for (JTextField txt : fieldMap.values()) {
            txt.setText("");
        }
    }
}
