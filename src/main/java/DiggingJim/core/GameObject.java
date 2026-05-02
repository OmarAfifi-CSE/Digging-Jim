package DiggingJim.core;

import DiggingJim.assets.AssetManager;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;

public abstract class GameObject extends ImageView {


    public GameObject(String imageKey, double width, double height) {
        setImage(AssetManager.getInstance().getImage(imageKey));
        setFitWidth(width);
        setFitHeight(height);
    }

    public boolean intersects(GameObject other) {
        Bounds thisBounds = this.getBoundsInParent();
        Bounds otherBounds = other.getBoundsInParent();
        return thisBounds.intersects(otherBounds);
    }

    public boolean intersects(double x, double y, double width, double height) {
        Bounds thisBounds = this.getBoundsInParent();
        return thisBounds.intersects(x, y, width, height);
    }

    public void update() {
        // To be overridden by subclasses
    }
}
