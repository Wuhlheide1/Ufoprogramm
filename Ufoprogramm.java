import sas.*;

import java.awt.Color;
import java.util.ArrayList;
import javax.sound.sampled.*;
import java.io.*;

public class Ufoprogramm {
    private static final String HIGH_SCORE_FILE = "highscore.txt";
    View window;
    GameOver gameOverScreen;
    Picture background;
    Astroid astroids[] = new Astroid[3];
    FastAstroid fastAstroid;
    Ufo ufo;
    Clip backgroundMusic;
    boolean gameRunning = false;
    Text score;
    int scoreValue = 0;
    private Laser laser; // Laser object for UFO weapons system

    // Constructor - Sets up the game environment and initializes all game objects
    Ufoprogramm() {
        window = new View(300, 800, "Ufo");
        background = new Picture(0, 0, "hintergrund.png");

        // Initialize laser object for UFO weapons system
        laser = new Laser(-20, 0, 1);

        // Create player UFO and center it at the bottom of the screen
        ufo = new Ufo(150, 800 - 100, 1, laser, this);
        ufo.ufoMove(-(ufo.getWidth() / 2));

        // Initialize game over screen after UFO is created
        gameOverScreen = new GameOver(ufo);
        gameOverScreen.hideGameOver(true);

        // Create asteroid objects and position them off-screen initially
        for (int i = 0; i < astroids.length; i++) {
            astroids[i] = new Astroid(-250, -250, 1, ufo);
        }

        // Start background music and initialize score display
        playSound("background.wav", true, -20f);
        score = new Text(15, 15, "Score: 0", Color.WHITE);
        score.setFontMonospaced(false, 20);

        // Begin the main game loop
        loop();
    }

    // Handles all collision detection between game objects
    public void checkCollision() {
        for (int i = 0; i < astroids.length; i++) {
            // Detect if UFO has collided with any asteroid
            if (astroids[i].isColliding()) {
                handleUfoCollision(i);
            }

            // Check if any active lasers have hit asteroids
            checkLaserCollision(i);
        }
    }

    // Processes what happens when the UFO collides with an asteroid
    private void handleUfoCollision(int asteroidIndex) {
        if (astroids[asteroidIndex].isPowerUp()) {
            if (astroids[asteroidIndex] instanceof PowerUpAstroid) {
                ufo.gunPowerUp();
                playSound("powerup.wav", false, -20f);
                astroids[asteroidIndex].setAstroid(-250, -250);
            }
        } else if (!ufo.exploded) {
            ufo.explode();
            playSound("explosion.wav", false, -20f);
            backgroundMusic.stop();

            // Clear all asteroids from screen after explosion
            for (int j = 0; j < astroids.length; j++) {
                astroids[j].setAstroid(-250, -250);
            }

            window.wait(100);
            ufo.hideExplosion();
        }
    }

    // Handles laser-asteroid collisions and awards points
    private void checkLaserCollision(int asteroidIndex) {
        ArrayList<Laser> activeLasers = ufo.getActiveLasers();
        for (int l = 0; l < activeLasers.size(); l++) {
            if (ufo.laserIntersects(astroids[asteroidIndex].getAstroid(), activeLasers.get(l))) {
                // Award points based on asteroid type plus bonus for shooting
                increaseScore(astroids[asteroidIndex].getScoreValue() + 20);

                // Remove destroyed asteroid and generate a new one
                astroids[asteroidIndex].setAstroid(-250, -250);
                astroidRandomizer(asteroidIndex);
                astroidStartPosition(asteroidIndex);
                playSound("explosion.wav", false, -20f);
                // Remove the laser beam that hit the asteroid
                activeLasers.get(l).setHidden(true);
                break;
            }
        }
    }

    // Updates the player's score and refreshes the display
    public void increaseScore(int pScore) {
        scoreValue = scoreValue + pScore;
        score.setText("Score: " + scoreValue);
    }

    // Audio system for game sounds and music
    private void playSound(String musicFileName, boolean loop, float volume) {
        try {
            File musicFile = new File(musicFileName);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);

            if (loop) {
                // Background music that plays continuously
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioStream);

                // Adjust volume level for background music
                FloatControl volumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volume);

                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                // One-time sound effects (explosions, power-ups, etc.)
                Clip soundClip = AudioSystem.getClip();
                soundClip.open(audioStream);

                // Adjust volume for sound effects
                FloatControl volumeControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volume);

                soundClip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

    public void gameNotRunning() {
        restartGame();
        System.out.println("Game started!");
    }

    // Handles restarting the game after game over
    public void restartGame() {
        gameOverScreen.hideGameOver(true);
        ufo.exploded = false;
        gameRunning = true;

        // Reset score and game elements
        scoreValue = 0;
        score.setText("Score: " + scoreValue);
        score.setHidden(false);
        ufo.getUfo().setHidden(false);

        // Clear all active lasers
        for (Laser laser : ufo.getActiveLasers()) {
            laser.setHidden(true);
        }
        ufo.getActiveLasers().clear();

        // Reset asteroids
        for (int i = 0; i < astroids.length; i++) {
            astroids[i].setAstroid(-250, -250);
        }
        astroidStartPosition();

        // Reset UFO position
        ufo.setUfo(150 - (ufo.getWidth() / 2), 800 - 100);

        // Restart background music
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
        playSound("background.wav", true, -20f);

        System.out.println("Game restarted!");
    }

    // Main game loop that handles game states and player input
    public void loop() {
        boolean running = true;
        int highScore = getHighScore();
        System.out.println("High Score: " + highScore);

        // Ensure game over screen is hidden at startup
        gameOverScreen.hideGameOver(true);
        gameOverScreen.moveGameOver(300, 0);

        while (running) {
            // Reset game state for a new round
            score.setColor(new Color(255, 255, 255));
            ufo.setHidden(true);

            // Wait for player to press Enter to begin the game
            while (!gameRunning) {
                gameNotRunning();
            }

            // Active gameplay loop - runs until player's UFO is destroyed
            while (gameRunning) {
                astroidFall();
                checkInput();
                checkCollision();
                // Don't hide game over screen during gameplay - it should only be hidden/shown
                // at specific times

                if (ufo.exploded) {
                    boolean gameOver = true;
                    System.out.println("UFO exploded! Restarting game...");
                    gameRunning = false;
                    // Make sure the class name matches exactly
                    saveHighScore(scoreValue);
                    gameOverScreen.setHighScore(getHighScore());
                    gameOverScreen.setScore(scoreValue);
                    window.wait(1000);
                    gameOverScreen.hideGameOver(false);
                    score.setHidden(true);
                    while (gameOver) {
                        boolean restartGame = gameOverScreen.restartClicked();
                        if (restartGame) {
                            restartGame();
                            running = true;
                            gameOver = false; // Break out of game over loop
                            break;
                        }
                        boolean quitGame = gameOverScreen.quitClicked();
                        if (quitGame) {
                            gameOver = false;
                            running = false;
                            System.exit(0);
                        }
                        window.wait(10);
                    }
                }
            }
        }
    }

    public static void saveHighScore(int score) {
        // Only save if the new score is higher than the current high score
        int currentHighScore = getHighScore();
        if (score > currentHighScore) {
            try {
                FileWriter writer = new FileWriter(HIGH_SCORE_FILE);
                writer.write(String.valueOf(score));
                writer.close();
                System.out.println("New high score saved: " + score);
            } catch (IOException e) {
                System.err.println("Error saving high score: " + e.getMessage());
            }
        }
    }

    public static int getHighScore() {
        int highScore = 0;
        try {
            File file = new File(HIGH_SCORE_FILE);
            if (!file.exists()) {
                return 0; // Return 0 if file doesn't exist yet
            }

            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.err.println("Error reading high score: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid high score format: " + e.getMessage());
        }
        return highScore;
    }

    // Processes keyboard input for UFO movement and laser firing
    public void checkInput() {
        double moveAmount = 1;
        if (window.keyPressed('a')) {
            if (ufo.getUfo().getShapeX() - moveAmount < 0) {
                moveAmount = 0; // Prevent UFO from moving off the left edge
            }
            ufo.ufoMove(-moveAmount);
        }
        if (window.keyPressed('d')) {
            if (ufo.getUfo().getShapeX() + ufo.getWidth() + moveAmount > window.getWidth()) {
                moveAmount = 0; // Prevent UFO from moving off the right edge
            }
            ufo.ufoMove(moveAmount);
        }
        if (ufo.isGunPoweredUp()) {
            ufo.shootLaser();
        }
    }

    // Updates positions of all asteroids
    public void astroidFall() {
        for (int i = 0; i < astroids.length; i++) {
            astroidFall(i);
        }
    }

    // Moves a specific asteroid and checks if it's gone off-screen
    public void astroidFall(int astroidPosition) {
        Astroid astroid = astroids[astroidPosition];
        // Update asteroid position with both vertical and horizontal movement
        astroid.setAstroid(astroid.getAstroid().getShapeX() + astroid.getXSpeed(),
                astroid.getAstroid().getShapeY() + astroid.getSpeed());

        // If asteroid has moved past the bottom of the screen
        if (astroid.getAstroid().getShapeY() > 800) {
            // Award points for successfully avoiding the asteroid
            handleScore(astroidPosition);
            // Create a new random asteroid type
            astroidRandomizer(astroidPosition);
            // Position the new asteroid at the top of the screen
            astroidStartPosition(astroidPosition);
        }
        window.wait(1);
    }

    // Awards points when an asteroid passes the bottom of the screen
    public void handleScore(int astroidPosition) {
        increaseScore(astroids[astroidPosition].getScoreValue());
    }

    // Randomly selects which type of asteroid to create based on difficulty weights
    public void astroidRandomizer(int astroidPosition) {
        int astroidType = (int) (Math.random() * 100) + 1;

        // Regular asteroids - most common (85% chance)
        if (astroidType <= 85) {
            astroids[astroidPosition] = new Astroid(-250, -250, 1, ufo);
            return;
        }

        // Fast asteroids - moderate challenge (10% chance)
        if (astroidType <= 95) {
            astroids[astroidPosition] = new FastAstroid(-250, -250, 1, ufo);
            return;
        }

        // ZigZag asteroids - difficult to avoid (3% chance)
        if (astroidType <= 98) {
            astroids[astroidPosition] = new ZigZagAstroid(-250, -250, 1, ufo);
            return;
        }

        // Power-up asteroids - rare bonus (2% chance)
        astroids[astroidPosition] = new PowerUpAstroid(-250, -250, 1, ufo);
    }

    // Positions an asteroid at the top of the screen with random X coordinate
    public void astroidStartPosition(int astroidPosition) {
        Astroid astroid = astroids[astroidPosition];

        // Calculate random X position within screen boundaries
        int x = (int) (Math.random() * (window.getWidth() - astroid.getWidth() - 20) + 10);
        // Position above the top of the screen with random offset
        int y = (int) ((-Math.random() * 150) - 50);
        astroid.setAstroid(x, y);
        astroid.getAstroid().setHidden(false); // Make sure asteroid is visible

        // Ensure asteroids aren't too close to each other to prevent clumping
        for (int i = 0; i < astroids.length; i++) {
            if (i != astroidPosition && astroids[i].getAstroid().getShapeY() > -250) { // Only check if asteroid is
                                                                                       // active
                if (Math.abs(x - astroids[i].getAstroid().getShapeX()) < 30
                        || Math.abs(x + astroid.getWidth() - astroids[i].getAstroid().getShapeX()) < 30) {
                    astroidStartPosition(astroidPosition); // Recursively try a new position
                    return;
                }
            }
        }
    }

    // Positions all asteroids at the start of the game
    public void astroidStartPosition() {
        for (int i = 0; i < astroids.length; i++) {
            astroidStartPosition(i);
        }
    }

    // Updates the laser object reference when needed
    public void overwiriteLaser(Laser newLaser) {
        laser = newLaser;
    }

    public static void main(String[] args) {
        Ufoprogramm ufoProgramm = new Ufoprogramm();
    }
}