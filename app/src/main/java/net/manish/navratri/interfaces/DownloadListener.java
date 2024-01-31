package net.manish.navratri.interfaces;

public interface DownloadListener {
    void onStart();
    void onProgressUpdate(int progress);
    void onEnd(String success);
}