package com.example.floro;

import android.util.Log;

import java.util.ArrayList;

public class SeedsList {
    private static SeedsList instance = null;

    public ArrayList<Seed> seedsList = new ArrayList<Seed>();

    public static synchronized SeedsList getInstance() {
        if(null == instance){
            instance = new SeedsList();
        }
        return instance;
    }

    public SeedsList() {
    }

    public ArrayList<Seed> getSeedsList() {
        return seedsList;
    }

}
