package com.rocketshipcheckingtool.ui.technician;

import com.sun.javafx.geom.Dimension;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SizeUtil {



    public static Dimension minDimStackPane(StackPane pane, double widthMargin) {
        double headerWidth = 0;
        double entryWidth = 0;
        int multiplierHeader = 0;
        int multiplierEntry = 0;
        for (Node node : pane.getChildren()) {
            if (node.getClass() == VBox.class) {
                VBox vBox = (VBox) node;
                for (Node no : vBox.getChildren()) {
                    if (no.getClass() == HBox.class) {
                        HBox hBox = (HBox) no;
                        for (Node n : hBox.getChildren()) {
                            if (n.getClass() == Text.class) {
                                multiplierHeader++;
                                Text text = (Text) n;
                                headerWidth += text.getBoundsInLocal().getWidth();
                            }
                        }
                    } else if (no.getClass() == VBox.class) {
                        VBox vBox1 = (VBox) no;
                        System.out.println(vBox1.getBoundsInLocal().getHeight());
                        for (Node n : vBox1.getChildren()) {
                            if (n.getClass() == VBox.class) {
                                VBox vBox2 = (VBox) n;
                                for (Node n2 : vBox2.getChildren()) {
                                    double tmpw = 0;
                                    if (n2.getClass() == HBox.class) {
                                        HBox hBox = (HBox) n2;
                                        for (Node n3 : hBox.getChildren()) {
                                            if (n3.getClass() == Text.class) {
                                                Text text = (Text) n3;
                                                tmpw += text.getBoundsInLocal().getWidth();
                                                multiplierEntry++;
                                            }
                                            if (n3.getClass() == ImageView.class) {
                                                ImageView imageView = (ImageView) n3;
                                                tmpw += imageView.getFitWidth();
                                                multiplierEntry++;
                                            }
                                        }
                                    }
                                    if (tmpw > entryWidth) {
                                        entryWidth = tmpw;
                                    }
                                    tmpw = 0;
                                }
                            }
                        }
                    }
                }
            }
        }
        headerWidth += headerWidth + ((multiplierHeader - 1) * widthMargin);
        entryWidth += entryWidth + ((multiplierEntry - 1) * widthMargin);
        System.out.println(headerWidth);
        System.out.println(entryWidth);
        return null;
    }

    public static void resizeStackPane(StackPane pane, double width, double height) {
        pane.setPrefWidth(width);
        pane.setPrefHeight(height);

        double margin = 0;

        for (Node node : pane.getChildren()) {
            if (node.getClass() == VBox.class) {
                VBox vBox = (VBox) node;
                for (Node no : vBox.getChildren()) {
                    if (no.getClass() == HBox.class) {
                        HBox hBox = (HBox) no;
                        margin = (width - getWidthHBox(hBox)) / hBox.getChildren().size();
                        for (Node n : hBox.getChildren()) {
                            if (n.getClass() == Text.class) {
                                System.out.println(HBox.getMargin(n));
                                if (HBox.getMargin(n) != null) {
                                    Insets insets = HBox.getMargin(n);
                                    HBox.setMargin(n, new Insets(insets.getTop(), insets.getRight(), insets.getBottom(), margin));
                                    System.out.println(HBox.getMargin(n));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static double getWidthHBox(HBox hBox) {
        double width = 0;
        for (Node n : hBox.getChildren()) {
            if (n.getClass() == Text.class) {
                Text text = (Text) n;
                width += text.getBoundsInLocal().getWidth();
            }
        }
        return width;
    }
}
