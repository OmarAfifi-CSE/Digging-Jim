package DiggingJim.entities.characters;

import DiggingJim.assets.AssetManager;
import DiggingJim.config.GameConfig;
import DiggingJim.core.GameObject;

public class GameCharacter extends GameObject {
    private boolean movingUp = false;
    private boolean movingDown = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private double upSpeed = GameConfig.CHARACTER_SPEED;
    private double downSpeed = GameConfig.CHARACTER_SPEED;
    private double leftSpeed = GameConfig.CHARACTER_SPEED;
    private double rightSpeed = GameConfig.CHARACTER_SPEED;

    public GameCharacter() {
        super("characterFront", GameConfig.CHARACTER_SIZE, GameConfig.CHARACTER_SIZE);
        setX(GameConfig.CHARACTER_START_X);
        setY(GameConfig.CHARACTER_START_Y);
    }

    public void setMovingUp(boolean movingUp) {
        this.movingUp = movingUp;
        updateFacing();
    }

    public void setMovingDown(boolean movingDown) {
        this.movingDown = movingDown;
        updateFacing();
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
        updateFacing();
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
        updateFacing();
    }

    public boolean isMovingUp() {
        return movingUp;
    }

    public boolean isMovingDown() {
        return movingDown;
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    public void setUpSpeed(double speed) {
        this.upSpeed = speed;
    }

    public void setDownSpeed(double speed) {
        this.downSpeed = speed;
    }

    public void setLeftSpeed(double speed) {
        this.leftSpeed = speed;
    }

    public void setRightSpeed(double speed) {
        this.rightSpeed = speed;
    }

    public double getUpSpeed() {
        return upSpeed;
    }

    public double getDownSpeed() {
        return downSpeed;
    }

    public double getLeftSpeed() {
        return leftSpeed;
    }

    public double getRightSpeed() {
        return rightSpeed;
    }

    public void resetSpeeds() {
        this.upSpeed = GameConfig.CHARACTER_SPEED;
        this.downSpeed = GameConfig.CHARACTER_SPEED;
        this.leftSpeed = GameConfig.CHARACTER_SPEED;
        this.rightSpeed = GameConfig.CHARACTER_SPEED;
    }

    public void move(double minX, double minY, double maxX, double maxY, double deltaTime) {
        double dx = 0, dy = 0;
        // Normalize delta time to target FPS
        double speedMultiplier = deltaTime * GameConfig.TARGET_FPS;

        if (movingUp && !movingDown && !movingLeft && !movingRight) {
            dy -= upSpeed * speedMultiplier;
        }
        if (movingDown && !movingUp && !movingLeft && !movingRight) {
            dy += downSpeed * speedMultiplier;
        }
        if (movingLeft && !movingUp && !movingDown && !movingRight) {
            dx -= leftSpeed * speedMultiplier;
        }
        if (movingRight && !movingUp && !movingDown && !movingLeft) {
            dx += rightSpeed * speedMultiplier;
        }

        double newX = getX() + dx;
        double newY = getY() + dy;

        // Apply boundary constraints
        newX = Math.max(minX, Math.min(newX, maxX - GameConfig.CHARACTER_SIZE));
        newY = Math.max(minY, Math.min(newY, maxY - GameConfig.CHARACTER_SIZE));

        setX(newX);
        setY(newY);
    }

    private void updateFacing() {
        if (movingLeft && !movingRight) {
            setImage(AssetManager.getInstance().getImage("characterLeft"));
        } else if (movingRight && !movingLeft) {
            setImage(AssetManager.getInstance().getImage("characterRight"));
        } else if (!movingLeft && !movingRight || (movingLeft && movingRight)) {
            setImage(AssetManager.getInstance().getImage("characterFront"));
        }
    }

    public void resetPosition() {
        setX(GameConfig.CHARACTER_START_X);
        setY(GameConfig.CHARACTER_START_Y);
        resetSpeeds();
    }

    public boolean isMoving() {
        return movingUp || movingDown || movingLeft || movingRight;
    }
}