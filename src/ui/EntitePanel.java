package ui;

import model.EntiteChamp;
import service.StockService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EntitePanel extends JPanel {

    public EntitePanel(String nomEntite, List<EntiteChamp> champs, StockService stockService) {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Entrees (In)", new InPanel(nomEntite, champs, stockService));
        tabs.addTab("Liste Entrees (ListIn)", new ListInPanel(nomEntite, stockService));
        tabs.addTab("Sorties (Out)", new OutPanel(nomEntite, stockService));
        tabs.addTab("Liste Sorties (ListOut)", new ListOutPanel(nomEntite, stockService));
        add(tabs, BorderLayout.CENTER);
    }
}
