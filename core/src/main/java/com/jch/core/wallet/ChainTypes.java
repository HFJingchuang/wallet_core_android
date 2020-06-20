package com.jch.core.wallet;

import androidx.annotation.NonNull;

import com.jch.core.exceptions.CoinNotFindException;

public enum ChainTypes {
    ETH("ETH", 60), MOAC("MOAC", 314), SWTC("SWTC", 315);
    private String chainName;
    private int chainType;

    ChainTypes(String chainName, int chainType) {
        this.chainName = chainName;
        this.chainType = chainType;
    }

    public int chainType() {
        return chainType;
    }

    public String chainName() {
        return chainName;
    }

    public static ChainTypes parseCoinType(int type) throws CoinNotFindException {
        for (ChainTypes e : ChainTypes.values()) {
            if (e.chainType == type) {
                return e;
            }
        }
        throw new CoinNotFindException("The currency is not supported for the time being");
    }


    @NonNull
    @Override
    public String toString() {
        return chainName;
    }
}