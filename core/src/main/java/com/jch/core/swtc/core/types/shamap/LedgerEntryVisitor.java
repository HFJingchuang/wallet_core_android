package com.jch.core.swtc.core.types.shamap;

import com.jch.core.swtc.core.types.known.sle.LedgerEntry;

public interface LedgerEntryVisitor {
    public void onEntry(LedgerEntry entry);
}
