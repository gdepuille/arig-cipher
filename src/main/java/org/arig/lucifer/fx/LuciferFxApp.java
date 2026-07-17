package org.arig.lucifer.fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class LuciferFxApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage splashStage = new Stage();
        new SplashView(splashStage, () -> new MainView(primaryStage).show()).show();
    }

    @Override
    public void stop() {
        Platform.exit();
    }
}
