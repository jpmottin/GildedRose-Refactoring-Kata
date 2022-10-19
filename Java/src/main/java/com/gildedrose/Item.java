package com.gildedrose;

public class Item implements Cloneable {

    public String name;

    public int sellIn;

    public int quality;

    public Item(String name, int sellIn, int quality) {
        this.name = name;
        this.sellIn = sellIn;
        this.quality = quality;
    }

    public EType getType() {
        return EType.fromString(name);
    }

    @Override
    protected Item clone() {
        return new Item(name,sellIn,quality);
    }

    @Override
    public String toString() {
        return this.name + ", " + this.sellIn + ", " + this.quality;
    }
}
