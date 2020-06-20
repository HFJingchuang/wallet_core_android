package com.jch.core.swtc;

import com.jch.core.cypto.IKeyPair;
import com.jch.core.exceptions.PrivateKeyFormatException;
import com.jch.core.swtc.base58.B58IdentiferCodecs;

import static com.jch.core.swtc.Config.getB58IdentiferCodecs;


public class JWallet {

    /**
     * 随机生成钱包地址
     *
     * @return
     */
    public static IKeyPair generate(boolean isED25519) {
        Seed seed = new Seed();
        if (isED25519) {
            seed.setEd25519();
        }
        String secret = seed.random();
        return fromSecret(secret, isED25519);
    }

    /**
     * 根据密钥生成钱包
     *
     * @param secret
     * @param isED25519
     * @return
     */
    public static IKeyPair fromSecret(String secret, boolean isED25519) {
        try {
            return Seed.fromBase58(secret).keyPair();
        } catch (Exception e) {
            Seed seed = new Seed();
            if (secret.length() == 64) {
                if (isED25519) {
                    seed.setEd25519();
                }
            } else if (secret.length() == 66) {
                if (secret.toUpperCase().startsWith("ED")) {
                    seed.setEd25519();
                }
                secret = secret.substring(2);
            } else {
                throw new PrivateKeyFormatException("deriving keypair requires correct prefixed private key");
            }
            return seed.fromPrivateKey(secret);
        }
    }

//    /**
//     * 根据助记词生成钱包
//     *
//     * @param mnemonics
//     * @param isED25519
//     * @return
//     */
//    public static Wallet fromMnemonics(String mnemonics, boolean isED25519) {
//        return Bip44WalletGenerator.fromMnemonic(mnemonics, null, isED25519);
//    }
//
//    /**
//     * 根据助记词和路径生成钱包
//     *
//     * @param mnemonics
//     * @param addressIndex
//     * @param isED25519
//     * @return
//     */
//    public static Wallet fromMnemonicWithPath(String mnemonics, AddressIndex addressIndex, boolean isED25519) {
//        return Bip44WalletGenerator.fromMnemonicWithPath(mnemonics, null, addressIndex, isED25519);
//    }
//
//    public String getPublicKey() {
//        if (this.keypairs == null) {
//            return null;
//        }
//        return this.keypairs.canonicalPubHex();
//    }

//    /**
//     * 使用钱包密钥对信息进行签名
//     *
//     * @param message
//     * @return
//     */
//    public String sign(String message) {
//        byte[] der = this.keypairs.signMessage(message.getBytes());
//        return Numeric.toHexStringNoPrefix(der);
//    }
//
//    /**
//     * 校验信息的自作签名是否正确
//     *
//     * @param message
//     * @param signature
//     * @return
//     */
//    public boolean verify(String message, String signature) {
//        // (byte[] hash, byte[] sigBytes
//        return this.keypairs.verifySignature(message.getBytes(), signature.getBytes());
//    }

//    /**
//     * 获取公钥地址
//     *
//     * @return
//     */
//    public String getAddress() {
//        byte[] bytes = this.keypairs.pub160Hash();
//        return encodeAddress(bytes);
//    }

    /**
     * 工具方法：公钥编码
     *
     * @return
     */
    private static String encodeAddress(byte[] a) {
        return getB58IdentiferCodecs().encodeAddress(a);
    }

    public static boolean isValidAddress(String address) {
        try {
            getB58IdentiferCodecs().decode(address, B58IdentiferCodecs.VER_ACCOUNT_ID);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidSecret(String secret, boolean isED25519) {
        try {
            fromSecret(secret, isED25519);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//    public String getSecret() {
//        if (this.secret == null || this.secret.isEmpty()) {
//            return this.keypairs.canonicalPriHex();
//        } else {
//            return this.secret;
//        }
//    }

//    public IKeyPair getKeypairs() {
//        return keypairs;
//    }
//
//    public void setKeypairs(IKeyPair keypairs) {
//        this.keypairs = keypairs;
//    }
//
//    public void setSecret(String secret) {
//        this.secret = secret;
//    }
//
//    public String getMnemonics() {
//        return this.mnemonics;
//    }
//
//    public void setMnemonics(String mnemonics) {
//        this.mnemonics = mnemonics;
//    }
}