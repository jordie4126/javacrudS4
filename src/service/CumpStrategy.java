package service;

import model.ListIn;
import model.Out;

import java.util.ArrayList;
import java.util.List;

public class CumpStrategy implements SortieStrategy {

    @Override
    public List<Out> calculerSorties(List<ListIn> stockDisponible, double quantiteDemandee) {
        // Calculate the global CUMP (Coût Unitaire Moyen Pondéré)
        double totalQuantite = 0;
        double totalValeur = 0;

        for (ListIn listIn : stockDisponible) {
            if (listIn.getQuantiteTotale() > 0) {
                totalQuantite += listIn.getQuantiteTotale();
                totalValeur += listIn.getQuantiteTotale() * listIn.getPrixMoyen();
            }
        }

        if (quantiteDemandee > totalQuantite + 0.001) {
            throw new IllegalStateException(
                "Stock insuffisant. Disponible: " + String.format("%.2f", totalQuantite) +
                ", Demandé: " + String.format("%.2f", quantiteDemandee));
        }

        double cump = (totalQuantite > 0) ? totalValeur / totalQuantite : 0;

        // CUMP produces a single Out line with the weighted average price
        // We still need to consume from ListIn entries to reduce stock
        List<Out> sorties = new ArrayList<>();
        double reste = quantiteDemandee;

        for (ListIn listIn : stockDisponible) {
            if (reste <= 0) break;
            double disponible = listIn.getQuantiteTotale();
            if (disponible <= 0) continue;

            double quantiteConsommee = Math.min(disponible, reste);

            Out out = new Out();
            out.setIdListInSource(listIn.getId());
            out.setQuantite(quantiteConsommee);
            out.setPrixUnitaire(cump); // Use CUMP price for all exits
            sorties.add(out);

            reste -= quantiteConsommee;
        }

        return sorties;
    }
}
