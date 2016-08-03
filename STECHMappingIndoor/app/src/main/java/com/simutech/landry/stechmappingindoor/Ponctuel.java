package com.simutech.landry.stechmappingindoor;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Ponctuel extends AppCompatActivity implements LocationListener {
    public TextView ponctuelHist;
    public TextView ponctuelNbpoint;
    public Button PonctuelBtntake;
    public Button ponctuelBtnFin;
    static List<File> listM = new ArrayList<File>();
    static List<File> listE = new ArrayList<File>();
    public TelephonyManager TelephonManager;
    public String[] operateur = new String[2];
    public String[] text = new String[2];
    public String[] mode = new String[2];
    public String Ldate = "";
    public String Heure = "";
    public SharedPreferences sharedPref;
    Thread majheure;
    Thread coulerT;
    boolean continu = false;
    Writter wm;
    boolean dual = true;
    SubscriptionManager Sm;
    public RadioButton r4g;
    public RadioButton r3g;
    public RadioButton r2g;
    int idsim1;
    int idsim2;
    double[] lat = new double[2];
    double[] lon = new double[2];
    String type = "";
    int i = 0;
    int nbpos = 0;
    int signalsim1 = -150;
    int signalsim2 = -150;
    boolean started = false;
    int num=0;
    String nu="";
    Handler handler;
    Long timemaj;
    Calendar cal;
    MultiSimListener muti;
    private LocationManager lManager;
    private Location location;
    private static Context mContext;
    public boolean canstart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ponctuel);
        ponctuelBtnFin = (Button) findViewById(R.id.ponctuelBtnFin);
        ponctuelBtnFin.setOnClickListener(FinClickListener);
        PonctuelBtntake = (Button) findViewById(R.id.ponctuelBtntake);
        PonctuelBtntake.setOnClickListener(takeClickListener);
        r4g = (RadioButton) findViewById(R.id.PonctuelRadio4G);
        r3g = (RadioButton) findViewById(R.id.PonctuelRadio3G);
        r2g = (RadioButton) findViewById(R.id.PonctuelRadio2G);
        ponctuelHist = (TextView) findViewById(R.id.ponctuelHist);
        ponctuelNbpoint = (TextView) findViewById(R.id.ponctuelNbpoint);
        mContext = getApplicationContext();
        ponctuelHist.setBackgroundColor(Color.WHITE);
        ponctuelHist.setTextColor(Color.BLACK);
        ponctuelNbpoint.setTextColor(Color.BLACK);
        ponctuelNbpoint.setBackgroundColor(Color.WHITE);
        Sm = SubscriptionManager.from(mContext);
        Date d = new Date();
        DateFormat df = new DateFormat();
        Heure = (String) DateFormat.format("HH-mm-ss", d);
        Ldate = (String) DateFormat.format("dd-MM-yyyy", d);
        mode[1] = "2G";
        mode[0] = "";

        ponctuelBtnFin.setEnabled(false);
        try {
            canstart = true;
            TelephonManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            List<SubscriptionInfo> list = Sm.getActiveSubscriptionInfoList();
            if (list.size() == 2) {
                wm = new Writter("Fichiersmesure", 2, Heure, handler, mContext, Ldate);
                operateur[0] = (String) Sm.getActiveSubscriptionInfoForSimSlotIndex(0).getCarrierName();
                operateur[1] = (String) Sm.getActiveSubscriptionInfoForSimSlotIndex(1).getCarrierName();
                idsim1 = list.get(0).getSubscriptionId();
                idsim2 = list.get(1).getSubscriptionId();
                muti = new MultiSimListener(idsim2);
            } else {
                wm = new Writter("Fichiersmesure", 1, Heure, handler, mContext, Ldate);
                operateur[0] = (String) Sm.getActiveSubscriptionInfoForSimSlotIndex(0).getCarrierName();
                dual = false;
            }
        } catch (Exception e) {
            ToastMaker to = new ToastMaker(Ponctuel.this, "Redemarer l'application et Verifier que le telephone est bien connecté a un reseau", Color.RED);
            to.createtwo();
            canstart = false;
        }
        if (dual) {
            TelephonManager.listen(muti,
                    PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                            | PhoneStateListener.LISTEN_SERVICE_STATE);
        }
        majheure = new Heurethread();
        majheure.start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle b = msg.getData();
                Integer key = b.getInt("KEY");
                Integer value = b.getInt("value");
                if (key == 3) {
                    switch (value) {

                    }
                }

            }

        };
    }
    //TODO: corriger le cas ou readcell renvoie un grand nombre
    View.OnClickListener takeClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            if (!canstart)
                return;

            if (!(wm.success )) {
                ToastMaker to = new ToastMaker(Ponctuel.this, "Impossible d'enregistrer verifier la mémoire du télephone", Color.RED);
                to.createtwo();
                return;
            }
            if (!started) {
                wm.WriteSettings(" mesure " + operateur[0] + " " + mode[0] + " " + Ldate + " " + Heure + "\n", mode[0], Ldate, nu, operateur[0], 0, Heure);
                if (dual)
                    wm.WriteSettings(" mesure " + operateur[1] + " " + mode[1] + " " + Ldate + " " + Heure + "\n", mode[1], Ldate, nu, operateur[1], 1, Heure);
                started = true;
            }
            if (mode[0].length() < 2) {
                ToastMaker to = new ToastMaker(Ponctuel.this, "choisir la technologie avant de commencer", Color.RED);
                to.createtwo();
                return;
            }

            ponctuelBtnFin.setEnabled(true);
            obtenirPosition();
            ReadcellInfo();
            i++;
            prewrite(signalsim1,0);
            String val = operateur[0]+" "+mode[0]+" "+signalsim1+" "+ "dbm" ;
            if(dual){
                prewrite(signalsim2,1);
                val = val+"\n"+operateur[1]+" "+mode[1]+" "+signalsim2+" "+ "dbm" ;
            }
            ponctuelHist.setText(val);
            ponctuelNbpoint.setText(""+i);
            r3g.setClickable(false);
            r4g.setClickable(false);
            r2g.setClickable(false);

        }
    };
    View.OnClickListener FinClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            num++;
            nu = num + "_";
            started = false;
            ponctuelBtnFin.setEnabled(false);
            r3g.setClickable(true);
            r4g.setClickable(true);
            r2g.setClickable(true);
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        if (location.getProvider().equals("gps")) {
            this.location = location;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public class MultiSimListener extends PhoneStateListener {

        String service = "";
        private Field subIdField;
        private int subId = -1;

        public MultiSimListener(int subId) {
            super();
            try {
                // Get the protected field mSubId of PhoneStateListener and set it
                subIdField = this.getClass().getSuperclass().getDeclaredField("mSubId");
                subIdField.setAccessible(true);
                subIdField.set(this, subId);
                this.subId = subId;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();

            } catch (IllegalAccessException e) {
                e.printStackTrace();

            } catch (IllegalArgumentException e) {
                e.printStackTrace();

            }
        }
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {

            if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                return;
            }
            int pre = -113 + 2 * signalStrength.getGsmSignalStrength();
            if (pre == 85) {
                pre = -150;
            }
            signalsim2 = pre;
        }

        public void onServiceStateChanged(ServiceState serviceState) {
            this.service = serviceState.getOperatorAlphaLong();
            super.onServiceStateChanged(serviceState);

        }

    }
    private void obtenirPosition() {

        List<String> providers = lManager.getProviders(true);
        String[] sources = new String[providers.size()];
        int j = 0;
        for (String provider : providers)
            sources[j++] = provider;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        lManager.requestLocationUpdates("gps", 100, 0,Ponctuel.this);
        location = lManager.getLastKnownLocation("gps");
        if (location == null) {
            //TODO: generer une erreur ou pas
        }
    }

        class Heurethread extends Thread {

        public Heurethread() {
        }

        public void run() {
            while (continu) {
                Date d = new Date();
                DateFormat df = new DateFormat();
                Heure = (String) DateFormat.format("HH-mm-ss", d);
                Ldate = (String) DateFormat.format("dd-MM-yyyy", d);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void scan() {
        Listfile();
        MediaScannerConnection.scanFile(mContext, new String[]{wm.cartolille.getAbsolutePath()}, null, null);
        for (File f : listM) {
            MediaScannerConnection.scanFile(mContext, new String[]{wm.myDir.toString(), f.toString()}, null, null);
        }
    }

    public void Listfile() {
        File f2 = wm.myDir;

        File[] fichiers = f2.listFiles();
        int k = 0;
        listM.clear();
        // Si le répertoire n'est pas vide...
        if (fichiers != null)
            // On les ajoute à  l'adaptateur
            for (File f : fichiers) {
                listM.add(k, f);
                k++;
            }
    }

    public void prewrite(int signal, int idsim) {
        if (idsim == 0) {
            text[idsim] = format(signal, idsim);
        } else {
            text[idsim] = format(signal, idsim);
        }
        if (lat[0] == 0) {
            text[idsim] = signal + " " + mode[idsim] + " " + Heure + " pas de gps\n ";
        }
        wm.WriteSettings(text[idsim], idsim, Heure);
    }

    private String format(int signal, int idsim) {
        if (location != null) {
            lat[0] = location.getLatitude();
            lon[0] = location.getLongitude();
        } else {
            lat[idsim] = 0;
            lon[idsim] = 0;
        }
        if (signal >= -150 && signal < 0)
            return signal + ";" + Heure + ";" + lat[0] + " ; " + lon[0] + "\n";
        else
            return -150 + ";" + Heure + ";" + lat[0] + " ; " + lon[0] + "\n";

    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.PonctuelRadio4G:
                if (checked)
                    mode[0] = "4G";
                break;
            case R.id.PonctuelRadio3G:
                if (checked)
                    mode[0] = "3G";
                break;
            default:
                mode[0] = "2G";
                break;
        }

    }

    private void ReadcellInfo() {
        boolean valid = false;
        if (TelephonManager.getAllCellInfo() != null) {
            List<CellInfo> list = TelephonManager.getAllCellInfo();
            for (CellInfo cell : list) {
                int dbmValue;
                if (cell.isRegistered()) {
                    if (cell instanceof CellInfoLte) {
                        type = "4G";
                        if (mode[0].equals("4G")) {
                            CellInfoLte cellLTE = (CellInfoLte) cell;
                            dbmValue = cellLTE.getCellSignalStrength().getDbm();
                            signalsim1 = dbmValue;
                            valid = true;
                            return;
                        }
                    }
                    if (cell instanceof CellInfoWcdma) {
                        type = "3G";
                        if (mode[0].equals("3G")) {
                            CellInfoWcdma cellWCDMA = (CellInfoWcdma) cell;
                            dbmValue = cellWCDMA.getCellSignalStrength().getDbm();
                            signalsim1 = dbmValue;
                            valid = true;
                            return;

                        }
                    }
                    if (cell instanceof CellInfoGsm) {
                        type = "2G";
                        if (mode[0].equals("2G")) {
                            CellInfoGsm cellGSM = (CellInfoGsm) cell;
                            dbmValue = cellGSM.getCellSignalStrength().getDbm();
                            signalsim1 = dbmValue;
                            valid = true;
                            return;
                        }
                    }

                }
            }
        }
        if (!valid) {
            signalsim1  = -150;
        }
    }
}
