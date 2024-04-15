package com.example.game2;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.game2.GameView;
import com.example.game2.R;

public class CharacterSelectionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_selection);

        ImageView skin1 = findViewById(R.id.skin1);
        ImageView skin2 = findViewById(R.id.skin2);

        skin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSkinAndFinish((Bitmap) skin1.getTag());
            }
        });

        skin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSkinAndFinish((Bitmap) skin2.getTag());
            }
        });

        // Загружаем изображения скинов
        Bitmap skinImage1 = BitmapFactory.decodeResource(getResources(), R.drawable.img_1);
        skin1.setImageBitmap(skinImage1);
        skin1.setTag(skinImage1);

        Bitmap skinImage2 = BitmapFactory.decodeResource(getResources(), R.drawable.img_2);
        skin2.setImageBitmap(skinImage2);
        skin2.setTag(skinImage2);
    }

    private void selectSkinAndFinish(Bitmap selectedSkin) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SELECTED_SKIN", selectedSkin);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}