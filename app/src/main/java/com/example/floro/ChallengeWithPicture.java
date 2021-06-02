package com.example.floro;

import android.media.Image;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChallengeWithPicture {
    public String getChallengeTitle() {
        return challengeTitle;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getImageURL() {
        return imageURL;
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
    private int imageResource;
    private String imageURL;
    private String prijs1;
    private String prijs2;
    private String prijs3;


    public ChallengeWithPicture(String _challenge, String _url, String _p1, String _p2, String _p3) {
        challengeTitle = _challenge;
        imageURL = _url;
        prijs1 = _p1;
        prijs2 = _p2;
        prijs3 = _p3;
    }

    public ChallengeWithPicture(String _challenge, int _imageResource, String _p1, String _p2, String _p3) {
        challengeTitle = _challenge;
        imageResource = _imageResource;
        prijs1 = _p1;
        prijs2 = _p2;
        prijs3 = _p3;
    }


}
