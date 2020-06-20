package com.jch.core.cypto;

import com.jch.core.cypto.wordlists.WordCount;

import java.util.Random;

public class RandomSeed {
    public static byte[] random(WordCount words) {
        return random(words, SecureRandomUtils.secureRandom());
    }

    public static byte[] random(WordCount words, Random random) {
        byte[] randomSeed = new byte[words.byteLength()];
        random.nextBytes(randomSeed);
        return randomSeed;
    }
}
