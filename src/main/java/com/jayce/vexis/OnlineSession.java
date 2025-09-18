package com.jayce.vexis;

import java.util.ArrayList;
import java.util.List;

public class OnlineSession {
    public String firstTimeCookie;
    public String sessionID;
    public List<String> loginTime;

    public OnlineSession(String firstTimeCookie, String sessionID) {
        this.firstTimeCookie = firstTimeCookie;
        this.sessionID = sessionID;
        this.loginTime = new ArrayList<>();
    }
}
