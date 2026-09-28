package com.example.Cortex.listener;


public interface Callback {
    
    void onSuccess(String json);
    void onError(String error);
}
