package com.example.floro;

import java.util.ArrayList;

public class ChallengesList {

    private ArrayList<Object> challengesList = new ArrayList<Object>();

    public ChallengesList() {
        this.challengesList.add(new Challenge("Vind je allereerste bloem", "2", "9", "9"));
        this.challengesList.add(new ChallengeWithPicture("Vind een boterbloem",
                "https://images.pexels.com/photos/4460670/pexels-photo-4460670.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
                "2",
                "80",
                "80"));
        this.challengesList.add(new ChallengeWithPicture(
                "vind je eerste zonnebloem",
                "https://images.pexels.com/photos/6429840/pexels-photo-6429840.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260",
                "1",
                "100",
                "100"
        ));
    }

    public ArrayList<Object> getChallengesList() {
        return challengesList;
    }
}
