package service;

import model.ListIn;
import model.Out;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LifoStrategy implements SortieStrategy {

    @Override
    public List<Out> calculerSorties(List<ListIn> stockDisponible, double quantiteDemandee) {
        // Sort by date DESC (newest first)
        stockDisponible.sort(Comparator.comparing(ListIn::getDateListIn).reversed());

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
            out.setPrixUnitaire(listIn.getPrixMoyen());
            sorties.add(out);

            reste -= quantiteConsommee;
        }

        if (reste > 0.001) {
            throw new IllegalStateException(
                "Stock insuffisant. Manque: " + String.format("%.2f", reste) + " unités.");
        }

        return sorties;
    }
}
