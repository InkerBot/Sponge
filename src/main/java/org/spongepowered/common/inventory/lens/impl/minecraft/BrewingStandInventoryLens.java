/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.inventory.lens.impl.minecraft;

import static org.spongepowered.api.item.ItemTypes.BLAZE_POWDER;

import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.slot.OutputSlot;
import org.spongepowered.common.inventory.adapter.InventoryAdapter;
import org.spongepowered.common.inventory.lens.impl.DefaultIndexedLens;
import org.spongepowered.common.inventory.lens.impl.RealLens;
import org.spongepowered.common.inventory.lens.impl.slot.FuelSlotLens;
import org.spongepowered.common.inventory.lens.impl.slot.InputSlotLens;
import org.spongepowered.common.inventory.lens.impl.slot.SlotLensProvider;

public class BrewingStandInventoryLens extends RealLens {

    private DefaultIndexedLens potions;
    private InputSlotLens ingredient;
    private InputSlotLens fuel;

    @SuppressWarnings("unchecked")
    public BrewingStandInventoryLens(final InventoryAdapter adapter, final SlotLensProvider slots) {
        super(0, adapter.bridge$getFabric().fabric$getSize(), (Class<? extends Inventory>) adapter.getClass());
        this.init(slots);
    }

    @SuppressWarnings("unchecked")
    public BrewingStandInventoryLens(final int base, final InventoryAdapter adapter, final SlotLensProvider slots) {
        super(base, adapter.bridge$getFabric().fabric$getSize(), (Class<? extends Inventory>) adapter.getClass());
        this.init(slots);
    }

    protected void init(final SlotLensProvider slots) {

        this.potions = new DefaultIndexedLens(0, 3, 1, OutputSlot.class, slots);
        this.ingredient = new InputSlotLens(3, (i) -> true, (i) -> true); // TODO filter PotionIngredients
        this.fuel = new FuelSlotLens(4, (i) -> BLAZE_POWDER.equals(i.getType()), BLAZE_POWDER::equals);

        this.addSpanningChild(this.potions);
        this.addSpanningChild(this.ingredient);
        this.addSpanningChild(this.fuel);
    }
}