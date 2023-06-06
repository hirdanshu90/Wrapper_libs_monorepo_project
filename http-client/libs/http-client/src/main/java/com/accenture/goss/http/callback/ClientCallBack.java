package com.accenture.goss.http.callback;

import java.io.IOException;

public interface ClientCallBack {
    public String onFailure(IOException e);
    public String onResponse(String response);
}
