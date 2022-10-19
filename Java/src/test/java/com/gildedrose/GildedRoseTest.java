package com.gildedrose;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GildedRoseTest {
    private static final int MAX_DAYS = 30;
    public static final int MAX_QUALITY = 50;
    public static final int MIN_QUALITY = 0;

    @Test
    void updateQuality_nullItems_throwException() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            Item[] input = new Item[] {
                new Item("foo", 0, 0),
                null
            };

            executeAndVerifyForXDaysUntilMaxDays(input,0);
        });

        assertEquals(GildedRose.NPE_EXCEPTION_MESSAGE, thrown.getMessage());
    }@Test
    void updateQuality_ordinaryItems_SellInAndQualityDecrease() {
        Item[] input = new Item[] {
            new Item("foo", 0, 0),
            new Item("+5 Dexterity Vest", 10, 20),
            new Item("Elixir of the Mongoose", 5, 7)
        };

        executeAndVerifyForXDaysUntilMaxDays(input,0);
    }

    @Test
    void updateQuality_specialItems_SellInAndQualityDecreaseWithSpecificRule() {
        Item[] input = new Item[] {
            new Item("Aged Brie", 2, 0),
            new Item("Sulfuras, Hand of Ragnaros", 0, 80),
            new Item("Sulfuras, Hand of Ragnaros", -1, 80),
            new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20),
            new Item("Backstage passes to a TAFKAL80ETC concert", 10, 49),
            new Item("Backstage passes to a TAFKAL80ETC concert", 5, 49)
        };

        executeAndVerifyForXDaysUntilMaxDays(input,0);
    }

    @Test
    void updateQuality_specialNewItemsConjured_SellInAndQualityDecreaseWithSpecificRule() {
        Item[] input = new Item[] {
            new Item("Conjured Mana Cake", 3, 6)
        };

        executeAndVerifyForXDaysUntilMaxDays(input,0);
    }

    private void executeAndVerifyForXDaysUntilMaxDays(Item[] itemsIn, int daysSpent) {
        // Execute
        Item[] itemsOut = instanceGildedRoseAppAndUpdateQualityAndGetResults(itemsIn);

        // Check
        businessChecks(itemsIn, itemsOut, daysSpent);

        // And repeat
        if(daysSpent < MAX_DAYS) executeAndVerifyForXDaysUntilMaxDays(itemsOut, ++daysSpent);
    }

    private void businessChecks(Item[] itemsIn, Item[] itemsOut, int daysSpent) {
        for (int i = 0; i < itemsIn.length; i++) {
            System.out.println("Testing original ["+ daysSpent +"]["+i+"]: " + itemsIn[i] + "    VS      new: " + itemsOut[i] + "\n");
            assertEquals(itemsIn[i].name, itemsOut[i].name);
            checkQuality(itemsIn[i], itemsOut[i]);
            checkSellIn(itemsIn[i], itemsOut[i]);
        }
    }

    private void checkQuality(Item previousItem, Item newItemToVerify) {
        assertTrue(isValidQualityRegardingItemType(previousItem, newItemToVerify));
    }

    private boolean isValidQualityRegardingItemType(Item previousItem, Item newItemToVerify) {
        if(previousItem.quality >= MAX_QUALITY) {
            if (EType.BACKSTAGE.equals(previousItem.getType()) && newItemToVerify.sellIn < 0)
                return 0 == newItemToVerify.quality;
            return previousItem.quality == newItemToVerify.quality;
        } else
            switch (previousItem.getType()) {
                case AGED_BRIE:
                    if(newItemToVerify.sellIn < 0)
                        return capMaxQuality(previousItem.quality, 2) == newItemToVerify.quality;
                    else
                        return capMaxQuality(previousItem.quality,1) == newItemToVerify.quality;
                case BACKSTAGE:
                    if(previousItem.sellIn > 10)
                        return previousItem.quality + 1 == newItemToVerify.quality;
                    else {
                        if(previousItem.sellIn > 5) {
                            return capMaxQuality(previousItem.quality, 2) == newItemToVerify.quality;
                        } else if(previousItem.sellIn > 0)
                            return capMaxQuality(previousItem.quality, 3) == newItemToVerify.quality;
                        else
                            return 0 == newItemToVerify.quality;
                    }
                case SULFURAS:
                    return previousItem.quality == newItemToVerify.quality;
                case CONJURED:
                    return capMinQuality(previousItem.quality, 2) == newItemToVerify.quality;
                default:
                    return isValidQualityForOrdinary(previousItem, newItemToVerify);
            }
    }

    private boolean isValidQualityForOrdinary(Item previousItem, Item newItemToVerify) {
        if (newItemToVerify.sellIn < 0) // Decrease twice faster when below 0 in sellIn
            return capMinQuality(previousItem.quality, 2) == newItemToVerify.quality;
        else // Otherwise -> -1
            return capMinQuality(previousItem.quality, 1) == newItemToVerify.quality;
    }

    private void checkSellIn(Item previousItem, Item newItemToVerify) {
        assertTrue(
            !EType.SULFURAS.equals(previousItem.getType()) ?
                previousItem.sellIn - 1 == newItemToVerify.sellIn:
                previousItem.sellIn == newItemToVerify.sellIn);
    }

    private int capMaxQuality(int currentValue, int increment) {
        if (currentValue + increment > MAX_QUALITY)
            return MAX_QUALITY;
        else
            return currentValue + increment;
    }


    private int capMinQuality(int currentValue, int decrement) {
        if (currentValue - decrement < MIN_QUALITY)
            return MIN_QUALITY;
        else
            return currentValue - decrement;
    }

    private static Item[] instanceGildedRoseAppAndUpdateQualityAndGetResults(Item[] items) {
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        return app.items;
    }
}
