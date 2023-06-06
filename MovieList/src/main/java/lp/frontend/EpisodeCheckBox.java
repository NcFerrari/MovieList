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
    private CheckBox checkBox;
    private boolean selected;
    private double scrollPanePosition;
    private List<EpisodeCheckBox> episodeParents = new ArrayList<>();
    private Map<String, EpisodeCheckBox> episodeCheckBoxes = new LinkedHashMap<>();
    private long size;
    private int[] countOfEpisodes = new int[]{0};

    public void createCheckBox(String title, InfoLabel totalSelectedCounter) {
        checkBox = new CheckBox(title);
        Tooltip checkBoxTooltip = new Tooltip(title);
        checkBoxTooltip.setFont(new Font("Arial", 16));
        checkBox.setTooltip(checkBoxTooltip);
        checkBox.setOnAction(evt -> {
            if (checkBox.getGraphic() == null || (checkBox.getGraphic() != null && !checkBox.getGraphic().isFocused())) {
                setSelected(checkBox.isSelected());
                selectOrDeselect(selected, episodeCheckBoxes);
                setParentSelection();
                totalSelectedCounter.updateCounter(Manager.getInstance().getSelectedCount());
            }
        });
        StyleClasses.addStyle(checkBox, StyleClasses.CHECKBOX);
    }

    private void setParentSelection() {
        for (int i = episodeParents.size() - 1; i > 0; i--) {
            countOfEpisodes[0] = 0;
            for (EpisodeCheckBox episodeCheckBox : episodeParents.get(i).getEpisodeCheckBoxes().values()) {
                if (episodeCheckBox.isSelected()) {
                    countOfEpisodes[0] = countOfEpisodes[0] + 1;
                } else {
                    break;
                }
            }
            if (countOfEpisodes[0] == episodeParents.get(i).getEpisodeCheckBoxes().size()) {
                episodeParents.get(i).setSelected(true);
                episodeParents.get(i).getCheckBox().setSelected(true);
            } else {
                episodeParents.get(i).setSelected(false);
                episodeParents.get(i).getCheckBox().setSelected(false);
            }
        }
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
