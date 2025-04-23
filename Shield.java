import sas.*;

public class Shield {
    Picture shield;
    double shieldWidth;
    private boolean blinked = false;

    Shield(double pX, double pY, double pScale) {
        shield = new Picture(pX, pY, 55 * pScale, 55 * pScale, "Shield2.png");
        shieldWidth = shieldWidth * pScale;
    }

    public void setPosition(double pX, double pY) {
        shield.moveTo(pX, pY);
    }

    public void hideShield(boolean hideShield) {
        shield.setHidden(hideShield);
    }

    public void centerOnUfo(Ufo ufo) {
        // Get UFO's center coordinates
        double ufoCenterX = ufo.getUfo().getShapeX() + ufo.getUfo().getShapeWidth() / 2;
        double ufoCenterY = ufo.getUfo().getShapeY() + ufo.getUfo().getShapeHeight() / 2;

        // Calculate shield position to match centers
        double shieldX = ufoCenterX - shield.getShapeWidth() / 2;
        double shieldY = ufoCenterY - shield.getShapeHeight() / 2;

        setPosition(shieldX, shieldY);
        hideShield(false);
    }

    public Picture getShape() {
        return shield;
    }

    public void resetTexture() {
        shield.loadTexture("Shield2.png");
    }

    public void blinkShield() {
        if (!blinked) {
            shield.loadTexture("Shield.png");
            blinked = true;
        } else {
            resetTexture();
            blinked = false;
        }
    }
}
