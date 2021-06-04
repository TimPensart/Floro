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

    private String challengeTitle;
    private String prijs1;
    private String prijs2;
    private String prijs3;


    public Challenge(String _challenge, String _p1, String _p2, String _p3) {
        challengeTitle = _challenge;
        prijs1 = _p1;
        prijs2 = _p2;
        prijs3 = _p3;
    }



}

