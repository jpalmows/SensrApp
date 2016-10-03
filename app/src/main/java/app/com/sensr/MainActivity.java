package app.com.sensr;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;

import dashr.app.com.sensr.R;


public class MainActivity extends AppCompatActivity implements BluetoothAdapter.LeScanCallback{

    private app.com.sensr.SpeedometerGauge speedometer;
    private app.com.sensr.BatteryIndicatorGauge batteryindicator;
    private app.com.sensr.CoolantTempGauge coolanttemp;
    private app.com.sensr.rpmGauge rpm;


    String hex = "";
    char[] arr;

    private BluetoothAdapter mBluetoothAdapter;
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

  Handler handler1 = new android.os.Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //speedometer code..
        speedometer = (SpeedometerGauge) findViewById(R.id.speedometer);
        speedometer.setMaxSpeed(50);
        speedometer.setLabelConverter(new app.com.sensr.SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });
        speedometer.setMaxSpeed(100);
        speedometer.setMajorTickStep(10);
        speedometer.setMinorTicks(9);
       // speedometer.addColoredRange(0, 30, Color.GREEN);
       // speedometer.addColoredRange(30, 45, Color.YELLOW);
       // speedometer.addColoredRange(45, 50, Color.RED);
        speedometer.setSpeed(15, 1000, 300);


        //rpm code..
        rpm = (rpmGauge) findViewById(R.id.rpm);
        rpm.setMaxSpeed(50);
        rpm.setLabelConverter(new app.com.sensr.rpmGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });
        rpm.setMaxSpeed(7001);
        rpm.setMajorTickStep(1000);
        rpm.setMinorTicks(4);
        rpm.addColoredRange(0, 30, Color.GREEN);
        rpm.addColoredRange(30, 45, Color.YELLOW);
        rpm.addColoredRange(45, 50, Color.RED);
        rpm.setSpeed(15, 1000, 300);



        //coolanttemp code..
        coolanttemp = (CoolantTempGauge) findViewById(R.id.coolanttemp);
        coolanttemp.setMaxSpeed(50);
        coolanttemp.setLabelConverter(new app.com.sensr.CoolantTempGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });
        coolanttemp.setMaxSpeed(250);
        coolanttemp.setMajorTickStep(50);
        coolanttemp.setMinorTicks(4);
        coolanttemp.addColoredRange(0, 30, Color.GREEN);
        coolanttemp.addColoredRange(30, 45, Color.YELLOW);
        coolanttemp.addColoredRange(45, 50, Color.RED);
        coolanttemp.setSpeed(15, 1000, 300);


//
//
//        batteryindicator = (BatteryIndicatorGauge) findViewById(R.id.batteryindicator);
//        batteryindicator.setValue(80, 1000, 300);
//
//        CircularProgressBar circ = (CircularProgressBar) findViewById(R.id.circularprogress);
//        circ.setProgress(90, 1000);
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
        mBluetoothAdapter.startLeScan(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//        long SystemTime = SystemClock.uptimeMillis();
//
        hex = bytesToHex(scanRecord);
        arr = hex.toCharArray();
        String message = hex.substring(10, 15);

        //   handler1.postDelayed(systemPrint,0);


        if (device.getAddress().toString().equals("D0:31:CB:30:78:C2")){

            System.out.println(HexToString(message) );
        }
    }


    @Override
    protected void onResume() {
        super.onResume();


//        // Ensures Bluetooth is available on the device and it is enabled. If not,
//        // displays a dialog requesting user permission to enable Bluetooth.
//        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            //Bluetooth is disabled
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivity(enableBtIntent);
//            finish();
//            return;
//        }
//
//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//
//        mBluetoothAdapter.startLeScan(this);



    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int hex2decimal(String s) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return val;
    }

    public Runnable systemPrint = new Runnable() {
        //
        public void run() {
            System.out.println("hex");

            handler1.removeCallbacks(this, 0);

        }
    };



    private static int hexToInt(char ch) {
        if ('a' <= ch && ch <= 'f') { return ch - 'a' + 10; }
        if ('A' <= ch && ch <= 'F') { return ch - 'A' + 10; }
        if ('0' <= ch && ch <= '9') { return ch - '0'; }
        throw new IllegalArgumentException(String.valueOf(ch));
    }

    public String HexToString(String hex){

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex.length()-1; i+=2 ){

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char)decimal);

            temp.append(decimal);
        }


        return sb.toString();
    }

}