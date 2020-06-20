/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.jch.core.wallet.BIP44;


import com.jch.core.cypto.Bip32ECKeyPair;
import com.jch.core.cypto.CipherException;
import com.jch.core.cypto.ECKeyPair;
import com.jch.core.cypto.IKeyPair;
import com.jch.core.cypto.MnemonicUtils;
import com.jch.core.cypto.RandomSeed;
import com.jch.core.cypto.wordlists.WordCount;
import com.jch.core.cypto.wordlists.WordList;
import com.jch.core.swtc.Seed;
import com.jch.core.utils.B16;
import com.jch.core.wallet.Bip39Wallet;
import com.jch.core.wallet.ChainTypes;
import com.jch.core.wallet.Wallet;

import java.util.Arrays;

import static org.web3j.crypto.Hash.sha256;

public class Bip44WalletGenerator {

    private static WordList wordList;
    private static WordCount wordCount;

    public Bip44WalletGenerator(final WordList wordList, final WordCount wordCount) {
        this.wordList = wordList;
        this.wordCount = wordCount;
    }

    /**
     * Generates a BIP-44 compatible SWTC wallet on top of BIP-39 generated seed.
     *
     * @param chainTypes
     * @param password     Will be used for both wallet encryption and passphrase for BIP-39 seed
     * @param addressIndex BIP-44 path
     * @param isED25519    Generates a SWTC wallet by ed25519,only used for SWTC
     * @return A BIP-39 compatible SWTC wallet
     */
    public static Bip39Wallet generateBip44Wallet(ChainTypes chainTypes,
                                                  String password, AddressIndex addressIndex, boolean isED25519) throws CipherException {
        byte[] initialEntropy = RandomSeed.random(wordCount);
        String mnemonic = new MnemonicUtils(wordList).generateMnemonic(initialEntropy);
        return fromMnemonicWithPath(chainTypes, mnemonic, password, addressIndex, isED25519);
    }

    /**
     * Generates a BIP-44 compatible SWTC wallet on top of BIP-39 generated seed.
     *
     * @param chainTypes
     * @param password     Will be used for both wallet encryption and passphrase for BIP-39 seed
     * @param addressIndex BIP-44 path
     * @return A BIP-39 compatible SWTC wallet
     */
    public static Bip39Wallet generateBip44Wallet(ChainTypes chainTypes,
                                                  String password, AddressIndex addressIndex) throws CipherException {
        byte[] initialEntropy = RandomSeed.random(wordCount);
        String mnemonic = new MnemonicUtils(wordList).generateMnemonic(initialEntropy);
        return fromMnemonicWithPath(chainTypes, mnemonic, password, addressIndex, false);
    }

    /**
     * Generates a BIP-44 compatible Block wallet on top of BIP-39 generated seed.
     *
     * @param chainTypes
     * @param password   Will be used for both wallet encryption and passphrase for BIP-39 seed
     * @return A BIP-39 compatible Block wallet
     */
    public static Bip39Wallet generateWallet(ChainTypes chainTypes,
                                             String password) throws CipherException {
        byte[] initialEntropy = RandomSeed.random(wordCount);
        String mnemonic = new MnemonicUtils(wordList).generateMnemonic(initialEntropy);

        return fromMnemonic(chainTypes, mnemonic, password, false);
    }

    /**
     * Generates a BIP-44 compatible Block wallet on top of BIP-39 generated seed.
     *
     * @param chainTypes
     * @param password   Will be used for both wallet encryption and passphrase for BIP-39 seed
     * @param isED25519  Generates a Block wallet by ed25519,only used for SWTC
     * @return A BIP-39 compatible Block wallet
     */
    public static Bip39Wallet generateWallet(ChainTypes chainTypes,
                                             String password, boolean isED25519) throws CipherException {
        byte[] initialEntropy = RandomSeed.random(wordCount);
        String mnemonic = new MnemonicUtils(wordList).generateMnemonic(initialEntropy);

        return fromMnemonic(chainTypes, mnemonic, password, isED25519);
    }

    /**
     * Generates a BIP-44 compatible SWTC wallet by mnemonic.
     *
     * @param chainTypes
     * @param mnemonic
     * @param password   Will be used for both wallet encryption and passphrase for BIP-39 seed
     * @param isED25519  Generates a SWTC wallet by ed25519
     * @return
     */
    public static Bip39Wallet fromMnemonic(ChainTypes chainTypes,
                                           String mnemonic, String password, boolean isED25519) throws CipherException {
        byte[] seedByte = MnemonicUtils.generateSeed(mnemonic, "");
        switch (chainTypes) {
            case ETH:
            case MOAC:
                ECKeyPair keyPair = ECKeyPair.create(sha256(seedByte));
                return new Bip39Wallet(Wallet.createLight(chainTypes, password, keyPair), mnemonic);
            case SWTC:
                Seed seed = new Seed();
                if (isED25519) {
                    seed.setEd25519();
                }
                String secret = seed.encodeSeed(Arrays.copyOf(seedByte, 16));
                IKeyPair keyPair1 = Seed.fromBase58(secret).keyPair();
                return new Bip39Wallet(Wallet.createLight(chainTypes, password, keyPair1), mnemonic);
        }
        return null;
    }

    /**
     * Generates a BIP-44 compatible SWTC wallet by mnemonic.
     *
     * @param mnemonic
     * @param password     Will be used for both wallet encryption and passphrase for BIP-39 seed
     * @param addressIndex BIP-44 path
     * @param isED25519    Generates a SWTC wallet by ed25519
     * @return
     */
    public static Bip39Wallet fromMnemonicWithPath(ChainTypes chainTypes,
                                                   String mnemonic, String password, AddressIndex addressIndex, boolean isED25519) throws CipherException {
        byte[] seedByte = MnemonicUtils.generateSeed(mnemonic, "");
        Bip32ECKeyPair masterKeypair = Bip32ECKeyPair.generateKeyPair(seedByte);
        Bip32ECKeyPair bip44Keypair = Bip32ECKeyPair.deriveKeyPair(masterKeypair, addressIndex);
        switch (chainTypes) {
            case ETH:
            case MOAC:
                return new Bip39Wallet(Wallet.createLight(chainTypes, password, bip44Keypair), mnemonic);
            case SWTC:
                Seed seed = new Seed();
                if (isED25519) {
                    seed.setEd25519();
                }
                IKeyPair keyPair = seed.fromPrivateKey(B16.toStringTrimmed(bip44Keypair.getPrivateKey().toByteArray()));
                return new Bip39Wallet(Wallet.createLight(chainTypes, password, keyPair), mnemonic);
        }
        return null;
    }
}
