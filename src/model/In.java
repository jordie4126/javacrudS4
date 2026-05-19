package model;

import java.time.LocalDateTime;

public abstract class In {
    private int id;
    private int idListIn;
    private LocalDateTime dateIn;
    private double quantite;
    private double prixUnitaire;

    // Default constructor (needed for reflection)
    public In() {}

    // All getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdListIn() { return idListIn; }
    public void setIdListIn(int idListIn) { this.idListIn = idListIn; }
    public LocalDateTime getDateIn() { return dateIn; }
    public void setDateIn(LocalDateTime dateIn) { this.dateIn = dateIn; }
    public double getQuantite() { return quantite; }
    public void setQuantite(double quantite) { this.quantite = quantite; }
    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    @Override
    public String toString() {
        return "In{id=" + id + ", idListIn=" + idListIn + ", dateIn=" + dateIn +
               ", quantite=" + quantite + ", prixUnitaire=" + prixUnitaire + "}";
    }
}
