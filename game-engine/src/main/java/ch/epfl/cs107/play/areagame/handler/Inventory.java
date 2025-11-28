package ch.epfl.cs107.play.areagame.handler;

import java.util.NavigableMap;
import java.util.TreeMap;


public abstract class Inventory {

    /// List of different pockets
    private final Pocket[] pockets;
    /// Inventory GUI
    private GUI gui;
    /**
     * Default Inventory Constructor
     *
     * @param pocketNames (Array of String), not null
     */
    public Inventory(String... pocketNames) {
        pockets = new Pocket[pocketNames.length];
        for (int i = 0; i < pocketNames.length; i++) {
            pockets[i] = new Pocket(pocketNames[i]);
        }
    }

    /**
     * Add if possible the given quantity of the given item into the given pocket
     * If done, notify listener of the change into the pocket
     *
     * @param item     (InventoryItem): item to add, not null
     * @param quantity (int): quantity of the item to add
     * @return (boolean): true if the given quantity of the given item has been added
     */
    public boolean addPocketItem(InventoryItem item, int quantity) {
        int pocket = item.getPocketId();
        if (pockets[pocket].addItem(item, quantity)) {
            notifyPocketUpdated(pocket);
            return true;
        }
        return false;
    }

    /**
     * Remove if possible the given quantity of the given item from the given pocket
     * If done, notify listener of the change into the pocket
     *
     * @param item     (InventoryItem): item to remove, not null
     * @param quantity (int): quantity of the item to remove
     * @return (boolean): true if the given quantity of the given item has been removed
     */
    public boolean removePocketItem(InventoryItem item, int quantity) {
        int pocket = item.getPocketId();
        if (pockets[pocket].removeItem(item, quantity)) {
            notifyPocketUpdated(pocket);
            return true;
        }
        return false;
    }

    /**
     * Add a listener to this inventory
     *
     * @param gui (GUI): The listener to add
     */
    public void setGui(GUI gui) {
        this.gui = gui;
        // Init the gui by indicating all pocket updated
        for (int id = 0; id < pockets.length; id++) {
            notifyPocketUpdated(id);
        }
    }

    /**
     * Notify the listener about the given pocket update
     *
     * @param id (int): Pocket id
     */
    protected void notifyPocketUpdated(int id) {
        if (gui != null)
            gui.pocketUpdated(id, pockets[id].name, new TreeMap<>(pockets[id].items));
    }

    /**
     * Boolean accessor to this inventory which indicate if one pocket possess the given object
     *
     * @param item (InventoryItem): the given object to check if contained, may be null
     * @return (boolean): True if one pocket possess the given object
     */
    public boolean contains(InventoryItem item) {
        for (Pocket pocket : pockets) {
            if (pocket.items.containsKey(item)) return true;
        }
        return false;
    }


    /**
     * Can be implemented by all Inventory holder. Allow others to request if the holder possess items
     */
    public interface Holder {
        /**
         * Boolean accessor to the Holder's inventory which indicate if it possess the given object
         *
         * @param item (InventoryItem): the given object to check, may be null
         * @return (boolean): True if the holder possess the given object
         */
        boolean possess(InventoryItem item);
    }

    public interface GUI {

        /**
         * Indicate Listeners a pocket has been updated
         *
         * @param id    (int): id of the updated pocket
         * @param name  (String): name of the updated pocket, not null
         * @param items (items): copy of the items contained in the pocket, not null
         */
        void pocketUpdated(int id, String name, NavigableMap<InventoryItem, Integer> items);
    }

    private class Pocket {

        private final String name;
        private final NavigableMap<InventoryItem, Integer> items;

        Pocket(String name) {
            this.name = name;
            this.items = new TreeMap<>();
        }

        boolean addItem(InventoryItem item, int quantity) {
            // Get the current quantity of this specific item into the pocket (may be null)
            Integer currentQuantity = items.get(item);

            // If the current quantity does not exist simply add the item
            // Otherwise simply update the quantity
            if (currentQuantity == null) {
                items.put(item, quantity);
            } else {
                items.replace(item, currentQuantity, currentQuantity + quantity);
            }
            return true;
        }

        boolean removeItem(InventoryItem item, int quantity) {

            // Get the current quantity of this specific item into the pocket (may be null)
            Integer currentQuantity = items.get(item);

            // If the current quantity does not exist or if we want to remove more than available quantity we cannot remove it
            // If we remove all of specific item, remove it from the pocket
            // Otherwise update the quantity
            if (currentQuantity == null || currentQuantity < quantity) {
                return false;
            } else if (currentQuantity == quantity) {
                items.remove(item);
            } else {
                items.replace(item, currentQuantity, currentQuantity - quantity);
            }
            return true;
        }
    }
}
