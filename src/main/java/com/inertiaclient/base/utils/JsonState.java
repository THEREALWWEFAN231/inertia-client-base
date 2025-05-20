package com.inertiaclient.base.utils;

import com.google.gson.JsonElement;

public interface JsonState {

    JsonElement toJson();

    void fromJson(JsonElement data);

}
