package com.unregistered;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Play audio sample at random in the background.
 */
public class RandomAudioPlayer {
    private Random rand = new Random();

    public RandomAudioPlayer() {
        // Do this to initialize the toolkit
        JFXPanel p = new JFXPanel();
        System.out.println(p);
    }

    public void playRandomSound() throws Exception {
        URL soundFile = getRandomSound();

        Media media = new Media(soundFile.toString());
        MediaPlayer mp = new MediaPlayer(media);

        mp.play();
    }

    private URL getRandomSound() throws Exception {
        List<String> sounds = getSoundList();

        int idx = rand.nextInt(sounds.size());
        String filename = sounds.get(idx);

        System.out.println(filename);
        return getClass().getResource("/sounds/" + filename);
    }

    private List<String> getSoundList() {
        InputStream index = getClass().getResourceAsStream("/sounds/index");

        try {
            String content = IOUtils.toString(index);
            String[] parts = content.split("\n");
            return Arrays.asList(parts);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}
