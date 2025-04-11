package com.rocketshipcheckingtool.ui.technician;

import com.rocketshipcheckingtool.domain.Manage;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Util {

    private static int detailsImageCount = 1;

    public static void calculateEntryHBoxMargin(HBox shuttleHeaderHBox, HBox shuttleEntryHBox){
        ObservableList<Node> shuttleHeaderHBoxChildren = shuttleHeaderHBox.getChildren();
        ObservableList<Node> shuttleEntryHBoxChildren = shuttleEntryHBox.getChildren();

        for (int i = 0; i < shuttleHeaderHBoxChildren.size(); i++) {
            Node header1 = shuttleHeaderHBoxChildren.get(i);
            Insets marginHeader = HBox.getMargin(header1);
            BoundingBox dimensions = (BoundingBox) header1.getBoundsInParent();
            if (marginHeader != null){
                Node entry1 = shuttleEntryHBoxChildren.get(i);
                Node entry2 = shuttleEntryHBoxChildren.get(i - 1);
                Node header2 = shuttleHeaderHBoxChildren.get(i - 1);
                Insets marginEntry = HBox.getMargin(entry1);

                double difference = (header2.getBoundsInParent().getWidth()) - (entry2.getBoundsInParent().getWidth());
                double newLeftMargin = marginHeader.getLeft() + difference;
                HBox.setMargin(entry1, new Insets(marginEntry.getTop(),marginEntry.getRight(),marginEntry.getBottom(), newLeftMargin));
            }
        }
        if (shuttleEntryHBox.getChildren().get(shuttleEntryHBox.getChildren().size() - 1) instanceof ImageView) {
            Node detailsImage = shuttleEntryHBox.getChildren().get(shuttleEntryHBox.getChildren().size() - 1);
            Node secondLastEntry = shuttleEntryHBox.getChildren().get(shuttleEntryHBox.getChildren().size() - 2);
            Insets marginDetailsImage = HBox.getMargin(detailsImage);
            HBox.setMargin(detailsImage, new Insets(marginDetailsImage.getTop(), marginDetailsImage.getRight(), marginDetailsImage.getBottom(), (marginDetailsImage.getLeft() - secondLastEntry.getBoundsInParent().getWidth())));
        }

    }

    public static VBox copyVBox(VBox original) {
        VBox copy = new VBox();
        copy.setSpacing(original.getSpacing());
        copy.setAlignment(original.getAlignment());
        copy.setPadding(original.getPadding());

        for (Node child : original.getChildren()) {
            Node copiedChild = null;

            if (child instanceof HBox hbox) {
                copiedChild = copyHBox(hbox);
            } else if (child instanceof Line line) {
                Line lineCopy = new Line();
                lineCopy.setStartX(line.getStartX());
                lineCopy.setStartY(line.getStartY());
                lineCopy.setEndX(line.getEndX());
                lineCopy.setEndY(line.getEndY());
                lineCopy.setStroke(line.getStroke());
                lineCopy.setStrokeWidth(line.getStrokeWidth());
                copiedChild = lineCopy;
            }

            if (copiedChild != null) {
                Insets margin = VBox.getMargin(child);
                copy.getChildren().add(copiedChild);
                if (margin != null) {
                    VBox.setMargin(copiedChild, margin);
                }
            }

            Insets originalMargin = VBox.getMargin(original);
            if (originalMargin != null) {
                VBox.setMargin(copy, originalMargin);
            }
        }

        return copy;
    }


    private static HBox copyHBox(HBox original) {
        HBox copy = new HBox();
        copy.setSpacing(original.getSpacing());
        copy.setAlignment(original.getAlignment());
        copy.setPadding(original.getPadding());

        for (Node child : original.getChildren()) {
            Node copiedChild = null;

            if (child instanceof Text text) {
                Text textCopy = new Text(text.getText());
                textCopy.setFont(text.getFont());
                textCopy.setFill(text.getFill());
                copiedChild = textCopy;
            } else if (child instanceof ImageView imagView) {
                ImageView imageCopy = new ImageView(imagView.getImage());
                imageCopy.setFitHeight(imagView.getFitHeight());
                imageCopy.setFitWidth(imagView.getFitWidth());
                imageCopy.setPreserveRatio(true);
                imageCopy.setCache(true);
                imageCopy.setId("detailsImage" + detailsImageCount);
                imageCopy.setOnMouseClicked(imagView.getOnMouseClicked());
                copiedChild = imageCopy;
                detailsImageCount++;
            }

            if (copiedChild != null) {
                Insets margin = HBox.getMargin(child);
                copy.getChildren().add(copiedChild);
                if (margin != null) {
                    HBox.setMargin(copiedChild, margin);
                }
            }
        }

        return copy;
    }

    public static void shuttleEntryLoadVBoxes(ArrayList<? extends Manage > data, VBox entryVBox){
        for (int i = 0; i < data.size() - 1; i++) {
            ObservableList<Node> vBox = entryVBox.getChildren();
            entryVBox.getChildren().add(Util.copyVBox((VBox) vBox.get(0)));
        }
        int counter = 0;
        ObservableList<Node> vBox = entryVBox.getChildren();
        for (Node node : vBox) {
            if (node.getClass() == VBox.class) {
                for (Node n : ((VBox) node).getChildren()){
                    if (n.getClass() == HBox.class) {
                        Manage s = data.get(counter);
                        HBox hBox = (HBox) n;
                        for (int i = 0; i < hBox.getChildren().size(); i++) {
                            if(hBox.getChildren().get(i).getClass() == Text.class) {
                                Text text = (Text) hBox.getChildren().get(i);
                                text.setText(s.getByI(i));
                            }
                        }
                        counter++;
                    }
                }
            }
        }
    }
}
