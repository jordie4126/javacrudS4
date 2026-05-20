package model;

import java.time.LocalDateTime;

public class Out {
    private int id;
    private int idListOut;
    private int idListInSource;
    private LocalDateTime dateOut;
    private double quantite;
    private double prixUnitaire;

    public Out() {}

    // All getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdListOut() { return idListOut; }
    public void setIdListOut(int idListOut) { this.idListOut = idListOut; }
    public int getIdListInSource() { return idListInSource; }
    public void setIdListInSource(int idListInSource) { this.idListInSource = idListInSource; }
    public LocalDateTime getDateOut() { return dateOut; }
    public void setDateOut(LocalDateTime dateOut) { this.dateOut = dateOut; }
    public double getQuantite() { return quantite; }
    public void setQuantite(double quantite) { this.quantite = quantite; }
    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    @Override
    public String toString() {
        return "Out{id=" + id + ", idListOut=" + idListOut + ", idListInSource=" + idListInSource +
               ", dateOut=" + dateOut + ", quantite=" + quantite + ", prixUnitaire=" + prixUnitaire + "}";
    }
}
