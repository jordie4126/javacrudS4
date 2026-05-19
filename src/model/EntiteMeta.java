package model;

import java.time.LocalDateTime;

public class EntiteMeta {
    private int id;
    private String nom;
    private StockMethode methodeStock;
    private LocalDateTime dateCreation;

    public EntiteMeta() {}

    public EntiteMeta(String nom, StockMethode methodeStock) {
        this.nom = nom;
        this.methodeStock = methodeStock;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public StockMethode getMethodeStock() { return methodeStock; }
    public void setMethodeStock(StockMethode methodeStock) { this.methodeStock = methodeStock; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
