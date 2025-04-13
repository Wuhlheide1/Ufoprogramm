import sas.*;

import java.awt.Color;
import java.awt.Color.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

//import java.awt.color.*;
public class Ufoprogramm {
    View window;
    Picture background;
    Astroid astroids[] = new Astroid[3];
    FastAstroid fastAstroid;
    Ufo ufo;
    Clip backgroundMusic;
    boolean gameRunning = false;
    Text score;
    int scoreValue = 0;
    private Laser laser; // Add this field
    
        Ufoprogramm() {
            window = new View(300, 800, "Ufo");
            laser = new Laser(0, 0, 1); // Initialize laser first
            ufo = new Ufo(150, 800 - 100, 1, laser); // Pass initialized laser
            background = new Picture(0, 0, "hintergrund.png");
            ufo = new Ufo(150, 800 - 100, 1, laser);
            ufo.ufoMove(-(ufo.getWidth() / 2));
            for (int i = 0; i < astroids.length; i++) {
                astroids[i] = new Astroid(-250, -250, 1, ufo);
            }
            playSound("background.wav", true, -20f);
            score = new Text(15, 15, "Score: 0", Color.WHITE);
            score.setFontMonospaced(false, 20);
            loop();
        }

    public void increaseScore(int pScore) {
        scoreValue = scoreValue + pScore;
        score.setText("Score: " + scoreValue);
    }

    // I have not made this music or the method my self made, I just found it on the
    // internet and it works well
    private void playSound(String musicFileName, boolean loop, float volume) {
        try {
            File musicFile = new File(musicFileName);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);

            if (loop) {
                // For looping sounds (background music), use the class variable
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioStream);

                // Add volume control
                FloatControl volumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volume);

                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                // For one-time sounds, create a new Clip
                Clip soundClip = AudioSystem.getClip();
                soundClip.open(audioStream);

                // Add volume control
                FloatControl volumeControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volume);

                soundClip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

    public void loop() {
        boolean running = true;

        while (running) {
            // Reset game state
            // Initialize the score Text object
            score.setColor(new Color(255, 255, 255)); // Explicit RGB white color
            astroidStartPosition();
            ufo.setUfo(150 - (ufo.getWidth() / 2), 800 - 100);
            ufo.exploded = false; // Make sure to reset the exploded state
            gameRunning = false;
            ufo.getUfo().setHidden(false);

            // Print a message to help debug
            System.out.println("Waiting for Enter key to start game...");

            // Wait for player to start game
            while (!gameRunning) {
                scoreValue = 0; // Reset score value
                score.setText("Score: " + scoreValue);
                if (window.keyEnterPressed()) {
                    gameRunning = true;
                    System.out.println("Game started!");

                    // Stop any existing music and restart it
                    if (backgroundMusic != null) {
                        backgroundMusic.stop();
                    }
                    playSound("background.wav", true, -20f);
                }
                window.wait(10); // Slightly longer wait to reduce CPU usage
            }

            // Main game loop
            while (gameRunning) {
                astroidFall();
                checkInput();
                checkCollision();

                // If UFO exploded, break out of this loop to restart
                if (ufo.exploded) {
                    System.out.println("UFO exploded! Restarting game...");
                    gameRunning = false;
                    window.wait(1000); // Pause briefly before restart
                    break; // Exit this loop, which will restart from the outer loop
                }

            }
        }
    }

    public void checkCollision() {
        // check if ufo is colliding with astroid
        for (int i = 0; i < astroids.length; i++) {
            if (astroids[i].isColliding()) {
                if (astroids[i].isPowerUp()) {
                    // Handle power-up collision
                    if (astroids[i] instanceof PowerUpAstroid) {
                        ufo.gunPowerUp();
                        astroids[i].setAstroid(-250, -250); // Move it off-screen
                        // playSound("powerup.wav", false, -20f);
                    }
                } else {
                    // Handle normal collision
                    if (ufo.exploded == false) {
                        ufo.explode();
                        playSound("explosion.wav", false, -20f);
                        backgroundMusic.stop();
                        for (int j = 0; j < astroids.length; j++) {
                            astroids[j].setAstroid(-250, -250);
                        }
                        window.wait(100);
                        ufo.hideExplosion();
                    }
                }
            }
            if (astroids[i].getAstroid().intersects(laser.getLaser())) {
                // Handle laser collision
                increaseScore(astroids[i].getScoreValue() + 20);
                astroids[i].setAstroid(-250, -250); // Move it off-screen
                laser.setLaser(-250); // Move laser off-screen
            }
        }
    }

    public void checkInput() {
        double moveAmount = 1;
        if (window.keyPressed('a')) {
            if (ufo.getUfo().getShapeX() - moveAmount < 0) {
                moveAmount = 0; // stop ufo from moving if it would be out of screen
            }
            ufo.ufoMove(-moveAmount);
        }
        if (window.keyPressed('d')) {
            if (ufo.getUfo().getShapeX() + ufo.getWidth() + moveAmount > window.getWidth()) {
                moveAmount = 0; // stop ufo from moving if it would be out of screen
            }
            ufo.ufoMove(moveAmount);
        }
        if (window.keyPressed('w') && ufo.isGunPoweredUp()) {
            ufo.shootLaser();
        }
    }

    public void astroidFall() {
        // overload for easier use
        for (int i = 0; i < astroids.length; i++) {
            astroidFall(i);
        }
    }

    public void astroidFall(int astroidPosition) {
        // move astroid down
        Astroid astroid = astroids[astroidPosition];
        astroid.setAstroid(astroid.getAstroid().getShapeX() + astroid.getXSpeed(),
                astroid.getAstroid().getShapeY() + astroid.getSpeed());
        // check if astroid is out of screen
        if (astroid.getAstroid().getShapeY() > 800) {
            // resets astroid to start position
            handleScore(astroidPosition);
            astroidRandomizer(astroidPosition);
            astroidStartPosition(astroidPosition);
        }
        window.wait(1);
    }

    public void handleScore(int astroidPosition) {
        // increase score depending on astroid type
        increaseScore(astroids[astroidPosition].getScoreValue());
    }

    public void astroidRandomizer(int astroidPosition) {
        // replace astroid with random special astroid
        // random integer: either 1 or 5
        int astroidType = (int) (Math.random() * 100) + 1;
        // if astroidType is 1, replace astroid with normal astroid
        if (astroidType < 80) {
            astroids[astroidPosition] = new Astroid(-250, -250, 1, ufo);
            return;
        }
        // if astroidType is 2, replace astroid with fast astroid
        if (astroidType >= 80 && astroidType < 90) {
            astroids[astroidPosition] = new FastAstroid(-250, -250, 1, ufo);
            return;
        }

        if (astroidType <= 97 && astroidType >= 90) {
            astroids[astroidPosition] = new ZigZagAstroid(-250, -250, 1, ufo);
            return;
        }

        if (astroidType > 97 && astroidType <= 100) {
            astroids[astroidPosition] = new PowerUpAstroid(-250, -250, 1, ufo);
        }

    }

    public void astroidStartPosition(int astroidPosition) {
        // random start position with at least 10px distance to the edge or another
        // astroid
        Astroid astroid = astroids[astroidPosition];
        // random x position in bounds of screen with at least 10px from borders
        int x = (int) (Math.random() * (window.getWidth() - astroid.getWidth() - 20) + 10);
        int y = (int) ((-Math.random() * 150) - 50);
        astroid.setAstroid(x, y);
        // check if astroid is too close to another astroid
        for (int i = 0; i < astroids.length; i++) {
            if (i != astroidPosition) {
                if (Math.abs(x - astroids[i].getAstroid().getShapeX()) < 30
                        || Math.abs(x + astroid.getWidth() - astroids[i].getAstroid().getShapeX()) < 30) {
                    astroidStartPosition(astroidPosition);
                    return;
                }

            }

        }
        astroid.getAstroid().setHidden(false);
    }

    public void astroidStartPosition() {
        // overload for easier use
        for (int i = 0; i < astroids.length; i++) {
            astroidStartPosition(i);
        }
    }

    public static void main(String[] args) {
        new Ufoprogramm();
    }
}