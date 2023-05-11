package com.example.demo.http.progress;

public class ProgressListenerImpl implements ProgressListener {
    boolean firstUpdate = true;

    @Override public void update(long bytesRead, long contentLength, boolean done) {
    if (done) {
        System.out.println("completed");
    } else {
        if (firstUpdate) {
        firstUpdate = false;
        if (contentLength == -1) {
            System.out.println("content-length: unknown");
        } else {
            System.out.format("content-length: %d\n", contentLength);
        }
        }

        System.out.println("bytesRead: " + bytesRead);

        if (contentLength != -1) {
        System.out.format("%d%% done\n", (100 * bytesRead) / contentLength);
        }
    }
    }
}
