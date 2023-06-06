package lp.frontend;

import javafx.scene.control.Label;

public class TotalSelectedCounter {

    private final Label label = new Label();

    public Label getLabel() {
        return label;
    }

    public void updateCounter(int counter) {
        String inflectionText = "";
        switch (counter) {
            case 1:
                inflectionText = TextEnum.TOTAL_SELECTED_TEXT_SUFFIX_FOR_ONE.getText();
                break;
            case 2:
            case 3:
            case 4:
                inflectionText = TextEnum.TOTAL_SELECTED_TEXT_SUFFIX_FOR_LOW.getText();
                break;
            default:
                inflectionText = TextEnum.TOTAL_SELECTED_TEXT_SUFFIX_DEFAULT.getText();
        }
        label.setText(TextEnum.TOTAL_SELECTED_TEXT_PREFIX.getText() + counter + inflectionText);
    }

}
