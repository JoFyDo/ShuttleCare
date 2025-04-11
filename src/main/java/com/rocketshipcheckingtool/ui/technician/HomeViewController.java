package com.rocketshipcheckingtool.ui.technician;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class HomeViewController {
    public void mouseClicked(MouseEvent mouseEvent) {
        System.out.println(mouseEvent.getButton());
        System.out.println(mouseEvent.getClickCount());
        System.out.println("Clicked");
    }

    public void detailsClicked(MouseEvent mouseEvent) {
        System.out.println(mouseEvent.getButton());
        System.out.println(((Node) mouseEvent.getSource()).getId());
    }
}
