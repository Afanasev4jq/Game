package com.example.game2;

import android.graphics.Bitmap;


import android.graphics.Bitmap;

public class CharacterSkin {
    private Bitmap skinImage;
    private String skinName;

    public CharacterSkin(Bitmap skinImage, String skinName) {
        this.skinImage = skinImage;
        this.skinName = skinName;
    }

    public Bitmap getSkinImage() {
        return skinImage;
    }

    public String getSkinName() {
        return skinName;
    }
}
