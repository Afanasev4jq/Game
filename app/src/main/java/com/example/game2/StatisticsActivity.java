package com.example.game2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StatisticsActivity extends Activity {

    private ListView listView;
    private ArrayAdapter<GameStat> adapter;
    private List<GameStat> gameStats;
    private boolean isSorted = false;
    private List<GameStat> originalGameStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        Button btnLogout = findViewById(R.id.btn_logout);
        listView = findViewById(R.id.list);
        gameStats = getDataFromDatabase();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gameStats);
        listView.setAdapter(adapter);
        Button sortButton = findViewById(R.id.btn_sort);
        originalGameStats = new ArrayList<>(gameStats);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionManager.getInstance().logout(); // Вызываем метод разлогинивания
                goToLoginActivity();
            }
        });

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSorted) {
                    // Сортировка статистики по убыванию
                    Collections.sort(gameStats, new Comparator<GameStat>() {
                        public int compare(GameStat stat1, GameStat stat2) {
                            return Integer.compare(stat2.getScore(), stat1.getScore());
                        }
                    });
                    isSorted = true;
                } else {
                    // Восстановление исходного списка
                    gameStats.clear();
                    gameStats.addAll(originalGameStats);
                    isSorted = false;
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    private List<GameStat> getDataFromDatabase() {
        DBHelper dbHelper = new DBHelper(this);
        return new ArrayList<>(dbHelper.getAllGameStats());
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(StatisticsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Закрываем текущую активити
    }
}