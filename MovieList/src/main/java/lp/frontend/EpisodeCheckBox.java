package lp.frontend;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class EpisodeCheckBox {
    private CheckBox checkBox;
    private boolean selected;
    private double scrollPanePosition;
    private Map<String, EpisodeCheckBox> episodeCheckBoxes = new LinkedHashMap<>();

    public EpisodeCheckBox(String title) {
        checkBox = new CheckBox(title);
        Tooltip checkBoxTooltip = new Tooltip(title);
        checkBoxTooltip.setFont(new Font("Arial", 16));
        checkBox.setTooltip(checkBoxTooltip);
        checkBox.setOnAction(evt -> selected = checkBox.isSelected());
        StyleClasses.addStyle(checkBox, StyleClasses.CHECKBOX);
    }

    public void addToggleButton(ToggleButton toggleButton) {
        checkBox.setGraphic(toggleButton);
    }
}
