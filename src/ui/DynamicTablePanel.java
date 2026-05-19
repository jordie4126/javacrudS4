package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Dynamic table panel that displays data from List<Map<String, Object>>.
 * Column names are derived from the map keys of the first row.
 */
public class DynamicTablePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private String[] columnNames;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Constructor with explicit column names.
     */
    public DynamicTablePanel(String[] columnNames) {
        this.columnNames = columnNames;
        setLayout(new BorderLayout());
        buildTable();
    }

    private void buildTable() {
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getTableHeader().setReorderingAllowed(false);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    /**
     * Refresh with Map-based data.
     */
    public void refresh(List<Map<String, Object>> data) {
        tableModel.setRowCount(0);
        if (data == null || data.isEmpty()) return;

        // If column names weren't set, derive from first row
        if (columnNames == null || columnNames.length == 0) {
            Map<String, Object> first = data.get(0);
            columnNames = first.keySet().toArray(new String[0]);
            tableModel.setColumnIdentifiers(columnNames);
        }

        for (Map<String, Object> row : data) {
            Object[] rowData = new Object[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                Object val = row.get(columnNames[i]);
                rowData[i] = formatValue(val);
            }
            tableModel.addRow(rowData);
        }
    }

    /**
     * Get selected row index, -1 if none.
     */
    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    /**
     * Get the ID from the selected row (assumes first column is "id").
     */
    public int getSelectedId() {
        int row = getSelectedRow();
        if (row < 0) return -1;
        Object val = tableModel.getValueAt(row, 0);
        if (val == null) return -1;
        return Integer.parseInt(val.toString());
    }

    public JTable getTable() { return table; }
    public DefaultTableModel getTableModel() { return tableModel; }

    private String formatValue(Object val) {
        if (val == null) return "";
        if (val instanceof LocalDateTime) {
            return ((LocalDateTime) val).format(DATE_FMT);
        }
        if (val instanceof Double) {
            double d = (Double) val;
            if (d == Math.floor(d) && d < 1e10) return String.format("%.0f", d);
            return String.format("%.2f", d);
        }
        return val.toString();
    }
}
