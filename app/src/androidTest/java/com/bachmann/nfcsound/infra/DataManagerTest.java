package com.bachmann.nfcsound.infra;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class DataManagerTest {

    private static final String BASE_ASSETS_PATH = "nfc_name";

    @Rule
    public TestName test_name = new TestName();


    @Test(expected = UnknownPathException.class)
    public void emptyPathResultsInException() {
        DataManager m = new DataManager(InstrumentationRegistry.getTargetContext(),
                BASE_ASSETS_PATH);
        DataEntry d = m.find("");
    }

    @Test(expected = UnknownPathException.class)
    public void unknownPathResultsInException() {
        DataManager m = new DataManager(InstrumentationRegistry.getTargetContext(),
                BASE_ASSETS_PATH);
        DataEntry d = m.find("no_matches_for_me");
    }


    @Test
    public void findDogsPathWorks() {
        DataManager m = new DataManager(InstrumentationRegistry.getContext(),
                BASE_ASSETS_PATH);
        DataEntry d = m.find("dog");
        DataPaths p = d.getNext();

        assertEquals(BASE_ASSETS_PATH + "/dog/hund_1.jpg", p.getImagePath());
        assertEquals(BASE_ASSETS_PATH + "/dog/hund_1.mp3", p.getSoundPath());
        assertEquals(BASE_ASSETS_PATH + "/dog/en_us/dog.mp3", p.getVoicePath());
    }

    @Test
    public void gettingLocaleWorksWhenLanguageOnly() {
        Locale l = DataManager.getLocaleFromString("de");
        assertEquals("de", l.getLanguage());
    }

    @Test
    public void gettingLocaleWorksWhenLanguageAndRegion() {
        Locale l = DataManager.getLocaleFromString("de_ch");
        assertEquals("de", l.getLanguage());
        assertEquals("CH", l.getCountry());
    }

    @Test
    public void gettingLocaleWorksWhenForUnrecognisedLanguages() {
        Locale l = DataManager.getLocaleFromString("kkkkk");
        assertEquals("kkkkk", l.getLanguage());
    }

    @Test(expected = RuntimeException.class)
    public void gettingLocaleWhenLanguageCodeIsInvalid() {
        Locale l = DataManager.getLocaleFromString("de_CH_kkk");
    }

    @Test
    public void getDogsTranslatedNames() {
        DataManager m = new DataManager(InstrumentationRegistry.getContext(),
                BASE_ASSETS_PATH);
        DataEntry d = m.find("dog");

        assertEquals("Hund_de_ch", d.getNextName());
        assertEquals("Dog_en_us", d.getNextName());
    }


}
