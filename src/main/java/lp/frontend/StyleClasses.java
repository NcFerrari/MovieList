package lp.frontend;

import javafx.scene.Node;
import lombok.Getter;

@Getter
public enum StyleClasses {

    PANE("pane"),
    EPISODE_PANE("episode-pane"),
    MENU("menu"),
    MENU_BUTTON("menu-button"),
    SELECTED("selected"),
    CHECKBOX("checkbox");

    private final String className;

    StyleClasses(String className) {
        this.className = className;
    }

    public static void addStyle(Node node, StyleClasses style) {
        node.getStyleClass().add(style.getClassName());
    }
}
