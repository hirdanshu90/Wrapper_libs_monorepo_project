package com.example.demo.http.progress;

interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
