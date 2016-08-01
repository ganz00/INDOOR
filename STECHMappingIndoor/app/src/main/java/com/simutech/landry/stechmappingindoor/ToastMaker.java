package com.simutech.landry.stechmappingindoor;

/**
 * Created by Landry Kateu on 01/08/2016.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.widget.Toast;

public class ToastMaker {
    public Handler h;
    String text;
    int color;
    Activity a;

    public ToastMaker(Activity a,String t, int color){
        this.color = color;
        this.text = t;
        this.a = a;

    }
    public ToastMaker(Activity a, String t, int color, Handler h){
        this.color = color;
        this.text = t;
        this.a = a;
        this.h = h;
    }
    public void createone(){
        Toast toast = Toast.makeText(a, String.format("  "+text+"  "), Toast.LENGTH_SHORT);
        toast.getView().setBackgroundColor(color);
        toast.show();
    }
    public void createtwo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).setTitle("Erreur");
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void showDialogone(){
        AlertDialog.Builder builder = new AlertDialog.Builder(a);

        builder.setTitle("confirmation");
        builder.setMessage(text);
        builder.setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }
        );
        builder.setNegativeButton("annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();

    }
    public void showDialogtwo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle("");

        builder.setMessage(text);
        builder.setPositiveButton("clio", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }
        );
        builder.setNegativeButton("scenic", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();

    }

}
