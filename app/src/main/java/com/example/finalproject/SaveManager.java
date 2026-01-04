package com.example.finalproject;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Cleaned SaveManager for the towerdefence project.
 * Handles persistent storage for coins and wave progress.
 */
public class SaveManager {
    private static final String PREF = "td_save";

    /**
     * Saves the current game state to SharedPreferences.
     */
    public static void saveState(Context ctx, int coins, int wave) {
        SharedPreferences p = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = p.edit();
        e.putInt("coins", coins);
        e.putInt("wave", wave);
        e.apply();
    }

    /**
     * Loads the saved game state.
     * Returns an array where index 0 is coins and index 1 is the wave number.
     */
    public static int[] loadState(Context ctx) {
        SharedPreferences p = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        // Uses GameConfig.START_COINS as a fallback if no save exists
        int coins = p.getInt("coins", GameConfig.START_COINS);
        int wave = p.getInt("wave", 0);
        return new int[]{coins, wave};
    }

    /**
     * Clears all saved data (useful for a 'New Game' feature).
     */
    public static void clearSave(Context ctx) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().clear().apply();
    }
}