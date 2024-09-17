package com.folautech.batch;

import org.junit.jupiter.api.Test;

import java.util.Random;

public class UtilityTests {

    @Test
    void testBoolean(){
        Random random = new Random();

        int count = 0;

        while (count < 10){
            boolean bool = random.nextBoolean();

            System.out.println("Random boolean: " + bool);

            count++;
        }
    }
}
