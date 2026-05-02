package DiggingJim.entities.environment;

import DiggingJim.assets.AssetManager;
import DiggingJim.config.GameConfig;
import DiggingJim.core.GameObject;

public class Door extends GameObject {
    private boolean isOpen;

    public Door(double x, double y, boolean isOpen) {
        super(isOpen ? "openDoor" : "closedDoor", GameConfig.DOOR_SIZE, GameConfig.DOOR_SIZE);
        this.isOpen = isOpen;
        setX(x);
        setY(y);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
        setImage(AssetManager.getInstance().getImage(open ? "openDoor" : "closedDoor"));
    }
}