package com.simutech.landry.stechmappingindoor;

/**
 * Created by Landry Kateu on 01/08/2016.
 */
import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Writter {
    public File[] myFile ;
    public File myDir;
    public File cartolille;
    public String dir;
    public boolean success;
    public int nbmes = 0;
    String Dossier = "Mesures";
    Erreur er;
    Handler handler;

    public Writter(String dir, int nb, String Heure, Erreur er , Handler h, Context context,String date) {
        this.er =er;
        this.handler = h;
        myFile = new File[nb];
        this.dir = dir+" "+date;
        this.cartolille  = new File(Environment.getExternalStorageDirectory() + File.separator + Dossier);
        this.myDir = new File(cartolille.getAbsolutePath() + File.separator + this.dir); //pour créer le repertoire dans lequel on va mettre notre fichier
        success = true;

        if (!cartolille.exists()) {
            success = cartolille.mkdir(); //On crée le répertoire (s'il n'existe pas!!)
        }
        if (success && !myDir.exists() )
            success = myDir.mkdir();
        if (!success) {
            er.NewErreur(Heure, Erreur.ERREUR_AUTRE, "impossible de creer le dossier ",11,handler);
        }

    }

    public void WriteSettings(String valeurs, String mode, String date, String nu, String operateur,int a,String heure) {

        this.myFile[a] = new File(this.myDir.getAbsolutePath()+ File.separator + nu + "_" + operateur + " " + mode + "_" + date +" "+heure+ ".txt"); //on déclare notre futur fichier

        try {
            FileOutputStream output = new FileOutputStream(myFile[a], true);
            output.write(valeurs.getBytes());
            output.close();
            nbmes++;
        } catch (IOException e) {
            er.NewErreur(heure, Erreur.ERREUR_AUTRE, "impossible d'acceder au fichier ",12,handler);
            e.printStackTrace();
        }

    }
    public void WriteSettings(String valeurs, String heure ,String date ) {

        this.myFile[0] = new File(this.myDir.getAbsolutePath()+ File.separator +"Erreur"+ "_" + date +"_"+heure+ ".txt"); //on déclare notre futur fichier

        try {
            FileOutputStream output = new FileOutputStream(myFile[0], true);
            output.write(valeurs.getBytes());
            output.close();
        } catch (IOException e) {
            er.NewErreur(heure, Erreur.ERREUR_AUTRE, "impossible d'acceder au fichier ",12,handler);
            e.printStackTrace();
        }

    }

    public void WriteSettings(String valeurs,int a,String Heure) {

        try {
            FileOutputStream output = new FileOutputStream(myFile[a], true);
            output.write(valeurs.getBytes());
            output.close();
            nbmes++;
        } catch (IOException e) {
            er.NewErreur(Heure, Erreur.ERREUR_AUTRE, "impossible d'acceder au fichier ",13,handler);
            e.printStackTrace();
        }

    }

}