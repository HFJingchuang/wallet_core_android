package com.jch.core.swtc.core.types.shamap;

import com.jch.core.swtc.core.coretypes.hash.prefixes.Prefix;
import com.jch.core.swtc.core.serialized.BytesSink;

abstract public class ShaMapItem<T> {
    abstract void toBytesSink(BytesSink sink);
    public abstract ShaMapItem<T> copy();
    public abstract T value();
    public abstract Prefix hashPrefix();
}
