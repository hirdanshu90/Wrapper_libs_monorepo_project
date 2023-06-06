package com.accenture.goss.http.progress;

/**
 * Class Implementation of ProgressListener.
 * Prints percentage completion of a request, if content length is known.
 * <p>
 * Taken from OkHttp recipes on Github
 * @see <a href="https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java">Progress Recipe</a>
 */
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
