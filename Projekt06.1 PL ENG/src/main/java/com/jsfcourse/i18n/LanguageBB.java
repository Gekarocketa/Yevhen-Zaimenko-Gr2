package com.jsfcourse.i18n;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Locale;

@Named
@SessionScoped
public class LanguageBB implements Serializable {
    
    private String currentLanguage = "pl";
    
    public String getCurrentLanguage() {
        return currentLanguage;
    }
    
    public void setCurrentLanguage(String currentLanguage) {
        this.currentLanguage = currentLanguage;
    }
    
    public void changeLanguage(String lang) {
        currentLanguage = lang;
        Locale locale = new Locale(lang);
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx.getViewRoot() != null) {
            ctx.getViewRoot().setLocale(locale);
        }
    }
    
    public String switchToPolish() {
        changeLanguage("pl");
        return null;
    }
    
    public String switchToEnglish() {
        changeLanguage("en");
        return null;
    }
    
    public boolean isPolish() {
        return "pl".equals(currentLanguage);
    }
    
    public boolean isEnglish() {
        return "en".equals(currentLanguage);
    }
}

