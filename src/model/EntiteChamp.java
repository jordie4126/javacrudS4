package model;

/**
 * Represents a dynamic field definition for a user-created entity.
 * Stored in the entite_champs table.
 */
public class EntiteChamp {
    private int id;
    private String nomEntite;
    private String nomChamp;
    private String typeJava;   // "String", "int", "double", "boolean"
    private String label;
    private int ordre;

    public EntiteChamp() {}

    public EntiteChamp(String nomEntite, String nomChamp, String typeJava, String label, int ordre) {
        this.nomEntite = nomEntite;
        this.nomChamp = nomChamp;
        this.typeJava = typeJava;
        this.label = label;
        this.ordre = ordre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNomEntite() { return nomEntite; }
    public void setNomEntite(String nomEntite) { this.nomEntite = nomEntite; }
    public String getNomChamp() { return nomChamp; }
    public void setNomChamp(String nomChamp) { this.nomChamp = nomChamp; }
    public String getTypeJava() { return typeJava; }
    public void setTypeJava(String typeJava) { this.typeJava = typeJava; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }

    /**
     * Returns the SQL type corresponding to this field's Java type.
     */
    public String getSqlType() {
        switch (typeJava) {
            case "String": return "VARCHAR(255)";
            case "int": return "INT";
            case "double": return "DOUBLE PRECISION";
            case "boolean": return "BOOLEAN";
            default: return "VARCHAR(255)";
        }
    }

    /**
     * Convert a string value to the appropriate Java type.
     */
    public Object convertValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            switch (typeJava) {
                case "int": return 0;
                case "double": return 0.0;
                case "boolean": return false;
                default: return "";
            }
        }
        switch (typeJava) {
            case "int": return Integer.parseInt(value.trim());
            case "double": return Double.parseDouble(value.trim());
            case "boolean": return Boolean.parseBoolean(value.trim());
            default: return value.trim();
        }
    }

    @Override
    public String toString() {
        return "EntiteChamp{" + nomChamp + " (" + typeJava + ") label='" + label + "'}";
    }
}
