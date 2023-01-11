package com.example.here;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;

public class TrainingActivity extends AppCompatActivity {

    private Button endButton;
    private Button pauseReturnButton;
    private TextView curSpeedTextView;
    private TextView avgSpeedTextView;
    private TextView maxSpeedTextView;
    private TextView distanceTextView;
    private TextView timeTextView;
    private TextView kcalTextView;
    private MapView map;

    private double cordX = 53.40662040792659,
        cordY = 23.504240407268576;
    private int timeInSec;
    private boolean activated = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_activity);
        this.map = findViewById(R.id.map_view);
        this.endButton = findViewById(R.id.end_trening);
        this.pauseReturnButton = findViewById(R.id.return_pause_training);
        this.timeTextView = findViewById(R.id.time);
        this.curSpeedTextView = findViewById(R.id.cur_speed);
        this.avgSpeedTextView = findViewById(R.id.avg_speed);
        this.maxSpeedTextView = findViewById(R.id.max_speed);
        this.distanceTextView = findViewById(R.id.distance);
        this.kcalTextView = findViewById(R.id.kcal);


        this.curSpeedTextView.setText("21 km/h");
        this.avgSpeedTextView.setText("Średnia prędkość: 37 km/h");
        this.maxSpeedTextView.setText("Maksymalna prędkość: 69 km/h");
        this.distanceTextView.setText("Dystans: 0m");
        this.kcalTextView.setText("Spalone kalorie: 100kcal");


        this.pauseReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activated){
                    pauseReturnButton.setText(R.string.paused);
                    activated=!activated;
                    pauseReturnButton.setBackgroundColor(getResources().getColor(R.color.orange));
                }else{
                    pauseReturnButton.setText(R.string.started);
                    activated=!activated;
                    pauseReturnButton.setBackgroundColor(getResources().getColor(R.color.green));
                }
            }
        });
        this.endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrainingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        this.map.onCreate(savedInstanceState);
        loadMapScene();
        new TimeMeasure().start();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        this.map.onSaveInstanceState(outState);
    }

    private void loadMapScene() {
        map.getMapScene().loadScene(MapScheme.NORMAL_DAY, mapError -> {
            if (mapError == null) {
                MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, 1000);
                map.getCamera().lookAt(
                        new GeoCoordinates(cordX, cordY), mapMeasureZoom);
            } else {
                Log.d("loadMapScene()", "Loading map failed: mapError: " + mapError.name());
            }
        });
    }

    private class TimeMeasure extends Thread{
        @Override
        public void run() {
            try {
                while(true)
                    if(activated){
                        timeInSec++;
                        timeTextView.setText("Czas: "+String.format("%02d:%02d:%02d",timeInSec/3600,timeInSec%3600/60, timeInSec%60));
                        sleep(1000);
                    }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}