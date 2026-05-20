package ui;

import dao.DynamicDAO;
import dao.EntiteMetaDAO;
import model.EntiteChamp;
import service.StockService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Read-only panel for Out entries.
 */
public class OutPanel extends JPanel {

    private String nomEntite;
    private StockService stockService;
    private List<EntiteChamp> champs;
    private DynamicTablePanel tablePanel;

    public OutPanel(String nomEntite, StockService stockService) {
        this.nomEntite = nomEntite;
        this.stockService = stockService;
        this.champs = new EntiteMetaDAO().getChamps(nomEntite);
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

        String[] cols = buildColumnNames();
        tablePanel = new DynamicTablePanel(cols);

        JLabel lblInfo = new JLabel("Les sorties sont generees automatiquement depuis l'onglet Liste Sorties.");

        add(headerPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(lblInfo, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        try {
            DynamicDAO dao = new DynamicDAO();
            String sql = buildOutDetailsQuery();
            List<Map<String, Object>> data = dao.query(sql);
            tablePanel.refresh(data);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] buildColumnNames() {
        List<String> cols = new ArrayList<>();
        cols.add("out_id");
        cols.add("out_idlistout");
        cols.add("out_idlistinsource");
        cols.add("out_dateout");
        cols.add("out_quantite");
        cols.add("out_prixunitaire");
        cols.add("in_datein");
        cols.add("in_quantite");
        cols.add("in_prixunitaire");
        for (EntiteChamp ch : champs) {
            cols.add("in_" + ch.getNomChamp().toLowerCase());
        }
        return cols.toArray(new String[0]);
    }

    private String buildOutDetailsQuery() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("o.id AS out_id, ");
        sql.append("o.idListOut AS out_idlistout, ");
        sql.append("o.idListInSource AS out_idlistinsource, ");
        sql.append("o.dateOut AS out_dateout, ");
        sql.append("o.quantite AS out_quantite, ");
        sql.append("o.prixUnitaire AS out_prixunitaire, ");
        sql.append("i.dateIn AS in_datein, ");
        sql.append("i.quantite AS in_quantite, ");
        sql.append("i.prixUnitaire AS in_prixunitaire");

        for (EntiteChamp ch : champs) {
            sql.append(", i.").append(ch.getNomChamp())
               .append(" AS in_").append(ch.getNomChamp().toLowerCase());
        }

        sql.append(" FROM ").append(nomEntite).append("_Out o ");
        sql.append("LEFT JOIN ").append(nomEntite).append("_In i ");
        sql.append("ON i.idListIn = o.idListInSource ");
        sql.append("ORDER BY o.id");
        return sql.toString();
    }
}
