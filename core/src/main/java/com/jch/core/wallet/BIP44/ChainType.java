package com.jch.core.wallet.BIP44;


import com.jch.core.wallet.ChainTypes;

/**
 * @author QuincySx
 * @date 2018/3/5 下午4:26
 */
public class ChainType {
    private final Purpose purpose;
    private final ChainTypes chainTypes;
    private final String string;

    ChainType(final Purpose purpose, final ChainTypes chainTypes) {
        this.purpose = purpose;
        this.chainTypes = chainTypes;
        string = String.format("%s/%d'", purpose, chainTypes.chainType());
    }

    public ChainTypes getValue() {
        return chainTypes;
    }

    public Purpose getParent() {
        return purpose;
    }

    @Override
    public String toString() {
        return string;
    }

    /**
     * Create a {@link Account} for this purpose and coin type.
     *
     * @param account The account number
     * @return An {@link Account} instance for this purpose and coin type
     */
    public Account account(final int account) {
        return new Account(this, account);
    }
}
