package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.domain.Shuttle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;


public class DetailsView extends Scene {

    private final static Logger logger = LoggerFactory.getLogger(DetailsView.class);
    private Shuttle shuttle;

    @FXML
    private HBox indicatorHBox;
    private Text shuttleNameText;
    private Rectangle shuttleMenuRectangle;

    public DetailsView(Parent parent, Shuttle shuttle) {
        super(parent);
        this.shuttle = shuttle;
        initialize();
    }

    public void initialize() {
        lookupContent();


        Platform.runLater(() -> {
            fitIndicatorHBox();
        });

    }

    private void lookupContent() {
        indicatorHBox = (HBox) lookup("#indicatorHBox");
        shuttleNameText = (Text) lookup("#shuttleNameText");
        shuttleMenuRectangle = (Rectangle) lookup("#shuttleMenuRectangle");


        shuttleNameText.setText(shuttle.getName());


        shuttleMenuRectangle.heightProperty().bind(this.heightProperty());
    }

    private void fitIndicatorHBox() {
        double newWidth = indicatorHBox.getWidth() / indicatorHBox.getChildren().size();

        Color[] colors = switch (shuttle.getStatus()) {
            case "Gelandet" -> new Color[]{Color.GREEN, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE};
            case "Inspektion 1" -> new Color[]{Color.GREEN, Color.ORANGE, Color.WHITE, Color.WHITE, Color.WHITE};
            case "In Wartung" -> new Color[]{Color.GREEN, Color.GREEN, Color.ORANGE, Color.WHITE, Color.WHITE};
            case "Inspektion 2" -> new Color[]{Color.GREEN, Color.GREEN, Color.GREEN, Color.ORANGE, Color.WHITE};
            case "Erledigt - Warte auf Freigabe" -> new Color[]{Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN, Color.ORANGE};
            case "Freigegeben" -> new Color[]{Color.GREEN, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE};
            case "Unterwegs" -> new Color[]{Color.ORANGE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE};
            default -> null;
        };
        for (int i = 0; i < indicatorHBox.getChildren().size(); i++) {
            StackPane stackPane = (StackPane) indicatorHBox.getChildren().get(i);
            for (Node n : stackPane.getChildren()) {
                if (n.getClass() == Rectangle.class) {
                    Rectangle rectangle = (Rectangle) n;
                    rectangle.setWidth(newWidth);
                    assert colors != null;
                    rectangle.setFill(colors[i]);
                }
                if (n.getClass() == Text.class) {
                    Text text = (Text) n;
                    text.wrappingWidthProperty().bind(indicatorHBox.widthProperty().divide(indicatorHBox.getChildren().size()));
                }
            }
        }
    }
}
