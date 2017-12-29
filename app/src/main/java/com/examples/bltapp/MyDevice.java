package com.examples.bltapp;

import org.json.JSONException;
import org.json.JSONObject;

public class MyDevice {
    private String name;
    private String address;
    private String password;

    public MyDevice(String name, String address, String password) {
        this.name = name;
        this.address = address;
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String toJson(){

        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("name", getName());
            jsonObject.put("address", getAddress());
            jsonObject.put("password", getPassword());

            return jsonObject.toString();
        } catch (JSONException e) {
            // Auto-generated catch block
            e.printStackTrace();
            return "";
        }

    }
}
