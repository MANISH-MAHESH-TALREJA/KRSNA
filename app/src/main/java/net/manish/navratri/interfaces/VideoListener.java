package net.manish.navratri.interfaces;

import net.manish.navratri.item.ItemMessage;
import net.manish.navratri.item.ItemVideos;

import java.util.ArrayList;

public interface VideoListener
{
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemVideos> arrayListVideos);
}