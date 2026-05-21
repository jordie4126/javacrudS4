package ui;

import dao.EntiteMetaDAO;
import model.EntiteChamp;
import model.EntiteMeta;
import service.StockService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainWindow extends JFrame {

    private DefaultListModel<String> entiteListModel;
    private JList<String> entiteJList;
    private JTabbedPane tabbedPane;
    private JLabel statusLabel;
    private JLabel stockLabel;
    private JLabel methodeLabel;

    private EntiteMetaDAO metaDAO = new EntiteMetaDAO();
    private StockService stockService = new StockService();

    private String entiteActive = null;
    private EntiteMeta metaActive = null;

    public MainWindow() {
        setTitle("Gestion de Stock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));
        initComponents();
        loadEntites();
    }

    private void initComponents() {
        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFichier = new JMenu("Fichier");

        JMenuItem miNouvelle = new JMenuItem("Nouvelle Entite...");
        miNouvelle.addActionListener(e -> ouvrirNouvelleEntite());
        JMenuItem miSupprimer = new JMenuItem("Supprimer Entite");
        miSupprimer.addActionListener(e -> supprimerEntite());
        JMenuItem miRefresh = new JMenuItem("Rafraichir");
        miRefresh.addActionListener(e -> { loadEntites(); refreshAll(); });
        JMenuItem miQuitter = new JMenuItem("Quitter");
        miQuitter.addActionListener(e -> System.exit(0));

        menuFichier.add(miNouvelle);
        menuFichier.add(miSupprimer);
        menuFichier.addSeparator();
        menuFichier.add(miRefresh);
        menuFichier.addSeparator();
        menuFichier.add(miQuitter);
        menuBar.add(menuFichier);
        setJMenuBar(menuBar);

        // Left panel - entity list
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        leftPanel.setPreferredSize(new Dimension(200, 0));

        leftPanel.add(new JLabel("Entites enregistrees"), BorderLayout.NORTH);

        entiteListModel = new DefaultListModel<>();
        entiteJList = new JList<>(entiteListModel);
        entiteJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entiteJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onEntiteSelected(entiteJList.getSelectedValue());
            }
        });
        leftPanel.add(new JScrollPane(entiteJList), BorderLayout.CENTER);

        JButton btnNouvelle = new JButton("+ Nouvelle Entite");
        btnNouvelle.addActionListener(e -> ouvrirNouvelleEntite());
        leftPanel.add(btnNouvelle, BorderLayout.SOUTH);

        // Center - tabs
        tabbedPane = new JTabbedPane();

        JPanel placeholder = new JPanel(new GridBagLayout());
        placeholder.add(new JLabel("Selectionnez ou creez une entite pour commencer"));
        tabbedPane.addTab("Accueil", placeholder);

        // Status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));

        statusLabel = new JLabel("Pret");
        methodeLabel = new JLabel("");
        stockLabel = new JLabel("");

        JPanel rightStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightStatus.add(methodeLabel);
        rightStatus.add(stockLabel);

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(rightStatus, BorderLayout.EAST);

        // Layout
        setLayout(new BorderLayout());
        add(leftPanel, BorderLayout.WEST);
        add(tabbedPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void loadEntites() {
        entiteListModel.clear();
        try {
            List<EntiteMeta> entites = metaDAO.loadAllEntites();
            for (EntiteMeta meta : entites) {
                entiteListModel.addElement(meta.getNom());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement:\n" + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEntiteSelected(String nom) {
        if (nom == null) return;
        this.entiteActive = nom;

        metaActive = metaDAO.findByNom(nom);
        if (metaActive == null) return;

        List<EntiteChamp> champs = metaDAO.getChamps(nom);

        tabbedPane.removeAll();

        InPanel inPanel = new InPanel(nom, champs, stockService);
        ListInPanel listInPanel = new ListInPanel(nom, stockService);
        OutPanel outPanel = new OutPanel(nom, stockService);
        ListOutPanel listOutPanel = new ListOutPanel(nom, stockService);
        InRestantPanel inRestantPanel = new InRestantPanel(nom, stockService);

        tabbedPane.addTab("Entrees (In)", inPanel);
        tabbedPane.addTab("Liste Entrees (ListIn)", listInPanel);
        tabbedPane.addTab("Sorties (Out)", outPanel);
        tabbedPane.addTab("Liste Sorties (ListOut)", listOutPanel);
        tabbedPane.addTab("Stock restant", inRestantPanel);

        updateStatus();
        statusLabel.setText("Entite active: " + nom);
    }

    private void updateStatus() {
        if (entiteActive != null && metaActive != null) {
            methodeLabel.setText("Methode: " + metaActive.getMethodeStock().name());
            try {
                double stock = stockService.getStockDisponible(entiteActive);
                stockLabel.setText(String.format("Stock: %.2f", stock));
            } catch (Exception e) {
                stockLabel.setText("Stock: N/A");
            }
        }
    }

    public void refreshAll() {
        if (entiteActive != null) {
            onEntiteSelected(entiteActive);
        }
    }

    private void ouvrirNouvelleEntite() {
        NewEntiteDialog dialog = new NewEntiteDialog(this, stockService);
        dialog.setVisible(true);
        if (dialog.isCreated()) {
            loadEntites();
            entiteJList.setSelectedValue(dialog.getCreatedName(), true);
        }
    }

    private void supprimerEntite() {
        if (entiteActive == null) {
            JOptionPane.showMessageDialog(this, "Selectionnez une entite.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer \"" + entiteActive + "\" et toutes ses donnees ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                metaDAO.deleteEntite(entiteActive);
                entiteActive = null;
                metaActive = null;
                tabbedPane.removeAll();
                tabbedPane.addTab("Accueil", new JPanel(new GridBagLayout()) {{
                    add(new JLabel("Selectionnez ou creez une entite"));
                }});
                methodeLabel.setText("");
                stockLabel.setText("");
                statusLabel.setText("Entite supprimee");
                loadEntites();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur:\n" + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
