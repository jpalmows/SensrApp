package app.com.sensr;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;



public class MainActivity extends AppCompatActivity implements BluetoothAdapter.LeScanCallback , LocationListener {

    Database db;
    private app.com.sensr.SpeedometerGauge speedometer;
    private app.com.sensr.BatteryIndicatorGauge batteryindicator;
    private app.com.sensr.CoolantTempGauge coolanttemp;
    private app.com.sensr.OilPressureGauge oilpressure;

    private app.com.sensr.rpmGauge rpm;

    private Float floatvalCoolantTemp, floatvalOilPressure;
    private Double tempDouble;
    private String outputTemp, outputPressure;
    private Long commdelay1 = 0L;
    private Long commdelay2 = 0L;
    public static String ARG_SECTION_NUMBER;
    String hex = "";
    char[] arr;

    TextView coolantTempText, oilPressureText;

    private BluetoothAdapter mBluetoothAdapter;
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    Handler handler1 = new Handler();
    Handler handler2 = new Handler();
    Handler handler3 = new Handler();

    ImageButton ChartView,GaugeView,DataView;

    private LineGraphSeries<DataPoint> series;
    Handler handler4 = new Handler();
    GraphView graph;
    int[] intArray = new int[10];
    Random r = new Random();
    LinearLayout gauge1layout, gauge2layout, dataLayout,ChartViewLayout,GaugeViewLayout,DataViewLayout;
    float mCurrentSpeedmph;
    int mCurrentSpeedmphInt;

    ArrayAdapter<String> dataAdapter1, dataAdapter2,dataAdapter3;
    ListView dataList1, dataList2, dataList3;
    List<String> listData1,listData2,listData3;

    private String currentSpeed;

    public static ArrayList<String> SensorTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         db = new Database(this);


        handler1.postDelayed(checkStatus, 0);

        ChartView = (ImageButton) findViewById(R.id.chartView);
        GaugeView = (ImageButton) findViewById(R.id.gaugeView);
        DataView = (ImageButton) findViewById(R.id.dataView);

        ChartViewLayout = (LinearLayout) findViewById(R.id.chartviewLayout);
        GaugeViewLayout = (LinearLayout) findViewById(R.id.gaugeviewLayout);
        DataViewLayout = (LinearLayout) findViewById(R.id.dataviewLayout);

        gauge1layout = (LinearLayout) findViewById(R.id.gauge1Layout);
        gauge2layout = (LinearLayout) findViewById(R.id.gauge2Layout);

        dataLayout = (LinearLayout) findViewById(R.id.dataLayout);
        dataList1 = (ListView) findViewById(R.id.dataList1);
        dataList2 = (ListView) findViewById(R.id.dataList2);
        dataList3 = (ListView) findViewById(R.id.dataList3);

        listData1 = new ArrayList<String>();
        listData2 = new ArrayList<String>();
        listData3 = new ArrayList<String>();

       // listData1.addAll(db.getSensorTypeList());
        listData1.addAll(db.getAllData());
        listData2.addAll(db.getDataList());
        listData3.addAll(db.getTimeStampList());

        dataAdapter1  = new ArrayAdapter<String>(this,R.layout.listviewcustomlayout1,listData1);
        dataAdapter2  = new ArrayAdapter<String>(this,R.layout.listviewcustomlayout1,listData2);
        dataAdapter3  = new ArrayAdapter<String>(this,R.layout.listviewcustomlayout1,listData3);

        dataList1.setAdapter(dataAdapter1);
        dataList2.setAdapter(dataAdapter2);
        dataList3.setAdapter(dataAdapter3);

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
     //   this.onLocationChanged(null);


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
        speedometer.setMinorTicks(1);
       // speedometer.addColoredRange(0, 30, Color.GREEN);
       // speedometer.addColoredRange(30, 45, Color.YELLOW);
       // speedometer.addColoredRange(45, 50, Color.RED);
        speedometer.setSpeed(0, 1000, 300);


        //rpm code..
//        rpm = (rpmGauge) findViewById(R.id.rpm);
//        rpm.setMaxSpeed(50);
//        rpm.setLabelConverter(new app.com.sensr.rpmGauge.LabelConverter() {
//            @Override
//            public String getLabelFor(double progress, double maxProgress) {
//                return String.valueOf((int) Math.round(progress));
//            }
//        });
//        rpm.setMaxSpeed(7001);
//        rpm.setMajorTickStep(1000);
//        rpm.setMinorTicks(4);
//        rpm.addColoredRange(0, 30, Color.GREEN);
//        rpm.addColoredRange(30, 45, Color.YELLOW);
//        rpm.addColoredRange(45, 50, Color.RED);
//        rpm.setSpeed(15, 1000, 300);



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
        coolanttemp.addColoredRange(0, 100, Color.GREEN);
        coolanttemp.addColoredRange(100, 200, Color.YELLOW);
        coolanttemp.addColoredRange(200, 300, Color.RED);

         coolantTempText =  (TextView) findViewById(R.id.coolantTempText);

        coolanttemp.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {


                return true;
            }
        });

        //oil pressure code..
        oilpressure = (OilPressureGauge) findViewById(R.id.oilpressure);
        oilpressure.setMaxSpeed(50);
        oilpressure.setLabelConverter(new app.com.sensr.OilPressureGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });
        oilpressure.setMaxSpeed(100);
        oilpressure.setMajorTickStep(25);
        oilpressure.setMinorTicks(4);
        oilpressure.addColoredRange(75, 100, Color.GREEN);
        oilpressure.addColoredRange(25, 75, Color.YELLOW);
        oilpressure.addColoredRange(0, 25, Color.RED);

        oilPressureText =  (TextView) findViewById(R.id.oilpressuretext2);

//        coolanttemp.setOnLongClickListener(new View.OnLongClickListener(){
//            @Override
//            public boolean onLongClick(View v) {
//
//
//                return true;
//            }
//        });

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

        intArray[0]=1;
        intArray[1]=2;
        intArray[2]=3;
        intArray[3]=4;
        intArray[4]=5;
        intArray[5]=6;
        intArray[6]=7;
        intArray[7]=8;
        intArray[8]=9;
        intArray[9]=10;


        graph = (GraphView) findViewById(R.id.graph);
        Viewport viewport = graph.getViewport();
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(intArray.length);
        viewport.setScrollable(true);

        handler1.postDelayed(randNums,100);

        DataView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {


                    Intent dbmanager = new Intent(MainActivity.this, DatabaseManager.class);
                    startActivity(dbmanager);

                return true;

            }

        });


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

      //  System.out.println(device.getAddress()+ " "+ hex);

        //0 to 1 output
        String output = hex.substring(10, 18);

        //   handler1.postDelayed(systemPrint,0);


        if (device.getAddress().toString().equals("E8:3D:0C:81:71:F4")){

            commdelay1 = SystemClock.uptimeMillis();

           // System.out.println(HexToString(message) );
           // System.out.println(hex);
          //  System.out.println(hex);
          //  System.out.println(output);
            String reversedOutput = output.substring(6,8)+output.substring(4,6)+ output.substring(2,4)+output.substring(0,2);
           // System.out.println("REVERSED:  "+reversedOutput);
            Long i = Long.parseLong(reversedOutput,16);
            floatvalCoolantTemp = (Float.intBitsToFloat(i.intValue()));
           // System.out.println("float: "+floatvalCoolantTemp);
            outputTemp = outputToTemp(floatvalCoolantTemp).toString();
         //   System.out.println("resistance: "+outputTemp);

            handler2.postDelayed(tempOutput,100);

        }

        if (device.getAddress().toString().equals("D0:31:CB:30:78:C2")){

            commdelay2 = SystemClock.uptimeMillis();

            // System.out.println(HexToString(message) );
            // System.out.println(hex);
            //  System.out.println(hex);
            //  System.out.println(output);
            String reversedOutput = output.substring(6,8)+output.substring(4,6)+ output.substring(2,4)+output.substring(0,2);
            // System.out.println("REVERSED:  "+reversedOutput);
            Long i = Long.parseLong(reversedOutput,16);
            floatvalOilPressure = (Float.intBitsToFloat(i.intValue()));
           // System.out.println("float: "+floatvalOilPressure);
            outputPressure = outputToPressure(floatvalOilPressure).toString();
         //   System.out.println("resistance: "+outputPressure);

            handler3.postDelayed(pressureOutput,100);

        }
//        if (device.getAddress().toString().equals("E8:3D:0C:81:71:F4")){
//
//            // System.out.println(HexToString(message) );
//            // System.out.println(hex);
//            //  System.out.println(hex);
//            //  System.out.println(output);
//            String reversedOutput = output.substring(6,8)+output.substring(4,6)+ output.substring(2,4)+output.substring(0,2);
//            // System.out.println("REVERSED:  "+reversedOutput);
//            Long i = Long.parseLong(reversedOutput,16);
//            floatval = (Float.intBitsToFloat(i.intValue()));
//            System.out.println("float: "+floatval);
//            outputTemp = outputToTemp(floatval).toString();
//            System.out.println("resistance: "+outputTemp);
//
//            handler2.postDelayed(tempOutput,50);
//
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        mBluetoothAdapter.startLeScan(this);

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

    public Runnable checkStatus = new Runnable() {
        //
        public void run() {

            if ((SystemClock.uptimeMillis() - commdelay1) > 3000 ){
                coolantTempText.setText(" - °F");
                coolanttemp.setSpeed(0, 10, 100);

            }
            if ((SystemClock.uptimeMillis() - commdelay2) > 3000 ){
                oilPressureText.setText(" - P.S.I.");
                oilpressure.setSpeed(0, 10, 100);

            }
            handler1.postDelayed(this, 250);

        }
    };

    public Runnable tempOutput = new Runnable() {
        //
        public void run() {

            coolantTempText.setText(outputTemp+" °F");
            coolanttemp.setSpeed(outputToTemp(floatvalCoolantTemp), 10, 100);
            SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy  hh:mm:ss");
            String timestamp = s.format(new Date());


            db.insertData("Coolant Temperature",outputTemp,timestamp);
            handler2.removeCallbacks(this, 0);

        }
    };
    public Runnable pressureOutput = new Runnable() {
        //
        public void run() {
            Long pressureVal = outputToPressure(floatvalOilPressure);

            if (Integer.parseInt(outputPressure) < 5){
                outputPressure = "0";
                 pressureVal = 0L;
             //   System.out.println(Integer.parseInt("string to int: "+outputPressure));
            }

            oilPressureText.setText(outputPressure+" P.S.I.");

            oilpressure.setSpeed(pressureVal, 10, 100);

            SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy  hh:mm:ss");
            String timestamp = s.format(new Date());


            db.insertData("Oil Pressure",outputPressure,timestamp);

            handler3.removeCallbacks(this, 0);

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

    private static String HexToFloat(String hexValue)
    {
        Long i = Long.parseLong(hexValue, 16);
        Float f = Float.intBitsToFloat(i.intValue());
        String output = Integer.toHexString(Float.floatToIntBits(f));
        return output;
    }
//    private static Long outputToTemp(Float FloatVal)
//    {
//        double roomTemp = 298.15;
//        double roomTempRes = 5000;
//        double Bcoeff = 3900;
//        Double Vdrop = 3.3 - (3.3* FloatVal);
//
//        Double i = 3.3*FloatVal/roomTempRes;
//        Double ThermRes = Vdrop/i;
//        Double ThermTempKel = 1/((1/roomTemp) + ((1/Bcoeff)*Math.log(ThermRes/roomTempRes)));
//        Double ThermTempCel = ThermTempKel -273.15;
//        Long ThermTempF = Math.round(ThermTempCel * 1.8 +32);
//
//       // String ThermStr = ThermTempF.toString();
//
//        return ThermTempF;
//    }
    private static Long outputToPressure(Float FloatVal)
    {


        Long OilPressure = Math.abs(Math.round(-255.68*FloatVal + 137.42));

        // String ThermStr = ThermTempF.toString();


        return OilPressure;
    }
    private static Long outputToTemp(Float FloatVal)
    {
//        double roomTemp = 298.15;
//        double roomTempRes = 1000;
//        double Bcoeff = 3900;
//        Double Vdrop = 3.3 - (3.3* FloatVal);
//
//        Double i = 3.3*FloatVal/roomTempRes;
//        Double ThermRes = Vdrop/i;
//        Double ThermTempKel = 1/((1/roomTemp) + ((1/Bcoeff)*Math.log(ThermRes/roomTempRes)));
//        Double ThermTempCel = ThermTempKel -273.15;
        Long ThermTempF = Math.round(-75.52*Math.log(FloatVal)+6.5178);



        return ThermTempF;
    }

    @Override
    public void onLocationChanged(Location location) {
        TextView speedText = (TextView) this.findViewById(R.id.speedText);
        System.out.println("working");
        if (location == null){
            speedText.setText("- MPH");


        }
        else {
            float mCurrentSpeed = location.getSpeed();
            mCurrentSpeedmph = mCurrentSpeed * 2.23694f;
            mCurrentSpeedmphInt = Math.round(mCurrentSpeedmph);
            currentSpeed = String.format("%.2f", mCurrentSpeedmph);
            speedText.setText(currentSpeed + " MPH");
            speedometer.setSpeed(mCurrentSpeedmph, 10, 100);
            SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy  hh:mm:ss");
            String timestamp = s.format(new Date());


            db.insertData("Speed",currentSpeed,timestamp);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void ChartView(View view) {
       // Intent ChartView = new Intent(MainActivity.this, ChartView.class);
      //  startActivity(ChartView);
      gauge1layout.setVisibility(View.GONE);
      gauge2layout.setVisibility(View.GONE);
        graph.setVisibility(View.VISIBLE);
     ChartViewLayout.setVisibility(View.GONE);
      GaugeViewLayout.setVisibility(View.VISIBLE);
        DataViewLayout.setVisibility(View.VISIBLE);
        dataLayout.setVisibility(View.GONE);

    }
    public void GaugeView(View view) {
        // Intent ChartView = new Intent(MainActivity.this, ChartView.class);
        //  startActivity(ChartView);
        gauge1layout.setVisibility(View.VISIBLE);
        gauge2layout.setVisibility(View.VISIBLE);
        graph.setVisibility(View.GONE);

        ChartViewLayout.setVisibility(View.VISIBLE);
        GaugeViewLayout.setVisibility(View.GONE);
        DataViewLayout.setVisibility(View.VISIBLE);
        dataLayout.setVisibility(View.GONE);



    }
    public void DataView(View view) {
        // Intent ChartView = new Intent(MainActivity.this, ChartView.class);
        //  startActivity(ChartView);
        gauge1layout.setVisibility(View.GONE);
        gauge2layout.setVisibility(View.GONE);
        graph.setVisibility(View.GONE);

        ChartViewLayout.setVisibility(View.VISIBLE);
        GaugeViewLayout.setVisibility(View.VISIBLE);
        DataViewLayout.setVisibility(View.GONE);
        dataLayout.setVisibility(View.VISIBLE);


    }
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                // Your code
                break;
            case 2:
                // your code
                break;
        }
    }

   private int i = 8;
    public Runnable randNums = new Runnable() {
        //
        public void run() {

            int randNum = r.nextInt(80 - 0) + 0;



                intArray[i] = intArray[i+1];

            intArray[9]=mCurrentSpeedmphInt;
            graph.removeAllSeries();

            series = new LineGraphSeries<>(new DataPoint[] {


                    new DataPoint(0, intArray[0]),
                    new DataPoint(1, intArray[1]),
                    new DataPoint(2, intArray[2]),
                    new DataPoint(3, intArray[3]),
                    new DataPoint(4, intArray[4]),
                    new DataPoint(5, intArray[5]),
                    new DataPoint(6, intArray[6]),
                    new DataPoint(7, intArray[7]),
                    new DataPoint(8, intArray[8]),
                    new DataPoint(9, intArray[9])

                    //  new DataPoint(50, 90)

            });
            graph.addSeries(series);
            i=i-1;
            if (i==0){
                i=8;
            }

            handler1.postDelayed(this,100);

        }
    };

}