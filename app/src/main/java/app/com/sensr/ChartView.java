package app.com.sensr;

import android.content.Intent;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.lang.reflect.Array;
import java.util.Random;


public class ChartView extends AppCompatActivity {

ImageButton GaugeView;
private LineGraphSeries<DataPoint> series;
Handler handler1 = new Handler();
GraphView graph;
int[] intArray = new int[10];
Random r = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_view);

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

        GaugeView = (ImageButton) findViewById(R.id.chartView);

        graph = (GraphView) findViewById(R.id.graph);
        Viewport viewport = graph.getViewport();
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(20);
        viewport.setScrollable(true);

        handler1.postDelayed(randNums,100);

    }

    public void GaugeView(View view) {
        Intent ChartView = new Intent(ChartView.this, MainActivity.class);
        startActivity(ChartView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
       // new Thread(new Runnable() {

//            @Override
//            public void run() {
//                // we add 100 new entries
//                for (int i = 0; i < 100; i++) {
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            addEntry();
//                        }
//                    });
//
//                    // sleep to slow down the add of entries
//                    try {
//                        Thread.sleep(600);
//                    } catch (InterruptedException e) {
//                        // manage error ...
//                    }
//                }
//            }
//        }).start();
    }

    // add random data to graph
    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
      //  series.appendData(new DataPoint(lastX++, RANDOM.nextDouble() * 10d), true, 10);
    }
private int i = 0;
    public Runnable randNums = new Runnable() {
        //
        public void run() {

            int randNum = r.nextInt(80 - 0) + 0;

            intArray[i]=randNum;
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
            i++;
            if (i==10){
                i=0;
            }

            handler1.postDelayed(this,10);

        }
    };

}
