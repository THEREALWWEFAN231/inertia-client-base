package com.inertiaclient.base.utils;

public interface LanguageBaseKey {

    String getLanguageBaseKey();

    default String baseKeyWithParameter(String parameter) {
        String baseKey = this.getLanguageBaseKey();
        return baseKey + "." + parameter;
    }

}
