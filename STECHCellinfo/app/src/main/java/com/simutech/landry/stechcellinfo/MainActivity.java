package com.simutech.landry.stechcellinfo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.ConsumerIrManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcel;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.IccOpenLogicalChannelResponse;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.Layout;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public TextView data1;
    public TextView data2;
    public  TextView[][] values; //champ de texte
    public  int[][] Donnéescell;
    public int idsim1;
    public int idsim2;
    public Button btns1;
    public Button btns2;
    public Button btnsave;
    public TableLayout cell1;
    public TableLayout cell2;
    public TelephonyManager TelephonManager;
    public ConnectivityManager connec;
    private SubscriptionManager Sm;
    public Context mContext;
    private String type="";
    public TelephonyInfo T;
    public String ChainecellSim1="";
    public String ChainecellSim2="";
    public String[] operateur = new String[2];
    private Network[] net;
    public File myDir;
    boolean dual = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        Sm = SubscriptionManager.from(mContext);
        connec = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        btns1 = (Button) findViewById(R.id.btns1);
        btns2 = (Button) findViewById(R.id.btns2);
        btnsave = (Button) findViewById(R.id.btnsave);
        cell1 = (TableLayout) findViewById(R.id.Cellinfo1);
        cell2 = (TableLayout) findViewById(R.id.Cellinfo2);
        Donnéescell = new int[2][12];
        values = setablevar(cell1,cell2);
        TelephonManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation loc = (GsmCellLocation) TelephonManager.getCellLocation();
        boolean a = connec.isActiveNetworkMetered();
        T = TelephonyInfo.getInstance(mContext);
        List<SubscriptionInfo> list = Sm.getActiveSubscriptionInfoList();
        if (list.size() == 2) {
            operateur[0] = (String) Sm.getActiveSubscriptionInfoForSimSlotIndex(0).getCarrierName();
            operateur[1] = (String) Sm.getActiveSubscriptionInfoForSimSlotIndex(1).getCarrierName();
            ChainecellSim1=operateur[0]+"\n"+"\n";
            ChainecellSim2=operateur[1]+"\n"+"\n";
            idsim1 = list.get(0).getSubscriptionId();
            idsim2 = list.get(1).getSubscriptionId();
        } else {
            operateur[0] = (String) Sm.getActiveSubscriptionInfoForSimSlotIndex(0).getCarrierName();
            ChainecellSim1=operateur[0]+"\n"+"\n";
            idsim1 = list.get(0).getSubscriptionId();
            dual = false;
        }
        btns2.setEnabled(dual);


    }
//TODO:faire set cell vall avec le tableau de valeur sur les textview
    public TextView[][] setablevar(TableLayout table,TableLayout table2){
        TextView[][] value=new TextView[2][12];
        for(int i = 1, j = table.getChildCount(); i < j; i++) {
            View view = table.getChildAt(i);
            View view2 = table2.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                TableRow row2 = (TableRow) view2;
                TextView v = (TextView) row.getChildAt(1);
                TextView v2 = (TextView) row2.getChildAt(1);
                value[0][i-1]=v;
                value[1][i-1]=v2;
            }
        }
        return value;
    }

    /*
        fonction qui s'execute lors d'un click sur le bouton "CELL SIM"
     */
    public void clicksim1(View view) {
        type = "";
        T = TelephonyInfo.getInstance(mContext);
        net = connec.getAllNetworks();
        NetworkCapabilities capa = connec.getNetworkCapabilities(net[0]);
        seein(mContext,connec,idsim2);
        List<CellInfo> liste = T.getInfoCellule1();
        CellLocation location = T.getLocationCellule2();
        if (liste == null)
            return;
        CellInfo cellvide = ReadcellInfo(liste.get(0));
        if(type.equals("4G"))
            ChainecellSim1 = type+"  "+ChainecellSim1+getInfo4g((CellInfoLte)cellvide,capa,location,0);
        if(type.equals("3G"))
            ChainecellSim1 = type+"  "+ChainecellSim1+"\n"+getInfo3g((CellInfoWcdma)cellvide,capa,location,0);
        if(type.equals("2G"))
            ChainecellSim1 = type+"  "+ChainecellSim1+"\n"+getInfo2g((CellInfoGsm)cellvide,capa,location,0);
    }

    /*
        fonction qui s'execute lors d'un click sur le bouton "CELL SIM"
     */
    public void clicksim2(View view) {
        type = "";
        T = TelephonyInfo.getInstance(mContext);
        List<CellInfo> liste = T.getInfoCellule2();
        CellLocation location = T.getLocationCellule2();
        if (liste == null)
            return;
        CellInfo cellvide = ReadcellInfo(liste.get(0));
        net = connec.getAllNetworks();
        NetworkCapabilities capa = connec.getNetworkCapabilities(net[0]);
        if(type.equals("4G"))
           ChainecellSim2 = type+"  "+ChainecellSim2+"\n"+getInfo4g((CellInfoLte)cellvide,capa,location,1);
        if(type.equals("3G"))
            ChainecellSim2 = type+"  "+ChainecellSim2+"\n"+getInfo3g((CellInfoWcdma)cellvide,capa,location,1);
        if(type.equals("2G"))
            ChainecellSim2 = type+"  "+ChainecellSim2+"\n"+getInfo2g((CellInfoGsm)cellvide,capa,location,1);

    }


    /*
    enregistrement fichier
     */
    public void clicksave(View view) {
        //appel de la fonction save avec les données
        Savedata(ChainecellSim1+"\n"+ChainecellSim2);
        //message de confirmation
        Toast toast = Toast.makeText(this, String.format("  "+"Enregistrement terminé"+"  "), Toast.LENGTH_SHORT);
        toast.getView().setBackgroundColor(Color.GREEN);
        toast.show();
    }
    /*
    fonction ReadcellInfo permet de lire les information sur une cellule globale passé en parametre
     et de determiner si c'est une cellule 4g,3g ou 2g
      enfin elle renvoyer la cellule sous son bon format
     */

    private CellInfo ReadcellInfo(CellInfo cell) {
        CellInfo cellvide = null;
        if (cell != null) {
                int dbmValue;
                    if (cell instanceof CellInfoLte) {
                        type = "4G";
                            CellInfoLte cellLTE = (CellInfoLte) cell;
                            dbmValue = cellLTE.getCellSignalStrength().getDbm();
                        return cellLTE;
                    }
                    if (cell instanceof CellInfoWcdma) {
                        type = "3G";
                            CellInfoWcdma cellWCDMA = (CellInfoWcdma) cell;
                            dbmValue = cellWCDMA.getCellSignalStrength().getDbm();
                        return cellWCDMA;
                    }
                    if (cell instanceof CellInfoGsm) {
                        type = "2G";
                            CellInfoGsm cellGSM = (CellInfoGsm) cell;
                            dbmValue = cellGSM.getCellSignalStrength().getDbm();
                            return  cellGSM;
                }

        }
        return  cellvide;

    }
    /*
        fonction getInfo4g
        pour recuperer les données dans le cas d'une antènne 4g
        elle prend en paramètre la cellule(cell),
        les capacité du reseaux utilisé(capa),
        la localisation de la cellule si elle est recuperer,
        et le numero de la sim (1 ou 2)

     */
    public String getInfo4g(CellInfoLte cell,NetworkCapabilities capa,CellLocation location,int i){
        String val = "";
        String sep = "\n";
        if(cell == null)
            return "";
        CellIdentityLte cellIdentity = cell.getCellIdentity();
        CellSignalStrengthLte signal = cell.getCellSignalStrength();
        Class<?> telephonyClass = null;
        try {
            telephonyClass = Class.forName(cellIdentity.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Method[] methods = telephonyClass.getMethods();
        int ci = cellIdentity.getCi();
        int Tac = cellIdentity.getTac();
        int pci = cellIdentity.getPci();
        int mnc = cellIdentity.getMnc();
        int asu = signal.getAsuLevel();
        int dbm = signal.getDbm();
        int upstreamBandwidthKbpslink = capa.getLinkUpstreamBandwidthKbps();
        int downstreBanwidthkbslink = capa.getLinkDownstreamBandwidthKbps();
        int[] valeur;
        valeur = new int[]{ci, pci, Tac,-1, mnc, dbm, asu, downstreBanwidthkbslink, upstreamBandwidthKbpslink, -1, -1, -1};
        setText(i,valeur);
        val =val+"Cell Identity :"+ci+sep
                +"Physical Cell Id :"+pci+sep
                +"Tracking Area Code:"+Tac+sep
                +"Mobile Network Code(0...999):"+mnc+sep
                +"Signal strength(dBm):"+dbm+sep
                +"LTE signal level(asu: 0..97,99 si inconnu):"+asu+sep
                +"Downstream bandwidth(Kbps):"+downstreBanwidthkbslink+sep
                +"Upstream bandwidth(Kbps):"+upstreamBandwidthKbpslink+sep+sep
                +"Latitude :"+sep
                +"Longitude:"+sep;
        return val;
    }

    /*
        fonction getInfo3g
        pour recuperer les données dans le cas d'une antènne 3g
         elle prend en paramètre la cellule(cell),
        les capacité du reseaux utilisé(capa),
        la localisation de la cellule si elle est recuperer,
        et le numero de la sim (1 ou 2)
     */

    public String getInfo3g(CellInfoWcdma cell,NetworkCapabilities capa,CellLocation location,int i){
        String val = "";
        String sep = "\n";
        if(cell == null)
            return "";
        CellIdentityWcdma cellIdentity = cell.getCellIdentity();
        CellSignalStrengthWcdma signal = cell.getCellSignalStrength();
        Class<?> telephonyClass = null;
        try {
            telephonyClass = Class.forName(cellIdentity.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Method[] methods = telephonyClass.getMethods();
        int Cid = cellIdentity.getCid();
        int Lac = cellIdentity.getLac();
        int ScramblingCode = cellIdentity.getPsc();
        int asu = signal.getAsuLevel();
        int mnc = cellIdentity.getMnc();
        int dbm = signal.getDbm();
        int upstreamBandwidthKbpslink = capa.getLinkUpstreamBandwidthKbps();
        int downstreBanwidthkbslink = capa.getLinkDownstreamBandwidthKbps();
        int[]  valeur = new int[]{Cid,-1,-1, Lac, mnc, dbm, asu, downstreBanwidthkbslink, upstreamBandwidthKbpslink, ScramblingCode, -1, -1};
        setText(i,valeur);
        val =val+"Cell Identity :"+Cid+sep
                +"Location Area Code :"+Lac+sep
                +"Scrambling Code:"+ScramblingCode+sep
                +"Mobile Network Code(0...999):"+mnc+sep
                +"Signal strength(dBm):"+dbm+sep
                +"signal level(asu : 0..31,99 si inconnu):"+asu+sep
                +"Downstream bandwidth(Kbps):"+downstreBanwidthkbslink+sep
                +"Upstream bandwidth(Kbps):"+upstreamBandwidthKbpslink+sep+sep
                +"Latitude :"+sep
                +"Longitude:"+sep;

        return val;
    }

    /*
        fonction getInfo2g
        pour recuperer les données dans le cas d'une antènne 2g
         elle prend en paramètre la cellule(cell),
        les capacité du reseaux utilisé(capa),
        la localisation de la cellule si elle est recuperer,
        et le numero de la sim (1 ou 2)
     */
    public String getInfo2g(CellInfoGsm cell,NetworkCapabilities capa,CellLocation location,int i){
        String val = "";
        String sep = "\n";
        if(cell == null)
            return "";
        CellIdentityGsm cellIdentity = cell.getCellIdentity();
        CellSignalStrengthGsm signal = cell.getCellSignalStrength();
        Class<?> telephonyClass = null;
        try {
            telephonyClass = Class.forName(cellIdentity.getClass().getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Method[] methods = telephonyClass.getMethods();
        int Cid = cellIdentity.getCid();
        int Lac = cellIdentity.getLac();
        int asu = signal.getAsuLevel();
        int mnc = cellIdentity.getMnc();
        int dbm = signal.getDbm();
        int upstreamBandwidthKbpslink = capa.getLinkUpstreamBandwidthKbps();
        int downstreBanwidthkbslink = capa.getLinkDownstreamBandwidthKbps();
        int[]  valeur = new int[]{Cid,-1,-1, Lac, mnc, dbm, asu, downstreBanwidthkbslink, upstreamBandwidthKbpslink, -1, -1, -1};
        setText(i,valeur);
        val =val+"Cell Identity :"+Cid+sep
                +"Location Area Code :"+Lac+sep
                +"Mobile Network Code(0...999):"+mnc+sep
                +"Signal strength(dBm):"+dbm+sep
                +"signal level(asu : 0..31,99 si inconnu):"+asu+sep
                +"Downstream bandwidth(Kbps):"+downstreBanwidthkbslink+sep
                +"Upstream bandwidth(Kbps):"+upstreamBandwidthKbpslink+sep+sep
                +"Latitude :"+sep
                +"Longitude:"+sep;
        return val;
    }

    /*
    fonction setText pour inserer les valeur dans les champ destiné a l'ecran
     */
    public void setText(int i,int[]  valeur){
        for (int j=0;j<values[i].length;j++) {
            if(valeur[j]!=-1)
            values[i][j].setText(""+valeur[j]);
            else
                values[i][j].setText("NA");
        }
    }
    private void seein(Context context, Object tel,int id){
        boolean status=false;
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            netInfo = cm.getNetworkInfo(1);

        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            String predictedMethodName = "getActiveNetworkInfoForUid";
            String predictedMethodName2 = "getAllNetworkInfo";
            String predictedMethodName4 = "getNetworkForType";

            Class<?> telephonyClass = Class.forName(tel.getClass().getName());
            Method[] methods = telephonyClass.getMethods();
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getLoc = telephonyClass.getMethod(predictedMethodName, parameter);
            Method getLoc2 = telephonyClass.getMethod(predictedMethodName2);
            Method getLoc4 = telephonyClass.getMethod(predictedMethodName4,parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = id;
            Object[] obParameter2 = new Object[1];
            obParameter2[0] = 0;
            Object ob_phone2 = getLoc2.invoke(tel);
            Object ob_phone3 = getLoc2.invoke(tel);
            Object ob_phone4 = getLoc4.invoke(tel, obParameter2);
            obParameter2[0] = 1;
            Object ob_phone = getLoc4.invoke(tel, obParameter);
            NetworkCapabilities a = connec.getNetworkCapabilities((Network) ob_phone4);
            NetworkCapabilities b = connec.getNetworkCapabilities((Network) ob_phone);
            NetworkInfo tao = connec.getNetworkInfo((Network) ob_phone4);
            String djkh = a.toString();
            int h = 5;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void Savedata(String valeurs) {
        Date d = new Date();
        DateFormat df = new DateFormat();
        Boolean success = true;
        String heure = (String) DateFormat.format("HH:mm:ss", d);
        String date = (String) DateFormat.format("dd-MM-yyyy", d);
        myDir = new File(Environment.getExternalStorageDirectory() + File.separator + "CellInfos"); //pour créer le repertoire dans lequel on va mettre notre fichier
        if (!myDir.exists()) {
            success = myDir.mkdir(); //On crée le répertoire (s'il n'existe pas!!)
        }
        File myFile   = new File(myDir.getAbsolutePath()+ File.separator +"données"+ "_" + date +"_"+heure+ ".txt"); //on déclare notre futur fichier
        try {
            FileOutputStream output = new FileOutputStream(myFile, true);
            output.write(valeurs.getBytes());
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /*
    scanne la memoire du telephone a la recherche du nouveau repertoire crée
     */

    public void scan() {
        File[] fichiers = myDir.listFiles();
        MediaScannerConnection.scanFile(mContext, new String[]{myDir.getAbsolutePath()}, null, null);
        for (File f : fichiers) {
            MediaScannerConnection.scanFile(mContext, new String[]{myDir.toString(), f.toString()}, null, null);
        }
    }



}
