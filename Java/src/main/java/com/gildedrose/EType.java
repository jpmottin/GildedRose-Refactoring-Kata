package com.gildedrose;

public enum EType {
    AGED_BRIE("Aged Brie"),
    BACKSTAGE("Backstage passes to a TAFKAL80ETC concert"),
    SULFURAS("Sulfuras, Hand of Ragnaros"),
    CONJURED("Conjured Mana Cake"),
    UNKNOWN("Unknown");

    public final String name;

    EType(String name) {
        this.name = name;
    }

    public static EType fromString(String text) {
        for (EType b : EType.values()) {
            if (b.name.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return UNKNOWN;
    }
}
