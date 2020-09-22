package com.jch.core;

import com.jch.core.btc.BWallet;
import com.jch.core.cypto.CipherException;
import com.jch.core.cypto.ECKeyPair;
import com.jch.core.cypto.Keys;
import com.jch.core.swtc.EDKeyPair;
import com.jch.core.swtc.JWallet;
import com.jch.core.swtc.K256KeyPair;
import com.jch.core.wallet.ChainTypes;
import com.jch.core.wallet.Wallet;
import com.jch.core.wallet.WalletFile;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.utils.Numeric;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.List;

import static net.i2p.crypto.eddsa.Utils.bytesToHex;
import static net.i2p.crypto.eddsa.Utils.hexToBytes;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class WalletTest {
    @Test
    public void create_eth_wallet() throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        // 随机创建钱包
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        WalletFile walletFile = Wallet.createLight(ChainTypes.ETH, "q12346", ecKeyPair);
        // 解密KeyStore
        ECKeyPair ecKeyPair1 = (ECKeyPair) Wallet.decrypt(ChainTypes.ETH, "q12346", false, walletFile);
        Assert.assertEquals(ecKeyPair.getPrivateKey(), ecKeyPair1.getPrivateKey());
        Assert.assertEquals(ecKeyPair.getPublicKey(), ecKeyPair1.getPublicKey());
    }

    @Test
    public void create_eth_wallet1() throws CipherException {
        // 密钥创建钱包
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigIntNoPrefix("3dafb4ebb061ec56527beacf659b83c4c7ac794389caf8ea191f0b2870362706"));
        WalletFile walletFile = Wallet.createLight(ChainTypes.ETH, "q12346", ecKeyPair);
        Assert.assertEquals("2062154cd708d9b1d61c526628912b69d98a014c", walletFile.getAddress());

        ecKeyPair = ECKeyPair.create(Numeric.hexStringToByteArray("3dafb4ebb061ec56527beacf659b83c4c7ac794389caf8ea191f0b2870362706"));
        walletFile = Wallet.createLight(ChainTypes.ETH, "q12346", ecKeyPair);
        Assert.assertEquals("2062154cd708d9b1d61c526628912b69d98a014c", walletFile.getAddress());
    }

    @Test
    public void create_moac_wallet() throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        // 随机创建钱包
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        WalletFile walletFile = Wallet.createLight(ChainTypes.MOAC, "q12346", ecKeyPair);
        System.out.println(walletFile.getAddress());
        // 解密KeyStore
        ECKeyPair ecKeyPair1 = (ECKeyPair) Wallet.decrypt(ChainTypes.MOAC, "q12346", false, walletFile);
        Assert.assertEquals(ecKeyPair.getPrivateKey(), ecKeyPair1.getPrivateKey());
        Assert.assertEquals(ecKeyPair.getPublicKey(), ecKeyPair1.getPublicKey());
    }

    @Test
    public void create_moac_wallet1() throws CipherException {
        // 密钥创建钱包
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigIntNoPrefix("3a556aaf1a8e291d5fad2b0100a1295c7602e894b6af1cdc25849d853d2ff974"));
        WalletFile walletFile = Wallet.createLight(ChainTypes.MOAC, "q12346", ecKeyPair);
        Assert.assertEquals("481ffc8100efb193170d349144e0e4a1c28431a6", walletFile.getAddress());

        ecKeyPair = ECKeyPair.create(Numeric.hexStringToByteArray("3a556aaf1a8e291d5fad2b0100a1295c7602e894b6af1cdc25849d853d2ff974"));
        walletFile = Wallet.createLight(ChainTypes.MOAC, "q12346", ecKeyPair);
        Assert.assertEquals("481ffc8100efb193170d349144e0e4a1c28431a6", walletFile.getAddress());
    }

    @Test
    public void create_swtc_wallet() throws CipherException {
        K256KeyPair keyPair = (K256KeyPair) JWallet.generate(false);
        System.out.println(keyPair.getAddress());
        System.out.println(keyPair.getSecret());
        System.out.println(keyPair.canonicalPubHex());
        System.out.println(keyPair.canonicalPriHex());
        WalletFile walletFile = Wallet.createLight(ChainTypes.SWTC, "q12346", keyPair);
        K256KeyPair k256KeyPair = (K256KeyPair) Wallet.decrypt(ChainTypes.SWTC, "q12346", false, walletFile);
        Assert.assertEquals(keyPair.getAddress(), k256KeyPair.getAddress());
        Assert.assertEquals(keyPair.getSecret(), k256KeyPair.getSecret());

        K256KeyPair _keyPair = (K256KeyPair) JWallet.fromSecret(keyPair.getSecret(), false);
        Assert.assertEquals(keyPair.getAddress(), _keyPair.getAddress());
        Assert.assertEquals(keyPair.getSecret(), _keyPair.getSecret());
        Assert.assertEquals(keyPair.canonicalPubHex(), _keyPair.canonicalPubHex());
        Assert.assertEquals(keyPair.canonicalPriHex(), _keyPair.canonicalPriHex());
        walletFile = Wallet.createLight(ChainTypes.SWTC, "q12346", _keyPair);
        k256KeyPair = (K256KeyPair) Wallet.decrypt(ChainTypes.SWTC, "q12346", false, walletFile);
        Assert.assertEquals(_keyPair.getAddress(), k256KeyPair.getAddress());
        Assert.assertEquals(_keyPair.getSecret(), k256KeyPair.getSecret());

        _keyPair = (K256KeyPair) JWallet.fromSecret(keyPair.canonicalPriHex(), false);
        Assert.assertEquals(keyPair.getAddress(), _keyPair.getAddress());
        Assert.assertEquals(keyPair.canonicalPriHex(), _keyPair.getSecret());
        Assert.assertEquals(keyPair.canonicalPubHex(), _keyPair.canonicalPubHex());
        Assert.assertEquals(keyPair.canonicalPriHex(), _keyPair.canonicalPriHex());
        walletFile = Wallet.createLight(ChainTypes.SWTC, "q12346", _keyPair);
        k256KeyPair = (K256KeyPair) Wallet.decrypt(ChainTypes.SWTC, "q12346", false, walletFile);
        Assert.assertEquals(_keyPair.getAddress(), k256KeyPair.getAddress());
        Assert.assertEquals(_keyPair.getSecret(), k256KeyPair.getSecret());

        EDKeyPair keyPair1 = (EDKeyPair) JWallet.generate(true);
        System.out.println(keyPair1.getAddress());
        System.out.println(keyPair1.getSecret());
        System.out.println(keyPair1.canonicalPubHex());
        System.out.println(keyPair1.canonicalPriHex());
        walletFile = Wallet.createLight(ChainTypes.SWTC, "q12346", keyPair1);
        EDKeyPair edKeyPair = (EDKeyPair) Wallet.decrypt(ChainTypes.SWTC, "q12346", true, walletFile);
        Assert.assertEquals(keyPair1.getAddress(), edKeyPair.getAddress());
        Assert.assertEquals(keyPair1.getSecret(), edKeyPair.getSecret());

        EDKeyPair _keyPair1 = (EDKeyPair) JWallet.fromSecret(keyPair1.getSecret(), true);
        Assert.assertEquals(keyPair1.getAddress(), _keyPair1.getAddress());
        Assert.assertEquals(keyPair1.getSecret(), _keyPair1.getSecret());
        Assert.assertEquals(keyPair1.canonicalPubHex(), _keyPair1.canonicalPubHex());
        Assert.assertEquals(keyPair1.canonicalPriHex(), _keyPair1.canonicalPriHex());
        walletFile = Wallet.createLight(ChainTypes.SWTC, "q12346", _keyPair1);
        edKeyPair = (EDKeyPair) Wallet.decrypt(ChainTypes.SWTC, "q12346", true, walletFile);
        Assert.assertEquals(_keyPair1.getAddress(), edKeyPair.getAddress());
        Assert.assertEquals(_keyPair1.getSecret(), edKeyPair.getSecret());

        _keyPair1 = (EDKeyPair) JWallet.fromSecret(keyPair1.canonicalPriHex(), true);
        Assert.assertEquals(keyPair1.getAddress(), _keyPair1.getAddress());
        Assert.assertEquals(keyPair1.canonicalPriHex(), _keyPair1.getSecret());
        Assert.assertEquals(keyPair1.canonicalPubHex(), _keyPair1.canonicalPubHex());
        Assert.assertEquals(keyPair1.canonicalPriHex(), _keyPair1.canonicalPriHex());
        walletFile = Wallet.createLight(ChainTypes.SWTC, "q12346", _keyPair1);
        edKeyPair = (EDKeyPair) Wallet.decrypt(ChainTypes.SWTC, "q12346", true, walletFile);
        Assert.assertEquals(_keyPair1.getAddress(), edKeyPair.getAddress());
        Assert.assertEquals(_keyPair1.getSecret(), edKeyPair.getSecret());
    }

    @Test
    public void create_btc_wallet() {
        org.bitcoinj.wallet.Wallet wallet = BWallet.generate();
        System.out.println(wallet.currentReceiveAddress());
        System.out.println(wallet.getActiveKeyChain().getMnemonicCode());
        System.out.println(wallet.currentReceiveKey().getPrivateKeyAsHex());
        System.out.println(wallet.currentReceiveKey().getPrivKey());
        System.out.println(wallet.currentReceiveKey().getPrivateKeyAsWiF(MainNetParams.get()));

        ECKey ecKey = BWallet.fromPrivateKey(wallet.currentReceiveKey().getPrivateKeyAsWiF(MainNetParams.get()));
        System.out.println("普通地址: " + LegacyAddress.fromKey(MainNetParams.get(), ecKey));//
        System.out.println("隔离见证（原生）: " + BWallet.createSegwitAddress(ecKey));//
        System.out.println("隔离见证（兼容）: " + BWallet.createP2SH(ecKey));// 隔离见证（兼容）

        org.bitcoinj.wallet.Wallet wallet1 = BWallet.fromMen(wallet.getActiveKeyChain().getMnemonicCode());
        System.out.println(wallet1.currentReceiveAddress());

    }


    final static String eth_keyStore = "{\"chainType\":\"ETH\"," +
            "\"address\":\"2062154cd708d9b1d61c526628912b69d98a014c\"," +
            "\"dateTime\":\"2020-06-18 15-40-02\"," +
            "\"id\":\"abd019fe-7a1a-42c9-afda-78dbb922c7a5\"," +
            "\"version\":3," +
            "\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"faf2540da8f96649d0d735c104e4ba0168792f757f66155b350691c92a26652c\",\"cipherparams\":{\"iv\":\"d059832a8e04848c76d4631713c8cf69\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":4096,\"p\":6,\"r\":8,\"salt\":\"9aecf692a790efd9be47c21e11ba89260d6be25adccd429fdfbe16386d73d754\"},\"mac\":\"af82c7bd82d2bcf8cf3317bb577d9ee3942213d9799a11b214a69c7a3fd6d6a0\"}}";
}