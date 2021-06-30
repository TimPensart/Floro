package com.example.floro;

import java.util.ArrayList;

public class SeedsNotPlanted {
    private static SeedsNotPlanted instance = null;

    public ArrayList<Seed> seedsNotPlantedList = new ArrayList<Seed>();

    public static synchronized SeedsNotPlanted getInstance() {
        if(null == instance){
            instance = new SeedsNotPlanted();
        }
        return instance;
    }

    public SeedsNotPlanted() {
    }

    public ArrayList<Seed> getSeedsNotPlantedList() {
        return seedsNotPlantedList;
    }
}
