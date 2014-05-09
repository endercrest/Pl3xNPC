package com.endercrest.pl3xnpc;

/**
 * This enum contains the different SlotTypes
 */
public enum SlotType {
    HELMET(4),
    CHESTPLATE(3),
    LEGGINGS(2),
    BOOTS(1),
    IN_HAND(0);

    private int id;

    SlotType(int id){ this.id = id; }

    /**
     * Get the id
     * @return The id
     */
    public int getId(){ return id; }
}
