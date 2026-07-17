package org.arig.lucifer.fx;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SplashView {

    // Total animation duration in the SVG: last element at 3.6s + 1s anim = 4.6s
    private static final double DISPLAY_SECONDS = 5.0;
    private static final double FADE_SECONDS = 0.6;

    private final Stage splashStage;
    private final Runnable onDone;

    public SplashView(Stage splashStage, Runnable onDone) {
        this.splashStage = splashStage;
        this.onDone = onDone;
    }

    public void show() throws Exception {
        URL svgUrl = getClass().getResource("/org/arig/lucifer/fx/logo_animated.svg");
        String svgRaw = new String(svgUrl.openStream().readAllBytes(), StandardCharsets.UTF_8);

        // Make the SVG scale to fill the container width
        String svgScaled = svgRaw
                .replaceFirst("width=\"[^\"]+\"", "width=\"100%\"")
                .replaceFirst("height=\"[^\"]+\"", "height=\"auto\"");

        String html = """
                <!DOCTYPE html>
                <html>
                <head><meta charset="utf-8"><style>
                  * { margin: 0; padding: 0; box-sizing: border-box; }
                  html, body {
                    background: #1e1e2e;
                    width: 100%%; height: 100%%;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    overflow: hidden;
                  }
                  .wrap {
                    width: 80%%;
                    max-width: 520px;
                  }
                </style></head>
                <body><div class="wrap">%s</div></body>
                </html>
                """.formatted(svgScaled);

        WebView webView = new WebView();
        webView.setContextMenuEnabled(false);
        webView.getEngine().loadContent(html, "text/html");

        StackPane root = new StackPane(webView);
        root.setStyle("-fx-background-color: #1e1e2e;");

        Scene scene = new Scene(root, 660, 380);
        scene.setFill(Color.web("#1e1e2e"));

        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.setScene(scene);
        splashStage.centerOnScreen();
        splashStage.show();

        // Click to dismiss early
        scene.setOnMouseClicked(e -> dismiss(root));

        // Auto-dismiss after the animation has played
        PauseTransition pause = new PauseTransition(Duration.seconds(DISPLAY_SECONDS));
        pause.setOnFinished(e -> dismiss(root));
        pause.play();
    }

    private void dismiss(StackPane root) {
        FadeTransition fade = new FadeTransition(Duration.seconds(FADE_SECONDS), root);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(f -> {
            splashStage.close();
            onDone.run();
        });
        fade.play();
    }
}
