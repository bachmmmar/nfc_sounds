package com.bachmann.nfcsound.infra;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class StickySizeLimitedListTest {

    @Rule
    public TestName test_name = new TestName();

    @Test
    public void neverContainsMoreThanMaxElements() {
        StickySizeLimitedList list = new StickySizeLimitedList(4);

        for (int i=0; i<10; i++) {
            list.add("item" + String.valueOf(i));
            assertTrue(list.size()<5);
        }
    }


    @Test
    public void stickPartCanBeAdded() {
        String[] sticky = {"element1", "element2"};

        StickySizeLimitedList list = new StickySizeLimitedList(0);
        list.setStickyPart(sticky);
        String [] output = list.getAllAsArray();

        assertEquals(sticky.length, output.length);
        assertEquals(sticky[0], output[0]);
        assertEquals(sticky[1], output[1]);
    }

    @Test
    public void stickPartGetsAllwaysReturned() {
        String[] sticky = {"element1", "element2"};

        StickySizeLimitedList list = new StickySizeLimitedList(4);
        list.setStickyPart(sticky);
        for (int i=0; i<10; i++) {
            list.add("item"+ String.valueOf(i));
        }
        String [] output = list.getAllAsArray();

        assertEquals(6, output.length);
        assertEquals(sticky[0], output[0]);
        assertEquals(sticky[1], output[1]);
    }

    @Test
    public void addingValueAllowedOnlyOnce() {
        StickySizeLimitedList list = new StickySizeLimitedList(4);
        for (int i=0; i<10; i++) {
            list.add("item");
        }
        String [] output = list.getAllAsArray();

        assertEquals(1, output.length);
    }

    @Test
    public void addingStickyValueDoesntWorkd() {
        String[] sticky = {"element1", "element2"};

        StickySizeLimitedList list = new StickySizeLimitedList(4);
        list.setStickyPart(sticky);
        list.add("element1");
        String [] output = list.getAllAsArray();

        assertEquals(2, output.length);
    }
}