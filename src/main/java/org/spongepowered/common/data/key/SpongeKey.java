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
package org.spongepowered.common.data.key;

import com.google.common.base.MoreObjects;
import com.google.common.reflect.TypeToken;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.data.ChangeDataHolderEvent;

import java.util.Comparator;
import java.util.function.BiPredicate;

public class SpongeKey<V extends Value<E>, E> implements Key<V> {

    private final CatalogKey key;
    private final TypeToken<V> valueToken;
    private final TypeToken<E> elementToken;
    private final Comparator<? super E> elementComparator;
    private final BiPredicate<? super E, ? super E> elementIncludesTester;

    SpongeKey(CatalogKey key, TypeToken<V> valueToken, TypeToken<E> elementToken, Comparator<? super E> elementComparator,
            BiPredicate<? super E, ? super E> elementIncludesTester) {
        this.key = key;
        this.valueToken = valueToken;
        this.elementToken = elementToken;
        this.elementComparator = elementComparator;
        this.elementIncludesTester = elementIncludesTester;
    }

    @Override
    public CatalogKey getKey() {
        return this.key;
    }

    @Override
    public TypeToken<V> getValueToken() {
        return this.valueToken;
    }

    @Override
    public TypeToken<E> getElementToken() {
        return this.elementToken;
    }

    @Override
    public Comparator<? super E> getElementComparator() {
        return this.elementComparator;
    }

    @Override
    public BiPredicate<? super E, ? super E> getElementIncludesTester() {
        return this.elementIncludesTester;
    }

    @Override
    public <H extends DataHolder> void registerEvent(Class<H> holderFilter, EventListener<ChangeDataHolderEvent.ValueChange> listener) {
        // TODO
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("key", this.key)
                .add("valueToken", this.valueToken);
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }
}
