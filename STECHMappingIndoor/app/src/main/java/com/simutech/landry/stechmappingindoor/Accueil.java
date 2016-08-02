package com.simutech.landry.stechmappingindoor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Accueil extends AppCompatActivity {
    public static String PONCTUEL = "com.simutech.landry.stechmappingindoor.Ponctuel";
    public static String ECHANTILLON = "com.simutech.landry.stechmappingindoor.MainActivity";
    public Button btnP, btnE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);
        btnE = (Button) findViewById(R.id.btnE);
        btnP = (Button) findViewById(R.id.btnP);
        btnE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view, Echantillonage.class);

            }
        });
        btnP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(view, Ponctuel.class);
            }
        });

    }

    public void sendMessage(View view, Class a) {
        Intent intent = new Intent(this, a);
        startActivity(intent);
    }
}