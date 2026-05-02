package DiggingJim.assets;

import java.io.InputStream;
import java.net.URL; // Import URL
import java.util.HashMap;
import java.util.Map;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;

public class AssetManager {
    private static volatile AssetManager instance;

    private final Map<String, Image> images = new HashMap<>();
    private final Map<String, MediaPlayer> sounds = new HashMap<>();

    private Image loadImage(String resourcePath) {
        URL imageUrl = getClass().getResource(resourcePath);
        if (imageUrl == null) {
            // Log the error clearly
            System.err.println("FATAL ERROR: Cannot find image resource: " + resourcePath);
            // Optionally, throw an exception to halt execution if the image is critical
            throw new RuntimeException("Missing required image resource: " + resourcePath);
        }
        return new Image(imageUrl.toExternalForm());
    }

    private MediaPlayer loadSound(String resourcePath) {
        URL soundUrl = getClass().getResource(resourcePath);
        if (soundUrl == null) {
            System.err.println("FATAL ERROR: Cannot find sound resource: " + resourcePath);
            throw new RuntimeException("Missing required sound resource: " + resourcePath);
        }
        Media media = new Media(soundUrl.toExternalForm());
        return new MediaPlayer(media);
    }

    private void loadFont(String resourcePath, double size) {
        try (InputStream fontStream = getClass().getResourceAsStream(resourcePath)) {
            if (fontStream == null) {
                System.err.println("FATAL ERROR: Cannot find font resource: " + resourcePath);
                throw new RuntimeException("Missing required font resource: " + resourcePath);
            }
            if (Font.loadFont(fontStream, size) == null) {
                System.err.println("ERROR: Failed to load font from resource: " + resourcePath);
            }
        } catch (java.io.IOException e) {
            System.err.println("ERROR: Failed to close font stream for: " + resourcePath);
            e.printStackTrace();
        }
    }

    // --- Constructor and Singleton ---

    private AssetManager() {
        // Load assets using helper methods
        try {
            System.out.println("Loading assets...");
            loadImages();
            loadSounds();
            loadFonts();
            System.out.println("Assets loaded successfully.");
        } catch (Exception e) {
            System.err.println("!!! CRITICAL ERROR DURING ASSET LOADING !!!");
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize AssetManager", e);
        }
    }

    public static AssetManager getInstance() {
        if (instance == null) {
            synchronized (AssetManager.class) {
                if (instance == null) {
                    instance = new AssetManager();
                }
            }
        }
        return instance;
    }

    // --- Asset Loading Logic ---

    private void loadImages() {
        // Assumes images are in src/main/resources/images/
        images.put("initialBoxBackground", loadImage("/images/InitialBoxBackground.png"));
        images.put("characterFront", loadImage("/images/Character front face.png"));
        images.put("characterLeft",  loadImage("/images/Character left face.png"));
        images.put("characterRight", loadImage("/images/Character right face.png"));
        images.put("sand",           loadImage("/images/Sand.png"));
        images.put("rock",           loadImage("/images/Rock.png"));
        images.put("diamond",        loadImage("/images/Diamond Blue.png"));
        images.put("monster",        loadImage("/images/Monster.png"));
        images.put("heart",          loadImage("/images/Heart.png"));
        images.put("emptyHeart",     loadImage("/images/Empty Heart.png"));
        images.put("openDoor",       loadImage("/images/Open Door.png"));
        images.put("closedDoor",     loadImage("/images/Door.png"));
        images.put("bricks",         loadImage("/images/Bricks.png"));
        images.put("icon",           loadImage("/images/icon.png"));
        images.put("gameOver",       loadImage("/images/Game Over.png"));
        images.put("victory",        loadImage("/images/Victory.png"));
    }

    private void loadSounds() {
        // Assumes sounds are in src/main/resources/sounds/
        // *** RENAME SOUND FILES TO AVOID SPACES (Recommended) ***
        // Example: "Start Sound.MP3" -> "start_sound.mp3"
        sounds.put("start",        loadSound("/sounds/start_sound.MP3")); 
        sounds.put("buttonClick",  loadSound("/sounds/button_sound.mp3")); 
        sounds.put("pickDiamond",  loadSound("/sounds/pick_diamond.mp3"));
        sounds.put("gameOver",     loadSound("/sounds/game_over.mp3"));
        sounds.put("victory",      loadSound("/sounds/victory.mp3"));

        // Configure sound players AFTER loading
        MediaPlayer pickDiamondPlayer = sounds.get("pickDiamond");
        if (pickDiamondPlayer != null) { // Check if loading succeeded
            pickDiamondPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        } else {
            System.err.println("Warning: Could not configure 'pickDiamond' sound player as it failed to load.");
        }
    }

    private void loadFonts() {
        // Assumes fonts are in src/main/resources/fonts/
        loadFont("/fonts/Vampire Wars.ttf", 100);
        loadFont("/fonts/Midnight Moon.ttf", 100);
    }

    // --- Public Accessor and Control Methods ---

    public Image getImage(String key) {
        Image img = images.get(key);
        if (img == null) {
            System.err.println("Warning: Requested image key not found: " + key);
            // Return a default/placeholder image?
        }
        return img;
    }

    public MediaPlayer getSound(String key) {
        MediaPlayer sound = sounds.get(key);
        if (sound == null) {
            System.err.println("Warning: Requested sound key not found: " + key);
            // Return a dummy player?
        }
        return sound;
    }

    public void playSound(String key) {
        MediaPlayer sound = sounds.get(key);
        if (sound != null) {
            sound.stop(); // Stop previous playback
            sound.play(); // Start from the beginning
        } else {
            System.err.println("Warning: Cannot play sound, key not found: " + key);
        }
    }

    public void stopSound(String key) {
        MediaPlayer sound = sounds.get(key);
        if (sound != null) {
            sound.stop();
        } else {
            System.err.println("Warning: Cannot stop sound, key not found: " + key);
        }
    }
}