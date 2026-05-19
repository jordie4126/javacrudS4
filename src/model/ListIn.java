package model;

import java.time.LocalDateTime;

public class ListIn {
    private int id;
    private LocalDateTime dateListIn;
    private double quantiteTotale;
    private double prixMoyen;

    public ListIn() {}

    // All getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getDateListIn() { return dateListIn; }
    public void setDateListIn(LocalDateTime dateListIn) { this.dateListIn = dateListIn; }
    public double getQuantiteTotale() { return quantiteTotale; }
    public void setQuantiteTotale(double quantiteTotale) { this.quantiteTotale = quantiteTotale; }
    public double getPrixMoyen() { return prixMoyen; }
    public void setPrixMoyen(double prixMoyen) { this.prixMoyen = prixMoyen; }

    @Override
    public String toString() {
        return "ListIn{id=" + id + ", dateListIn=" + dateListIn +
               ", quantiteTotale=" + quantiteTotale + ", prixMoyen=" + prixMoyen + "}";
    }
}
