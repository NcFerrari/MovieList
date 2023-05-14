package lp.serviceimpl;

import lp.frontend.Dialogs;
import lp.service.DialogService;

import java.util.Optional;

public class DialogServiceImpl implements DialogService {

    private static DialogServiceImpl dialogService;

    private DialogServiceImpl() {

    }

    public static DialogServiceImpl getInstance() {
        if (dialogService == null) {
            dialogService = new DialogServiceImpl();
        }
        return dialogService;
    }

    @Override
    public String getInputDialog(String title, String message) {
        Dialogs.setTitle(title);
        Dialogs.setMessage(message);
        javafx.application.Application.launch(Dialogs.class);
        return Dialogs.getInputResult();
    }
}
