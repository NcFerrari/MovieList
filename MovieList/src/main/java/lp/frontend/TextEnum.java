package lp.frontend;

public enum TextEnum {

    IMPORT_FILE("IMPORT.json"),
    DEFAULT_ROOT_PATH("C://Users/lpesek/Desktop/Test"),
    FILE_NOT_FOUND_TITLE("Chybí soubor"),
    FILE_NOT_FOUND_MESSAGE("Zadejte cestu, k adresáři, od kterého se mají načíst rekurzivně soubory"),
    ADDITIONAL_TEXT_EMPTY_STRING("\n\nPRÁZDNÝ ŘETĚZEC!"),
    ADDITIONAL_TEXT_FILE_NOT_FOUND("\n\nADRESÁŘ NEEXISTUJE!"),
    APPLICATION_TITLE("Seznam filmů"),
    RESOURCES("/css/fx-component.css");

    private String text;

    TextEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
