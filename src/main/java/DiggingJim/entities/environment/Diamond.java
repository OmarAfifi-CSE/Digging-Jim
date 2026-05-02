package DiggingJim.entities.environment;

import DiggingJim.config.GameConfig;
import DiggingJim.core.GameObject;

public class Diamond extends GameObject {
    public Diamond(double x, double y) {
        super("diamond", GameConfig.DIAMOND_WIDTH, GameConfig.DIAMOND_HEIGHT);
        setX(x);
        setY(y);
    }
}