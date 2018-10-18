package com.example.nfcsound.infra;

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
public class DataManagerTest {

    private static final String BASE_ASSETS_PATH = "nfc_name";

    @Rule
    public TestName test_name = new TestName();


    @Test(expected = UnknownPathException.class)
    public void emptyPathResultsInException() {
        DataManager m = new DataManager(InstrumentationRegistry.getTargetContext().getResources(),
                BASE_ASSETS_PATH);
        DataPaths d = m.find("");
    }

    @Test(expected = UnknownPathException.class)
    public void unknownPathResultsInException() {
        DataManager m = new DataManager(InstrumentationRegistry.getTargetContext().getResources(),
                BASE_ASSETS_PATH);
        DataPaths d = m.find("no_matches_for_me");
    }


    @Test
    public void findDogsPathWorks() {
        DataManager m = new DataManager(InstrumentationRegistry.getTargetContext().getResources(),
                BASE_ASSETS_PATH);
        DataPaths d = m.find("dog");

        assertEquals(BASE_ASSETS_PATH + "/dog/images.jpg", d.getImagePath());
        assertEquals(BASE_ASSETS_PATH + "/dog/hund_1.mp3", d.getSoundPath());
    }


}
