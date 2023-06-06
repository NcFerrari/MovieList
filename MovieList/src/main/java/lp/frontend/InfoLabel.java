package lp.frontend;

import javafx.scene.control.Label;

public class InfoLabel {

    private final Label label = new Label();

    public Label getLabel() {
        return label;
    }

    public void updateCounter(long[] counterAndSize) {
        String inflectionText = "";
        switch ((int) counterAndSize[0]) {
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
        label.setText(String.format("%s %s %s (%s)", TextEnum.TOTAL_SELECTED_TEXT_PREFIX.getText(), counterAndSize[0], inflectionText, unitConversion(counterAndSize[1])));
    }

    private String unitConversion(double byteSize) {
        double result = byteSize;
        int numberOfDividing = 0;
        while (result > 1024) {
            numberOfDividing++;
            result = result / (double) 1024;
        }
        String unit = "";
        switch (numberOfDividing) {
            case 0:
                unit = "B";
                break;
            case 1:
                unit = "KB";
                break;
            case 2:
                unit = "MB";
                break;
            case 3:
                unit = "GB";
                break;
            case 4:
                unit = "TB";
        }
        return String.format("%.2f %s", result, unit);
    }

}
