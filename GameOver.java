import sas.*;
import java.awt.Color;

public class GameOver {
    private Picture gameOver;
    private Picture playAgain;
    private Picture quit;
    private Text score;
    private int scoreValue = 0, highScoreValue = 0;
    private Text highScore;
    Ufo ufo;

    GameOver(Ufo pUfo) {
        gameOver = new Picture(0 - 300, 0, 300, 800, "GameOver.png");
        playAgain = new Picture(50 - 300, 475, 200, 100, "PlayAgain.png");
        quit = new Picture(50 - 300, 600, 200, 100, "Quit.png");
        score = new Text(30 - 300, 100, "Score: " + scoreValue, Color.WHITE);
        highScore = new Text(30 - 300, 130, "High Score: " + highScoreValue, Color.WHITE);
        ufo = pUfo;
        gameOver.setHidden(true);
        playAgain.setHidden(true);
        quit.setHidden(true);
        score.setHidden(true);
        highScore.setHidden(true);

    }

    public void moveGameOver(double pX, double pY) {
        gameOver.move(pX, pY);
        playAgain.move(pX, pY);
        quit.move(pX, pY);
        score.move(pX, pY);
        highScore.move(pX, pY);
    }

    public void hideGameOver(boolean hideGameover) {
        gameOver.setHidden(hideGameover);
        playAgain.setHidden(hideGameover);
        quit.setHidden(hideGameover);
        score.setHidden(hideGameover);
        highScore.setHidden(hideGameover);
        // make Fonts Monospace
        score.setFontMonospaced(false, 20);
        highScore.setFontMonospaced(false, 20);
    }

    public void setScore(int pScore) {
        scoreValue = pScore;
        score.setText("Score: " + scoreValue);
    }

    public void setHighScore(int pHighScore) {
        highScoreValue = pHighScore;
        highScore.setText("High Score: " + highScoreValue);
    }

    public boolean restartClicked() {
        if (playAgain.getHidden() == false) {
            if (playAgain.mouseClicked()) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    public boolean quitClicked() {
        if (quit.getHidden() == false) {
            if (quit.mouseClicked()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
