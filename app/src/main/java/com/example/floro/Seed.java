package com.example.floro;

public class Seed {
    private String plantName;
    private int plantResourceImage;

    public String getPlantName() {
        return plantName;
    }

    public int getPlantResourceImage() {
        return plantResourceImage;
    }

    public Seed(String _plantName, int _plantResourceImage) {
        plantName = _plantName;
        plantResourceImage = _plantResourceImage;
    }
}
