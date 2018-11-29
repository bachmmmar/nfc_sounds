package com.bachmann.nfcsound.infra;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DataEntryTest {

    @Rule
    public TestName test_name = new TestName();


    @Test
    public void gettingDataPathFromOneChoiceReturnsTheSame() {
        String [] images = {"img1"};
        String [] sounds = {"s1"};
        String [] voices = {"v1"};
        DataEntry e = new DataEntry("dummy", images, sounds, voices);

        DataPaths n1 = e.getNext();
        DataPaths n2 = e.getNext();

        assertEquals(n1.getImagePath(), n2.getImagePath());
        assertEquals(n1.getSoundPath(), n2.getSoundPath());
        assertEquals(n1.getVoicePath(), n2.getVoicePath());

        assertEquals(n1.getImagePath(), images[0]);
        assertEquals(n1.getSoundPath(), sounds[0]);
        assertEquals(n1.getVoicePath(), voices[0]);
    }

    public void gettingDataPathFromSeveralChoicesReturnsTheCorrect() {
        String [] images = {"img1", "img2"};
        String [] sounds = {"s1", "s2"};
        String [] voices = {"v1", "v2"};
        DataEntry e = new DataEntry("dummy", images, sounds, voices);

        DataPaths n0 = e.getNext();
        DataPaths n1 = e.getNext();

        assertEquals(n0.getImagePath(), images[0]);
        assertEquals(n0.getSoundPath(), sounds[0]);
        assertEquals(n0.getVoicePath(), voices[0]);

        assertEquals(n1.getImagePath(), images[1]);
        assertEquals(n1.getSoundPath(), sounds[1]);
        assertEquals(n1.getVoicePath(), voices[1]);
    }
}
