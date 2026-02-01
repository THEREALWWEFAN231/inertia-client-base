package com.inertiaclient.base.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
public class Friend implements JsonState {

    private String username;
    @Nullable
    private UUID uuid;

    public Friend(String username, @Nullable UUID uuid) throws IllegalArgumentException {
        this.setUsername(username);
        this.uuid = uuid;
    }

    private Friend() {

    }

    private boolean isValidUsername(String username) {
        if (username.length() > 16) {
            return false;
        }
        return true;
    }

    private void setUsername(String username) {
        if (!this.isValidUsername(username)) {
            throw new IllegalArgumentException(String.format("Username %s is not a valid username", username));
        }
        this.username = username;
    }

    @Override
    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", this.username);
        if (this.uuid != null) {
            jsonObject.addProperty("uuid", this.uuid.toString());
        }
        return jsonObject;
    }

    @Override
    public void fromJson(JsonElement data) {
        JsonObject friendObject = data.getAsJsonObject();

        this.setUsername(friendObject.get("username").getAsString());
        if (friendObject.has("uuid")) {
            this.uuid = UUID.fromString(friendObject.get("uuid").getAsString());
        }
    }

    public static Friend makeFromJson(JsonElement data) {
        Friend friend = new Friend();
        friend.fromJson(data);

        return friend;
    }

}
