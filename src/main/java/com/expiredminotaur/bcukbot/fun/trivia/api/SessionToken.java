package com.expiredminotaur.bcukbot.fun.trivia.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SessionToken
{

    @SerializedName("response_code")
    @Expose
    private Integer responseCode;
    @SerializedName("response_message")
    @Expose
    private String responseMessage;//Token Generated Successfully!
    @SerializedName("token")
    @Expose
    private String token;

    public Integer getResponseCode()
    {
        return responseCode;
    }

    public String getResponseMessage()
    {
        return responseMessage;
    }

    public String getToken()
    {
        return token;
    }
}