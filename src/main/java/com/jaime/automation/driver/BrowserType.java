package com.jaime.automation.driver;
/*
* We use this ENUM to normalize the browser name and set Chrome as the default browser.
*/
public enum BrowserType {
    CHROME, FIREFOX;

    public static BrowserType from(String value) {
        if (value == null) return CHROME; //Si no se envía nada, se devuelve CHROME
        switch (value.trim().toLowerCase()) {
            case "firefox": return FIREFOX;
            default: return CHROME; //CHROME como navegador por defecto
        }
    }
}
