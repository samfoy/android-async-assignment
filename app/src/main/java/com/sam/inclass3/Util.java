package com.sam.inclass3;
import java.util.Random;

/**
 * Created by mshehab on 9/14/15.
 */
public class Util {
    private static final String _CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz!@#$%/.";
    private static final int SIZE = 12;

    public static String getPassword(){
        StringBuffer randStr = new StringBuffer();
        for(int i=0; i<SIZE; i++){ //outer loop
            int randomIndex = getRandomIndex();
            char ch = _CHAR.charAt(randomIndex);
            randStr.append(ch);
        }
        return  randStr.toString();
    }

    private static int getRandomIndex(){
        Random rand = new Random();
        int randomInt = 0;
        for(int i=0; i<10000; i++){
            for(int j=0; j<100;j++){
                randomInt = rand.nextInt(_CHAR.length());
            }
        }
        return randomInt;
    }
}