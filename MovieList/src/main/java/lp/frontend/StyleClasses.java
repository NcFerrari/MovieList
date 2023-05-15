package lp.frontend;

import lombok.Getter;

@Getter
public enum StyleClasses {

    PANE("pane"),
    MENU("menu"),
    MENU_BUTTON("menu-button"),
    SELECTED("selected"),
    CHECKBOX("checkbox");

    private String className;

    StyleClasses(String className) {
        this.className = className;
    }
}
