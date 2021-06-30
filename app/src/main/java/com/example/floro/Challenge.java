package com.example.floro;

import android.media.Image;

import de.hdodenhof.circleimageview.CircleImageView;

public class Challenge {
    public String getChallengeTitle() {
        return challengeTitle;
    }

    public String getPrijs1() {
        return prijs1;
    }

    public String getPrijs2() {
        return prijs2;
    }

    public String getPrijs3() {
        return prijs3;
    }

    public Seed getSeed() {
        return seed;
    }

    public String getPlantName() {
        return plantName;
    }

    private String plantName;

    private String challengeTitle;



    private String prijs1;
    private String prijs2;
    private String prijs3;

    private Seed seed;

    public Challenge(String _plantName, String _challenge, String _p1, String _p2, String _p3) {
        plantName = _plantName;
        challengeTitle = _challenge;
        prijs1 = _p1;
        prijs2 = _p2;
        prijs3 = _p3;
    }

    public Challenge(String _plantName, String _challenge, String _p1, String _p2, String _p3, Seed _seed) {
        plantName = _plantName;
        challengeTitle = _challenge;
        prijs1 = _p1;
        prijs2 = _p2;
        prijs3 = _p3;

        seed = _seed;
    }


}

