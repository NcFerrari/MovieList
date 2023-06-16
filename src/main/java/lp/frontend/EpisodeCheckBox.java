package lp.frontend;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import lombok.Getter;
import lombok.Setter;
import lp.Manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class EpisodeCheckBox {

    //=======================ATTRIBUTES========================
    private final List<EpisodeCheckBox> episodeParents = new ArrayList<>();
    private final Map<String, EpisodeCheckBox> episodeCheckBoxes = new LinkedHashMap<>();
    private int[] countOfEpisodes = new int[]{0};

    private CheckBox checkBox;
    private long size;
    private double scrollPanePosition;
    private ToggleButton toggleButton;
    //=======================ATTRIBUTES========================


    //=======================METHODS===========================
    public void createCheckBox(String title, InfoLabel infoLabel) {
        checkBox = new CheckBox(title);
        Tooltip checkBoxTooltip = new Tooltip(title);
        checkBoxTooltip.setFont(new Font("Arial", 16));
        checkBox.setTooltip(checkBoxTooltip);
        checkBox.setOnAction(evt -> {
            if (checkBox.getGraphic() == null ||
                    (checkBox.getGraphic() != null && !checkBox.getGraphic().isFocused())) {
                selectOrDeselect(checkBox.isSelected(), episodeCheckBoxes);
                setParentSelection();
                infoLabel.updateCounter(Manager.getInstance().getSelectedCount());
            }
        });
        StyleClasses.addStyle(checkBox, StyleClasses.CHECKBOX);
    }

    public void addToggleButton() {
        toggleButton = new ToggleButton(TextEnum.CLOSED_SYMBOL_FOR_TOGGLE_BUTTON.getText());
        checkBox.setGraphic(toggleButton);
    }
    //=======================METHODS===========================


    //=======================RETURN METHODS====================
    //=======================RETURN METHODS====================


    //=======================PRIVATE METHODS===================
    private void setParentSelection() {
        for (int i = episodeParents.size() - 1; i > 0; i--) {
            countOfEpisodes[0] = 0;
            for (EpisodeCheckBox episodeCheckBox : episodeParents.get(i).getEpisodeCheckBoxes().values()) {
                if (episodeCheckBox.getCheckBox().isSelected()) {
                    countOfEpisodes[0] = countOfEpisodes[0] + 1;
                } else {
                    break;
                }
            }
            if (countOfEpisodes[0] == episodeParents.get(i).getEpisodeCheckBoxes().size()) {
                episodeParents.get(i).getCheckBox().setSelected(true);
                episodeParents.get(i).getCheckBox().setSelected(true);
            } else {
                episodeParents.get(i).getCheckBox().setSelected(false);
                episodeParents.get(i).getCheckBox().setSelected(false);
            }
        }
    }

    private void selectOrDeselect(boolean selected, Map<String, EpisodeCheckBox> episodeCheckBoxes) {
        episodeCheckBoxes.values().forEach(episodeCheckBoxList -> {
            episodeCheckBoxList.getCheckBox().setSelected(selected);
            episodeCheckBoxList.getCheckBox().setSelected(selected);
            if (!episodeCheckBoxList.getEpisodeCheckBoxes().isEmpty()) {
                selectOrDeselect(selected, episodeCheckBoxList.getEpisodeCheckBoxes());
            }
        });
    }
    //=======================PRIVATE METHODS===================
}
