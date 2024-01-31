package net.manish.navratri.interfaces;

import net.manish.navratri.item.ItemWallpaper;

import java.util.ArrayList;

public interface WallpaperListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemWallpaper> arrayListWallpaper);
}