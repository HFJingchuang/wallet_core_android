package com.jch.core.wallet;

/**
 * Data class encapsulating a BIP-39 compatible Block wallet.
 */
public class Bip39Wallet {

    /**
     * WalletFile.
     */
    private final WalletFile walletFile;

    /**
     * Generated BIP-39 mnemonic for the wallet.
     */
    private final String mnemonic;

    public Bip39Wallet(WalletFile walletFile, String mnemonic) {
        this.walletFile = walletFile;
        this.mnemonic = mnemonic;
    }

    public WalletFile getWalletFile() {
        return walletFile;
    }

    public String getMnemonic() {
        return mnemonic;
    }

}
