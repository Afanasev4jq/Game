package com.example.game2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.game2.GameView;
import com.example.game2.R;
import com.example.game2.StatisticsActivity;

public class MainActivity extends Activity {

    private GameView gameView;
    private ImageView btnStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        gameView = new GameView(this, size.x, size.y);

        setContentView(R.layout.activity_main);
        FrameLayout gameFrame = findViewById(R.id.game_frame);
        gameFrame.addView(gameView);

        // Инициализация ImageView с кнопкой статистики
        btnStatistics = findViewById(R.id.btn_statistics);
        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }
}