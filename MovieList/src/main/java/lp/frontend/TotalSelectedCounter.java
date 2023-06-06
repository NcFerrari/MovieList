package lp.frontend;

import javafx.scene.control.Label;

public class TotalSelectedCounter {

    private final Label label = new Label();

    public Label getLabel() {
        return label;
    }

    public void updateCounter(int counter) {
        label.setText(TextEnum.TOTAL_SELECTED_TEXT_PREFIX.getText() + counter + TextEnum.TOTAL_SELECTED_TEXT_SUFFIX.getText());
    }

}
