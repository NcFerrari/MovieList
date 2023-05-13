package lp.service;

import java.util.Optional;

public interface DialogService {

    Optional<String> getInputDialog(String title, String message);
}
