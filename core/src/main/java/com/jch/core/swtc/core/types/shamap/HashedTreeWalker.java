package com.jch.core.swtc.core.types.shamap;

import com.jch.core.swtc.core.coretypes.hash.Hash256;

public interface HashedTreeWalker {
    public void onLeaf(Hash256 h, ShaMapLeaf le);
    public void onInner(Hash256 h, ShaMapInner inner);
}
