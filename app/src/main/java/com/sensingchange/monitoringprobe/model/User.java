package com.sensingchange.monitoringprobe.model;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    String email;
    String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JSONObject toJSON() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("string", this.email);
        jsonObject.put("string", this.password);

        return jsonObject;
    }
}