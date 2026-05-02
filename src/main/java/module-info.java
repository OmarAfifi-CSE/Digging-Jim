// module-info.java
module DiggingJimGame { // Or whatever you want to name the module

    // You NEED requires for JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;  // If you use FXML
    requires javafx.media;  // If you use Media
    requires javafx.graphics; // Usually needed

    // Export YOUR main package so JavaFX can access your Application class
    exports DiggingJim;

    // Or 'opens' if needed for reflection by frameworks
     opens DiggingJim to javafx.fxml;
    exports DiggingJim.config;
    opens DiggingJim.config to javafx.fxml;
    exports DiggingJim.core;
    opens DiggingJim.core to javafx.fxml;
    exports DiggingJim.entities.characters;
    opens DiggingJim.entities.characters to javafx.fxml;
    exports DiggingJim.entities.environment;
    opens DiggingJim.entities.environment to javafx.fxml;
    exports DiggingJim.assets;
    opens DiggingJim.assets to javafx.fxml;
    exports DiggingJim.input;
    opens DiggingJim.input to javafx.fxml;
    exports DiggingJim.physics;
    opens DiggingJim.physics to javafx.fxml;
    exports DiggingJim.ui;
    opens DiggingJim.ui to javafx.fxml;
    exports DiggingJim.world;
    opens DiggingJim.world to javafx.fxml;
}