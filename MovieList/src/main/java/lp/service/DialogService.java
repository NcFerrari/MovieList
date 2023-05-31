package lp.service;

import javafx.scene.control.ButtonType;

import java.util.Optional;

public interface DialogService {

    void useTextInputDialog(String title, String message);

    Optional<ButtonType> useConfirmDialog(String title, String message);

    void useInformationDialog(String title, String message);
}
