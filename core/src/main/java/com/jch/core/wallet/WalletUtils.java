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
package com.jch.core.wallet;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jch.core.cypto.CipherException;
import com.jch.core.cypto.ECKeyPair;
import com.jch.core.cypto.IKeyPair;
import com.jch.core.cypto.Keys;
import com.jch.core.cypto.MnemonicUtils;
import com.jch.core.cypto.SecureRandomUtils;
import com.jch.core.cypto.wordlists.WordCount;
import com.jch.core.cypto.wordlists.WordList;
import com.jch.core.swtc.JWallet;
import com.jch.core.swtc.Seed;
import com.jch.core.wallet.BIP44.AddressIndex;
import com.jch.core.wallet.BIP44.Bip44WalletGenerator;

import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import static com.jch.core.cypto.Keys.ADDRESS_LENGTH_IN_HEX;
import static com.jch.core.cypto.Keys.PRIVATE_KEY_LENGTH_IN_HEX;
import static org.web3j.crypto.Hash.sha256;

/**
 * Utility functions for working with Wallet files.
 */
public class WalletUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static WalletFile generateFullNewWalletFile(ChainTypes chainTypes, boolean isED25519, String password)
            throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException, CipherException {

        return generateNewWalletFile(chainTypes, isED25519, password, true);
    }

    public static WalletFile generateLightNewWalletFile(ChainTypes chainTypes, boolean isED25519, String password)
            throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException, CipherException {

        return generateNewWalletFile(chainTypes, isED25519, password, false);
    }

    public static WalletFile generateNewWalletFile(ChainTypes chainTypes, boolean isED25519, String password)
            throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchProviderException {
        return generateFullNewWalletFile(chainTypes, isED25519, password);
    }

    public static WalletFile generateNewWalletFile(
            ChainTypes chainTypes, boolean isED25519, String password, boolean useFullScrypt)
            throws CipherException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchProviderException {
        IKeyPair keyPair = null;
        switch (chainTypes) {
            case ETH:
            case MOAC:
                keyPair = Keys.createEcKeyPair();
                break;
            case SWTC:
                keyPair = JWallet.generate(isED25519);
                break;
        }
        return generateWalletFile(chainTypes, password, keyPair, useFullScrypt);
    }

    public static WalletFile generateWalletFile(ChainTypes chainTypes,
                                                String password, IKeyPair keyPair, boolean useFullScrypt)
            throws CipherException {

        WalletFile walletFile;
        if (useFullScrypt) {
            walletFile = Wallet.createStandard(chainTypes, password, keyPair);
        } else {
            walletFile = Wallet.createLight(chainTypes, password, keyPair);
        }

        return walletFile;
    }

    /**
     * Generates a BIP-39 compatible Ethereum wallet. The private key for the wallet can be
     * calculated using following algorithm:
     *
     * <pre>
     *     Key = SHA-256(BIP_39_SEED(mnemonic, password))
     * </pre>
     *
     * @param password Will be used for both wallet encryption and passphrase for BIP-39 seed
     * @return A BIP-39 compatible Ethereum wallet
     * @throws CipherException if the underlying cipher is not available
     * @throws IOException     if the destination cannot be written to
     */
    public static Bip39Wallet generateBip39Wallet(ChainTypes chainTypes, WordList wordList, WordCount wordCount, String password, boolean isED25519)
            throws CipherException {
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);

        return new Bip44WalletGenerator(wordList, wordCount).generateWallet(chainTypes, password, isED25519);
    }

    public static Bip39Wallet generateBip44Wallet(ChainTypes chainTypes, WordList wordList, WordCount wordCount, String password, AddressIndex addressIndex, boolean isED25519)
            throws CipherException {
        return new Bip44WalletGenerator(wordList, wordCount).generateBip44Wallet(chainTypes, password, addressIndex, isED25519);
    }

    /**
     * Generates a BIP-39 compatible Block wallet using a mnemonic passed as argument.
     *
     * @param password  Will be used for both wallet encryption and passphrase for BIP-39 seed
     * @param isED25519 only used for SWTC.
     * @param mnemonic  The mnemonic that will be used to generate the seed
     * @return A BIP-39 compatible Block wallet
     * @throws CipherException if the underlying cipher is not available
     * @throws IOException     if the destination cannot be written to
     */
    public static WalletFile generateBip39WalletFromMnemonic(
            ChainTypes chainTypes, String password, boolean isED25519, String mnemonic)
            throws CipherException {
        byte[] seedByte = MnemonicUtils.generateSeed(mnemonic, password);
        IKeyPair keyPair = null;
        switch (chainTypes) {
            case ETH:
            case MOAC:
                keyPair = ECKeyPair.create(sha256(seedByte));
                break;
            case SWTC:
                Seed seed = new Seed();
                if (isED25519) {
                    seed.setEd25519();
                }
                String secret = seed.encodeSeed(Arrays.copyOf(seedByte, 16));
                keyPair = Seed.fromBase58(secret).keyPair();
                break;
        }

        return generateWalletFile(chainTypes, password, keyPair, false);
    }

    public static WalletFile generateWalletFromPrivateKey(
            ChainTypes chainTypes, String privateKey, String password, boolean isED25519)
            throws CipherException {
        IKeyPair keyPair = null;
        switch (chainTypes) {
            case ETH:
            case MOAC:
                keyPair = ECKeyPair.create(Numeric.hexStringToByteArray(privateKey));
                break;
            case SWTC:
                keyPair = JWallet.fromSecret(password, isED25519);
                break;
        }

        return generateWalletFile(chainTypes, password, keyPair, false);
    }

    public static IKeyPair loadCredentials(ChainTypes chainTypes, String password, boolean isED25519, WalletFile walletFile)
            throws CipherException {
        return Wallet.decrypt(chainTypes, password, isED25519, walletFile);
    }

    private static String getWalletFileName(WalletFile walletFile) {
        return timestamp(new Date()) + walletFile.getAddress() + ".json";
    }

    /**
     * Formats a timestamp string from the given date.
     *
     * @param date a date to be formatted
     * @return a timestamp string for the given date
     */
    static String timestamp(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("'UTC--'yyyy-MM-dd'T'HH-mm-ss.S'Z--'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    public static String getDefaultKeyDirectory() {
        return getDefaultKeyDirectory(System.getProperty("os.name"));
    }

    static String getDefaultKeyDirectory(String osName1) {
        String osName = osName1.toLowerCase();

        if (osName.startsWith("mac")) {
            return String.format(
                    "%s%sLibrary%sEthereum",
                    System.getProperty("user.home"), File.separator, File.separator);
        } else if (osName.startsWith("win")) {
            return String.format("%s%sEthereum", System.getenv("APPDATA"), File.separator);
        } else {
            return String.format("%s%s.ethereum", System.getProperty("user.home"), File.separator);
        }
    }

    public static String getTestnetKeyDirectory() {
        return String.format(
                "%s%stestnet%skeystore", getDefaultKeyDirectory(), File.separator, File.separator);
    }

    public static String getMainnetKeyDirectory() {
        return String.format("%s%skeystore", getDefaultKeyDirectory(), File.separator);
    }

    /**
     * Get keystore destination directory for a Rinkeby network.
     *
     * @return a String containing destination directory
     */
    public static String getRinkebyKeyDirectory() {
        return String.format(
                "%s%srinkeby%skeystore", getDefaultKeyDirectory(), File.separator, File.separator);
    }

    public static boolean isValidPrivateKey(String privateKey) {
        String cleanPrivateKey = Numeric.cleanHexPrefix(privateKey);
        return cleanPrivateKey.length() == PRIVATE_KEY_LENGTH_IN_HEX;
    }

    public static boolean isValidAddress(String input) {
        return isValidAddress(input, ADDRESS_LENGTH_IN_HEX);
    }

    public static boolean isValidAddress(String input, int addressLength) {
        String cleanInput = Numeric.cleanHexPrefix(input);

        try {
            Numeric.toBigIntNoPrefix(cleanInput);
        } catch (NumberFormatException e) {
            return false;
        }

        return cleanInput.length() == addressLength;
    }
}
