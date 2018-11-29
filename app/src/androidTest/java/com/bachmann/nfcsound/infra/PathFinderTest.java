package com.bachmann.nfcsound.infra;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class PathFinderTest {

    private static final String BASE_ASSETS_PATH = "nfc_name";

    @Rule
    public TestName test_name = new TestName();


    @Test(expected = UnknownPathException.class)
    public void emptyPathResultsInException() {
        PathFinder p = new PathFinder(InstrumentationRegistry.getTargetContext().getResources(),
                BASE_ASSETS_PATH);
        DataPaths d = p.find("");
    }

    @Test(expected = UnknownPathException.class)
    public void unknonwnPathResultsInException() {
        PathFinder p = new PathFinder(InstrumentationRegistry.getTargetContext().getResources(),
                BASE_ASSETS_PATH);
        DataPaths d = p.find("no_matches_for_me");
    }

    @Test(expected = UnknownPathException.class)
    public void getRandomItemFromEmptyListResultsInException() {
        PathFinder p = new PathFinder(InstrumentationRegistry.getTargetContext().getResources(),"");

        ArrayList<String> in = new ArrayList<String>();
        p.getRandomItem(in);
    }

    @Test
    public void getRandomItemFromListWithOneItem() {
        PathFinder p = new PathFinder(InstrumentationRegistry.getTargetContext().getResources(),"");

        ArrayList<String> in = new ArrayList<>(Arrays.asList("item"));
        assertEquals("item", p.getRandomItem(in));
    }

    @Test
    public void getRandomItemFromList() {
        PathFinder p = new PathFinder(InstrumentationRegistry.getTargetContext().getResources(),"");

        ArrayList<String> in = new ArrayList<>(Arrays.asList("my", "three", "items"));
        String out = p.getRandomItem(in);
        assertTrue(in.contains(out));
    }

    @Test
    public void findDogsPathWorks() {
        PathFinder p = new PathFinder(InstrumentationRegistry.getTargetContext().getResources(),
                BASE_ASSETS_PATH);
        DataPaths d = p.find("dog");

        assertEquals(BASE_ASSETS_PATH + "/dog/hund_1.jpg", d.getImagePath());
        assertEquals(BASE_ASSETS_PATH + "/dog/hund_1.mp3", d.getSoundPath());
    }


}
