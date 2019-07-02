package com.bachmann.nfcsound.infra;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class DataEntryIteratorTest {

    private static final String BASE_ASSETS_PATH = "nfc_name";

    @Rule
    public TestName test_name = new TestName();


    @Test(expected = IndexOutOfBoundsException.class)
    public void emptyDataManager_CausesOutOfBoundsException() {
        DataManager m = new DataManager(InstrumentationRegistry.getTargetContext(),
                "inexisting_path");
        DataEntryIterator dei = new DataEntryIterator(m);

        dei.getNext();
    }

    @Test
    public void getNextReturnsAnotherEntry() {
        DataManager m = new DataManager(InstrumentationRegistry.getTargetContext(),
                BASE_ASSETS_PATH);
        DataEntryIterator dei = new DataEntryIterator(m);

        DataEntry de1 = dei.getNext();
        DataEntry de2 = dei.getNext();
        assertNotEquals(de1.getName(), de2.getName());
    }


    @Test
    public void getNextRestartsWhenEndReached() {
        DataManager m = new DataManager(InstrumentationRegistry.getContext(),
                BASE_ASSETS_PATH);
        DataEntryIterator dei = new DataEntryIterator(m);

        DataEntry de1 = dei.getNext();
        DataEntry de2 = dei.getNext();
        assertEquals(de1.getName(), de2.getName());
    }
}
