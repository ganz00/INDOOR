package com.simutech.landry.stechmappingindoor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

public class Ponctuel extends AppCompatActivity {
    public TextView ponctuelHist;
    public TextView ponctuelNbpoint;
    public Button PonctuelBtntake;
    public Button ponctuelBtnFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ponctuel);
        ponctuelBtnFin = (Button) findViewById(R.id.ponctuelBtnFin);
        PonctuelBtntake = (Button) findViewById(R.id.ponctuelBtntake);
        ponctuelHist = (TextView) findViewById(R.id.ponctuelHist);
        ponctuelNbpoint = (TextView) findViewById(R.id.ponctuelNbpoint);


    }
}
