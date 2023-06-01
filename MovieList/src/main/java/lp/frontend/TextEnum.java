package lp.frontend;

public enum TextEnum {

    IMPORT_FILE("IMPORT.json"),
    EXPORT_FILE("EXPORTED.json"),
    DEFAULT_ROOT_PATH("C://Users/lpesek/Desktop/Test"),
    FILE_PATH_TITLE("Zadejte soubor"),
    FILE_PATH_MESSAGE("Zadejte cestu, k adresáři, od kterého se mají načíst rekurzivně soubory"),
    ADDITIONAL_TEXT_EMPTY_STRING("\n\nPRÁZDNÝ ŘETĚZEC!"),
    ADDITIONAL_TEXT_FILE_NOT_FOUND("\n\nADRESÁŘ NEEXISTUJE!"),
    APPLICATION_TITLE("Seznam filmů"),
    RESOURCES("/css/fx-component.css"),
    OPENED_SUB_LIST("v"),
    CLOSED_SUB_LIST(">"),
    CLEAR_SELECTED_BUTTON_TEXT("Reset vybranných položek"),
    EXPORT_ITEMS("Vygenerovat soubor vybranných položek"),
    COPY_FILES("Zkopíruj vybrané položky"),
    RESET_SELECTED_TITLE("Resetovat vybrané položky?"),
    RESET_SELECTED_QUESTION("Opravdu si přeješ resetovat celý výběr?"),
    YES_TEXT("Ano"),
    NO_TEXT("Ne"), SUCCESS_TITLE("Úspěšně exportováno"),
    OK_TEXT("Ok"),
    SUCCESS_EXPORT("HOTOVO!\nGenerování souboru " + EXPORT_FILE.getText() + " se podařilo."),
    ERROR_TITLE("Chybová hláška"),
    ERROR_MESSAGE("No ty koki! Něco se posralo a je třeba to řešit. Následující chybu zkopíruj a pošli Lubovi\n\n"),
    INTRODUCE_DIALOG_TITLE("Nutný soubor"),
    INTRODUCE_DIALOG_TEXT("Je potřeba soubor " + IMPORT_FILE.getText()),
    INTRODUCE_DIALOG_PASSWORD("SETFILE");

    private String text;

    TextEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
