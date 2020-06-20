package com.jch.core.wallet.BIP44;

import com.jch.core.wallet.ChainTypes;

public final class Purpose {
    private final M m;
    private final int purpose;
    private final String toString;

    Purpose(final M m, final int purpose) {
        this.m = m;
        if (purpose == 0 || Index.isHardened(purpose))
            throw new IllegalArgumentException();
        this.purpose = purpose;
        toString = String.format("%s/%d'", m, purpose);
    }

    public int getValue() {
        return purpose;
    }

    @Override
    public String toString() {
        return toString;
    }

    /**
     * Create a {@link ChainType} for this purpose.
     *
     * @param chainTypes The coin type
     * @return A coin type instance for this purpose
     */
    public ChainType chainType(final ChainTypes chainTypes) {
        return new ChainType(this, chainTypes);
    }
}
