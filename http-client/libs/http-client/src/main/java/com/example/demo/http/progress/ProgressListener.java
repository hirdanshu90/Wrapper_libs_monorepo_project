package com.example.demo.http.progress;

/**
 * An object that updates the progress of a request
 * <p>
 * Taken from OkHttp recipes on Github
 * @see <a href="https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java">Progress Recipe</a>
 */
interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
