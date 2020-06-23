package com.jch.core;

import com.jch.core.cypto.CipherException;
import com.jch.core.cypto.ECKeyPair;
import com.jch.core.cypto.MneKeyPair;
import com.jch.core.cypto.wordlists.Chinese_simplified;
import com.jch.core.cypto.wordlists.WordCount;
import com.jch.core.swtc.EDKeyPair;
import com.jch.core.swtc.K256KeyPair;
import com.jch.core.wallet.BIP44.AddressIndex;
import com.jch.core.wallet.BIP44.BIP44;
import com.jch.core.wallet.BIP44.Bip44WalletGenerator;
import com.jch.core.wallet.Bip39Wallet;
import com.jch.core.wallet.ChainTypes;
import com.jch.core.wallet.Wallet;
import com.jch.core.wallet.WalletFile;

import org.junit.Assert;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MnemonicTest {
    final static String PASSWORD = "pwd123456";

    @Test
    public void mnemonic() throws CipherException {
        String mne = "津 统 迹 向 震 接 艺 涂 盗 催 机 够";
        MneKeyPair mneKeyPair = new MneKeyPair(mne);
        WalletFile walletFile = Wallet.createLight(ChainTypes.MNEMNOIC, "pwd123456", mneKeyPair);
        System.out.println(walletFile.toString());
        MneKeyPair mneKeyPair1 = (MneKeyPair) Wallet.decrypt(ChainTypes.MNEMNOIC, "pwd123456", false, walletFile);

        Assert.assertEquals(mneKeyPair.getSecret(), mneKeyPair1.getSecret());
    }

    @Test
    public void create_eth_wallet_BIP39() throws CipherException {
        Bip39Wallet wallet = new Bip44WalletGenerator(Chinese_simplified.INSTANCE, WordCount.TWELVE).generateWallet(ChainTypes.ETH, PASSWORD);
        String mnemonic = wallet.getMnemonic();
        System.out.println("mnemonic: " + mnemonic);
        WalletFile walletFile = wallet.getWalletFile();
        ECKeyPair keyPair = (ECKeyPair) Wallet.decrypt(ChainTypes.ETH, PASSWORD, false, walletFile);
        System.out.println(keyPair.getAddress());
        System.out.println(keyPair.getSecret());

        Bip39Wallet wallet1 = Bip44WalletGenerator.fromMnemonic(ChainTypes.ETH, mnemonic, PASSWORD, false);
        String mnemonic1 = wallet1.getMnemonic();
        Assert.assertEquals(mnemonic, mnemonic1);
        WalletFile walletFile1 = wallet.getWalletFile();
        ECKeyPair keyPair1 = (ECKeyPair) Wallet.decrypt(ChainTypes.ETH, PASSWORD, false, walletFile1);
        Assert.assertEquals(keyPair.getAddress(), keyPair1.getAddress());
        Assert.assertEquals(keyPair.getSecret(), keyPair1.getSecret());
        Assert.assertEquals(keyPair.getPublicKey(), keyPair1.getPublicKey());
        Assert.assertEquals(keyPair.getPrivateKey(), keyPair1.getPrivateKey());
    }

    @Test
    public void create_eth_wallet_BIP44() throws CipherException {
        AddressIndex addressIndex = BIP44.m()
                .purpose44()
                .chainType(ChainTypes.ETH)
                .account(0)
                .external()
                .address(0);

        Bip39Wallet wallet = new Bip44WalletGenerator(Chinese_simplified.INSTANCE, WordCount.TWELVE).generateBip44Wallet(ChainTypes.ETH, PASSWORD, addressIndex);
        String mnemonic = wallet.getMnemonic();
        System.out.println("mnemonic: " + mnemonic);
        WalletFile walletFile = wallet.getWalletFile();
        ECKeyPair keyPair = (ECKeyPair) Wallet.decrypt(ChainTypes.MOAC, PASSWORD, false, walletFile);
        System.out.println(keyPair.getAddress());
        System.out.println(keyPair.getSecret());

        Bip39Wallet wallet1 = Bip44WalletGenerator.fromMnemonicWithPath(ChainTypes.ETH, mnemonic, PASSWORD, addressIndex, false);
        String mnemonic1 = wallet1.getMnemonic();
        Assert.assertEquals(mnemonic, mnemonic1);
        WalletFile walletFile1 = wallet.getWalletFile();
        ECKeyPair keyPair1 = (ECKeyPair) Wallet.decrypt(ChainTypes.MOAC, PASSWORD, false, walletFile1);
        Assert.assertEquals(keyPair.getAddress(), keyPair1.getAddress());
        Assert.assertEquals(keyPair.getSecret(), keyPair1.getSecret());
        Assert.assertEquals(keyPair.getPublicKey(), keyPair1.getPublicKey());
        Assert.assertEquals(keyPair.getPrivateKey(), keyPair1.getPrivateKey());
    }

    @Test
    public void create_moac_wallet_BIP39() throws CipherException {
        Bip39Wallet wallet = new Bip44WalletGenerator(Chinese_simplified.INSTANCE, WordCount.TWELVE).generateWallet(ChainTypes.MOAC, PASSWORD);
        String mnemonic = wallet.getMnemonic();
        System.out.println("mnemonic: " + mnemonic);
        WalletFile walletFile = wallet.getWalletFile();
        ECKeyPair keyPair = (ECKeyPair) Wallet.decrypt(ChainTypes.MOAC, PASSWORD, false, walletFile);
        System.out.println(keyPair.getAddress());
        System.out.println(keyPair.getSecret());

        Bip39Wallet wallet1 = Bip44WalletGenerator.fromMnemonic(ChainTypes.ETH, mnemonic, PASSWORD, false);
        String mnemonic1 = wallet1.getMnemonic();
        Assert.assertEquals(mnemonic, mnemonic1);
        WalletFile walletFile1 = wallet.getWalletFile();
        ECKeyPair keyPair1 = (ECKeyPair) Wallet.decrypt(ChainTypes.MOAC, PASSWORD, false, walletFile1);
        Assert.assertEquals(keyPair.getAddress(), keyPair1.getAddress());
        Assert.assertEquals(keyPair.getSecret(), keyPair1.getSecret());
        Assert.assertEquals(keyPair.getPublicKey(), keyPair1.getPublicKey());
        Assert.assertEquals(keyPair.getPrivateKey(), keyPair1.getPrivateKey());
    }

    @Test
    public void create_moac_wallet_BIP44() throws CipherException {
        AddressIndex addressIndex = BIP44.m()
                .purpose44()
                .chainType(ChainTypes.MOAC)
                .account(0)
                .external()
                .address(0);

        Bip39Wallet wallet = new Bip44WalletGenerator(Chinese_simplified.INSTANCE, WordCount.TWELVE).generateBip44Wallet(ChainTypes.MOAC, PASSWORD, addressIndex);
        String mnemonic = wallet.getMnemonic();
        System.out.println("mnemonic: " + mnemonic);
        WalletFile walletFile = wallet.getWalletFile();
        ECKeyPair keyPair = (ECKeyPair) Wallet.decrypt(ChainTypes.MOAC, PASSWORD, false, walletFile);
        System.out.println(keyPair.getAddress());
        System.out.println(keyPair.getSecret());

        Bip39Wallet wallet1 = Bip44WalletGenerator.fromMnemonicWithPath(ChainTypes.ETH, mnemonic, PASSWORD, addressIndex, false);
        String mnemonic1 = wallet1.getMnemonic();
        Assert.assertEquals(mnemonic, mnemonic1);
        WalletFile walletFile1 = wallet.getWalletFile();
        ECKeyPair keyPair1 = (ECKeyPair) Wallet.decrypt(ChainTypes.MOAC, PASSWORD, false, walletFile1);
        Assert.assertEquals(keyPair.getAddress(), keyPair1.getAddress());
        Assert.assertEquals(keyPair.getSecret(), keyPair1.getSecret());
        Assert.assertEquals(keyPair.getPublicKey(), keyPair1.getPublicKey());
        Assert.assertEquals(keyPair.getPrivateKey(), keyPair1.getPrivateKey());
    }


    @Test
    public void create_swtc_wallet_BIP39() throws CipherException {
        Bip39Wallet wallet = new Bip44WalletGenerator(Chinese_simplified.INSTANCE, WordCount.TWELVE).generateWallet(ChainTypes.SWTC, PASSWORD);
        String mnemonic = wallet.getMnemonic();
        System.out.println("mnemonic: " + mnemonic);
        WalletFile walletFile = wallet.getWalletFile();
        K256KeyPair keyPair = (K256KeyPair) Wallet.decrypt(ChainTypes.SWTC, PASSWORD, false, walletFile);
        System.out.println(keyPair.getAddress());
        System.out.println(keyPair.getSecret());

        Bip39Wallet wallet1 = Bip44WalletGenerator.fromMnemonic(ChainTypes.SWTC, mnemonic, PASSWORD, false);
        String mnemonic1 = wallet1.getMnemonic();
        Assert.assertEquals(mnemonic, mnemonic1);
        WalletFile walletFile1 = wallet.getWalletFile();
        K256KeyPair keyPair1 = (K256KeyPair) Wallet.decrypt(ChainTypes.SWTC, PASSWORD, false, walletFile1);
        Assert.assertEquals(keyPair.getAddress(), keyPair1.getAddress());
        Assert.assertEquals(keyPair.getSecret(), keyPair1.getSecret());
        Assert.assertEquals(keyPair.getPublicKey(), keyPair1.getPublicKey());
        Assert.assertEquals(keyPair.getPrivateKey(), keyPair1.getPrivateKey());
    }

    @Test
    public void create_swtc_wallet_BIP44() throws CipherException {
        AddressIndex addressIndex = BIP44.m()
                .purpose44()
                .chainType(ChainTypes.MOAC)
                .account(0)
                .external()
                .address(0);

        Bip39Wallet wallet = new Bip44WalletGenerator(Chinese_simplified.INSTANCE, WordCount.TWELVE).generateBip44Wallet(ChainTypes.SWTC, PASSWORD, addressIndex);
        String mnemonic = wallet.getMnemonic();
        System.out.println("mnemonic: " + mnemonic);
        WalletFile walletFile = wallet.getWalletFile();
        K256KeyPair keyPair = (K256KeyPair) Wallet.decrypt(ChainTypes.SWTC, PASSWORD, false, walletFile);
        System.out.println(keyPair.getAddress());
        System.out.println(keyPair.getSecret());

        Bip39Wallet wallet1 = Bip44WalletGenerator.fromMnemonicWithPath(ChainTypes.ETH, mnemonic, PASSWORD, addressIndex, false);
        String mnemonic1 = wallet1.getMnemonic();
        Assert.assertEquals(mnemonic, mnemonic1);
        WalletFile walletFile1 = wallet.getWalletFile();
        K256KeyPair keyPair1 = (K256KeyPair) Wallet.decrypt(ChainTypes.SWTC, PASSWORD, false, walletFile1);
        Assert.assertEquals(keyPair.getAddress(), keyPair1.getAddress());
        Assert.assertEquals(keyPair.getSecret(), keyPair1.getSecret());
        Assert.assertEquals(keyPair.getPublicKey(), keyPair1.getPublicKey());
        Assert.assertEquals(keyPair.getPrivateKey(), keyPair1.getPrivateKey());
    }

    @Test
    public void create_swtc_wallet_BIP39_ED25519() throws CipherException {
        Bip39Wallet wallet = new Bip44WalletGenerator(Chinese_simplified.INSTANCE, WordCount.TWELVE).generateWallet(ChainTypes.SWTC, PASSWORD, true);
        String mnemonic = wallet.getMnemonic();
        System.out.println("mnemonic: " + mnemonic);
        WalletFile walletFile = wallet.getWalletFile();
        EDKeyPair keyPair = (EDKeyPair) Wallet.decrypt(ChainTypes.SWTC, PASSWORD, true, walletFile);
        System.out.println(keyPair.getAddress());
        System.out.println(keyPair.getSecret());

        Bip39Wallet wallet1 = Bip44WalletGenerator.fromMnemonic(ChainTypes.SWTC, mnemonic, PASSWORD, true);
        String mnemonic1 = wallet1.getMnemonic();
        Assert.assertEquals(mnemonic, mnemonic1);
        WalletFile walletFile1 = wallet.getWalletFile();
        EDKeyPair keyPair1 = (EDKeyPair) Wallet.decrypt(ChainTypes.SWTC, PASSWORD, true, walletFile1);
        Assert.assertEquals(keyPair.getAddress(), keyPair1.getAddress());
        Assert.assertEquals(keyPair.getSecret(), keyPair1.getSecret());
        Assert.assertEquals(keyPair.getPublicKey(), keyPair1.getPublicKey());
        Assert.assertEquals(keyPair.getPrivateKey(), keyPair1.getPrivateKey());
    }

    @Test
    public void create_swtc_wallet_BIP44_ED25519() throws CipherException {
        AddressIndex addressIndex = BIP44.m()
                .purpose44()
                .chainType(ChainTypes.MOAC)
                .account(0)
                .external()
                .address(0);

        Bip39Wallet wallet = new Bip44WalletGenerator(Chinese_simplified.INSTANCE, WordCount.TWELVE).generateBip44Wallet(ChainTypes.SWTC, PASSWORD, addressIndex, true);
        String mnemonic = wallet.getMnemonic();
        System.out.println("mnemonic: " + mnemonic);
        WalletFile walletFile = wallet.getWalletFile();
        EDKeyPair keyPair = (EDKeyPair) Wallet.decrypt(ChainTypes.SWTC, PASSWORD, true, walletFile);
        System.out.println(keyPair.getAddress());
        System.out.println(keyPair.getSecret());

        Bip39Wallet wallet1 = Bip44WalletGenerator.fromMnemonicWithPath(ChainTypes.ETH, mnemonic, PASSWORD, addressIndex, true);
        String mnemonic1 = wallet1.getMnemonic();
        Assert.assertEquals(mnemonic, mnemonic1);
        WalletFile walletFile1 = wallet.getWalletFile();
        EDKeyPair keyPair1 = (EDKeyPair) Wallet.decrypt(ChainTypes.SWTC, PASSWORD, true, walletFile1);
        Assert.assertEquals(keyPair.getAddress(), keyPair1.getAddress());
        Assert.assertEquals(keyPair.getSecret(), keyPair1.getSecret());
        Assert.assertEquals(keyPair.getPublicKey(), keyPair1.getPublicKey());
        Assert.assertEquals(keyPair.getPrivateKey(), keyPair1.getPrivateKey());
    }

}