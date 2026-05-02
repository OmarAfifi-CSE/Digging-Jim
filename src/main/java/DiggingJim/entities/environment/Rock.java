package DiggingJim.entities.environment;

import DiggingJim.config.GameConfig;
import DiggingJim.core.GameObject;

public class Rock extends GameObject {
    private double gravity = 0;
    private boolean isFalling = false;

    public Rock(double x, double y) {
        super("rock", GameConfig.ROCK_WIDTH, GameConfig.ROCK_HEIGHT);
        setX(x);
        setY(y);
    }

    public void applyGravity() {
        if (isFalling) {
            gravity += GameConfig.GRAVITY;
            setY(getY() + gravity);
        }
    }

    public void startFalling() {
        this.isFalling = true;
    }

    public void stopFalling() {
        this.isFalling = false;
        resetGravity();
    }

    public boolean isFalling() {
        return isFalling;
    }

    public void resetGravity() {
        gravity = 0;
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double value) {
        this.gravity = value;
    }
}