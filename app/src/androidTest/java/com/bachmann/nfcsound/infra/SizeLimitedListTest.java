package com.bachmann.nfcsound.infra;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class SizeLimitedListTest {

    @Rule
    public TestName test_name = new TestName();

    @Test
    public void neverContainsMoreThanMaxElements() {
        SizeLimitedList list = new SizeLimitedList(4);

        for (int i=0; i<10; i++) {
            list.add("item" + String.valueOf(i));
            assertTrue(list.size()<5);
        }
    }


    @Test
    public void addingValueAllowedOnlyOnce() {
        SizeLimitedList list = new SizeLimitedList(4);
        for (int i=0; i<10; i++) {
            list.add("item");
        }
        String [] output = list.getAllAsArray();

        assertEquals(1, output.length);
    }

    @Test
    public void addingValueWork() {
        SizeLimitedList list = new SizeLimitedList(4);

        list.add("element1");
        String [] output1 = list.getAllAsArray();
        assertEquals("element1", output1[0]);

        list.add("element2");
        String [] output2 = list.getAllAsArray();
        assertEquals("element2", output2[1]);
    }
}