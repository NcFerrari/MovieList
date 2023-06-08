package lp.frontend;

import javafx.scene.control.Label;

public class InfoLabel {

    //=======================ATTRIBUTES========================
    private Label label;
    //=======================ATTRIBUTES========================


    //=======================METHODS===========================
    public void updateCounter(long[] counterAndSize) {
        String inflectionText;
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
        label.setText(String.format("%s %s %s (%s)",
                TextEnum.TOTAL_SELECTED_TEXT_PREFIX.getText(),
                counterAndSize[0],
                inflectionText,
                unitConversion(counterAndSize[1])));
    }
    //=======================METHODS===========================


    //=======================RETURN METHODS====================
    public Label getLabel() {
        if (label == null) {
            label = new Label();
        }
        return label;
    }
    //=======================RETURN METHODS====================


    //=======================PRIVATE METHODS===================
    private String unitConversion(double byteSize) {
        double result = byteSize;
        int numberOfDividing = 0;
        while (result > 1024) {
            numberOfDividing++;
            result = result / 1024;
        }
        String unit;
        switch (numberOfDividing) {
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
                break;
            default:
                unit = "B";
        }
        return String.format("%.2f %s", result, unit);
    }
    //=======================PRIVATE METHODS===================

}
