package com.example.demo.http.callback;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class ClientCallBackImpl implements ClientCallBack{
    @Override
    public String onFailure(IOException e){
        if(e instanceof SocketTimeoutException){
            System.out.println("Request got timed out");
        }
        else{
            if(e.getMessage() == "Canceled")
                System.out.println("Request got canceled");
            else{
                e.printStackTrace();
            }
        }
        return "IOException";
    }

    @Override
    public String onResponse(String response){
        return response;
    }
    
}
