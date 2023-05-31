package lp.service;

import javafx.scene.control.ButtonType;

import java.util.Optional;

public interface DialogService {

    void initTextInputDialog(String title, String message);

    Optional<ButtonType> useConfirmDialog(String title, String message);

    void useInformationDialog(String title, String message);
}
