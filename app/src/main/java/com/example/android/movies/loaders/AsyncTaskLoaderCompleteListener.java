package com.example.android.movies.loaders;

public interface AsyncTaskLoaderCompleteListener<T> {

    public void onTaskComplete(T result);

    public void onTaskInitialisation();
}
