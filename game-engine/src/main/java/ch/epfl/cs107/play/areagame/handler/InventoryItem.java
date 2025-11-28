package ch.epfl.cs107.play.areagame.handler;

public interface InventoryItem  {

    /** @return (int): pocket id, the item is referred to */
    int getPocketId();
    /** @return (String): name of the item, not null */
    String getName();

    /**
     * By default compare item by using there name
     * @param item (InventoryItem): other items , not null
     * @return (int): 1, 0, -1 following the comparison result
     */
     default int compareTo(InventoryItem item) {
        return getName().compareTo(item.getName());
   }
}
