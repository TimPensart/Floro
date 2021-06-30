package com.example.floro;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.strictmode.InstanceCountViolation;
import android.util.Log;

import java.util.ArrayList;

public class ChallengesList {

    private static ChallengesList instance = null;


    public ArrayList<Object> challengesList = new ArrayList<Object>();

    public ChallengesList() {
        Log.d("challengetest", "adding challenges");
        challengesList.add(new Challenge(
                "any",
                "Vind je allereerste plant",
                "1",
                "9",
                "9",
                new Seed("paardenbloem", R.drawable.ic_dandelion)));
        challengesList.add(new ChallengeWithPicture(
                "vergeet-mij-nietje",
                "Vind een vergeet-mij-nietje",
                "https://images.pexels.com/photos/60005/forget-me-not-flower-meadow-wild-flower-60005.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
                "1",
                "80",
                "80",
                new Seed("vergeet-mij-nietje", R.drawable.ic__14_alpine_forget_me_not)));
        challengesList.add(new ChallengeWithPicture(
                "papaver",
                "vind je eerste papaver",
                "https://media.vtwonen.nl/m/l0e5glf8zxa5.jpg",
                "1",
                "100",
                "100",
                new Seed("klaproos", R.drawable.ic_klaproos)
        ));
        challengesList.add(new ChallengeWithPicture(
                "margriet",
                "vind je eerste margriet",
                "https://images.pexels.com/photos/67857/daisy-flower-spring-marguerite-67857.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
                "1",
                "80",
                "120",
                new Seed("margriet", R.drawable.ic_daisy)
        ));
        challengesList.add(new ChallengeWithPicture(
                "vingerhoedskruid",
                "vind je eerste vingerhoedskruid",
                "https://cdn.shopify.com/s/files/1/0334/9679/3133/products/vingerhoedskruid-Digitalis-Purpurea_2000x2000.jpg?v=1615881372",
                "1",
                "100",
                "80",
                new Seed("vingerhoedskruid", R.drawable.ic_vingerhoedskruid)
        ));
        challengesList.add(new ChallengeWithPicture(
                "varen",
                "vind je eerste varen",
                "https://cdn.pixabay.com/photo/2020/04/15/11/13/fern-5046221_1280.jpg",
                "1",
                "300",
                "80",
                new Seed("varen", R.drawable.ic_varen)
        ));
        challengesList.add(new ChallengeWithPicture(
                "aardbei",
                "vind je eerste bosaardbei",
                "https://ecopedia.s3.eu-central-1.amazonaws.com/styles/colorbox-groot/sa/media/DSC_8731.JPG?itok=nLBv99gA",
                "1",
                "200",
                "100",
                new Seed("bosaardbei bloem", R.drawable.ic_strawberry_blossoms)
        ));
        challengesList.add(new ChallengeWithPicture(
                "gelderse roos",
                "vind je eerste gelderse roos",
                "https://bosennatuur.files.wordpress.com/2020/05/gelderse-roos-4.jpg",
                "1",
                "90",
                "90",
                new Seed("gelderse roos plant", R.drawable.ic_gelderse_roos)
        ));
    }

    public static synchronized ChallengesList getInstance() {
        if(null == instance){
            instance = new ChallengesList();
        }
        return instance;
    }

}
