package net.manish.navratri.interfaces;

import net.manish.navratri.item.ItemQuiz;
import net.manish.navratri.item.ItemWallpaper;

import java.util.ArrayList;

public interface QuizListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemQuiz> arrayListQuiz);
}