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

    public void createCheckBox(String title) {
        checkBox = new CheckBox(title);
        Tooltip checkBoxTooltip = new Tooltip(title);
        checkBoxTooltip.setFont(new Font("Arial", 16));
        checkBox.setTooltip(checkBoxTooltip);
        checkBox.setOnAction(evt -> {
            selected = checkBox.isSelected();
            selectOrDeselect(selected, episodeCheckBoxes);
        });
        StyleClasses.addStyle(checkBox, StyleClasses.CHECKBOX);
    }

    private void selectOrDeselect(boolean selected, Map<String, EpisodeCheckBox> episodeCheckBoxes) {
        episodeCheckBoxes.values().forEach(episodeCheckBoxList -> {
            episodeCheckBoxList.getCheckBox().setSelected(selected);
            episodeCheckBoxList.setSelected(selected);
            if (!episodeCheckBoxList.getEpisodeCheckBoxes().isEmpty()) {
                selectOrDeselect(selected, episodeCheckBoxList.getEpisodeCheckBoxes());
            }
        });
    }

    public void addToggleButton(ToggleButton toggleButton) {
        checkBox.setGraphic(toggleButton);
    }
}
