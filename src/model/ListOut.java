package model;

import java.time.LocalDateTime;

public class ListOut {
    private int id;
    private LocalDateTime dateListOut;
    private double quantiteTotale;
    private double prixMoyenUnitaire;

    public ListOut() {}

    // All getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getDateListOut() { return dateListOut; }
    public void setDateListOut(LocalDateTime dateListOut) { this.dateListOut = dateListOut; }
    public double getQuantiteTotale() { return quantiteTotale; }
    public void setQuantiteTotale(double quantiteTotale) { this.quantiteTotale = quantiteTotale; }
    public double getPrixMoyenUnitaire() { return prixMoyenUnitaire; }
    public void setPrixMoyenUnitaire(double prixMoyenUnitaire) { this.prixMoyenUnitaire = prixMoyenUnitaire; }

    @Override
    public String toString() {
        return "ListOut{id=" + id + ", dateListOut=" + dateListOut +
               ", quantiteTotale=" + quantiteTotale + ", prixMoyenUnitaire=" + prixMoyenUnitaire + "}";
    }
}
