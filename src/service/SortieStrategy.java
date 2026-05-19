package service;

import model.ListIn;
import model.Out;

import java.util.List;

/**
 * Strategy interface for calculating stock exits.
 * Each implementation determines the order in which ListIn entries are consumed.
 */
public interface SortieStrategy {
    /**
     * Calculate the Out entries needed to fulfill the requested quantity.
     * @param stockDisponible List of available ListIn entries (with remaining quantities)
     * @param quantiteDemandee The quantity requested for exit
     * @return List of Out entries referencing the consumed ListIn entries
     */
    List<Out> calculerSorties(List<ListIn> stockDisponible, double quantiteDemandee);
}
