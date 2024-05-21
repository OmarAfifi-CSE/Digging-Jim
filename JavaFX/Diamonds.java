package diggingjim;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Diamonds extends ImageView {
    
    private Image diamondImage;
    
    public Diamonds() {
        diamondImage = new Image("images/Diamond Blue.png");
        setImage(diamondImage);
    }
    
}
