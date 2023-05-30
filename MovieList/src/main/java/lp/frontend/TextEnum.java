package lp.frontend;

public enum TextEnum {

    IMPORT_FILE("IMPORT.json"),
    DEFAULT_ROOT_PATH("C://Users/lpesek/Desktop/Test"),
    FILE_NOT_FOUND_TITLE("Chybí soubor"),
    FILE_NOT_FOUND_MESSAGE("Zadejte cestu, k adresáři, od kterého se mají načíst rekurzivně soubory"),
    ADDITIONAL_TEXT_EMPTY_STRING("\n\nPRÁZDNÝ ŘETĚZEC!"),
    ADDITIONAL_TEXT_FILE_NOT_FOUND("\n\nADRESÁŘ NEEXISTUJE!"),
    APPLICATION_TITLE("Seznam filmů"),
    RESOURCES("/css/fx-component.css"),
    OPENED_SUB_LIST("v"),
    CLOSED_SUB_LIST(">"),
    CLEAR_SELECTED_BUTTON_TEXT("Reset vybranných položek"),
    EXPORT_ITEMS("Vygenerovat soubor vybranných položek"),
    COPY_FILES("Zkopíruj vybrané položky"),
    RESET_SELECTED_TITLE("Resetovat vybrané?"),
    RESET_SELECTED_QUESTION("Opravdu si přeješ resetovat celý výběr?");

    private String text;

    TextEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
