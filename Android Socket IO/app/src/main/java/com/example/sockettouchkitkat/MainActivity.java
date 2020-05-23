package com.example.sockettouchkitkat;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
private Socket mSocket;
private TextView tvTouch;
private Switch swLed;
private int i =0;
private int bg = 255;
private PieChart mPieChart;
private ArrayList dataPie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTouch = (TextView)findViewById(R.id.tvTouch);
        swLed = (Switch)findViewById(R.id.swLed);
        SocketConnection app = new SocketConnection();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on("status_touch", status_touch);
        mSocket.on("status_potentio", status_potentio);
        mSocket.on("status_led", status_led);
        mSocket.connect();
        mPieChart=(PieChart)findViewById(R.id.mPieChart);
        mPieChart.setBackgroundColor(Color.WHITE);
        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setDrawHoleEnabled(true);

        mPieChart.setMaxAngle(180);
        mPieChart.setRotationAngle(180);
        mPieChart.setCenterTextOffset(0,20);
        //set data
        swLed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b){
                    try {
                        getHttpResponseOn();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }if (!b){
                    try {
                        getHttpResponseOff();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });



    }

    public Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Log.d("Socket_check", "Socket Connected!");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (Object obj : args) {
                        Log.d("Socket_check", "Error!" + obj);
                    }

                }
            });
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                }
            });
        }
    };
    private Emitter.Listener status_touch = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (Object obj : args) {
                        tvTouch.setText(obj.toString());
                    }
                }
            });
        }
    };
    private Emitter.Listener status_led = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (Object obj : args) {
                        if (obj.toString().equals("ON")){
                            swLed.setChecked(true);
                            swLed.setText("On");
                            Log.d("LedSocON",obj.toString());
                        } if (obj.toString().equals("OFF") ){
                            swLed.setChecked(false);
                            swLed.setText("Off");
                            Log.d("LedSocOff",obj.toString());
                        }
                    }
                }
            });
        }
    };
    private Emitter.Listener status_potentio = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (Object obj : args) {
                        int bgV = bg -Integer.parseInt(obj.toString());
                        dataPie = new ArrayList<>();
                        dataPie.add(new PieEntry(Integer.parseInt(obj.toString())));
                        dataPie.add(new PieEntry(bgV));
                        PieDataSet dataSet = new PieDataSet(dataPie,"Value");
                        dataSet.setSelectionShift(2f);
                        dataSet.setSliceSpace(2f);
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                        PieData data = new PieData(dataSet);

                        mPieChart.setData(data);
                        mPieChart.invalidate();
                    }
                }
            });
        }
    };
    public void getHttpResponseOn() throws IOException {

        String url = "http://192.168.1.10:3000/ledOn";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

//        Response response = client.newCall(request).execute();
//        Log.e(TAG, response.body().string());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("TestHTTPSF", mMessage);
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String mMessage = response.body().string();
                Log.e("TestHTTPS", mMessage);
            }
        });
    }
    public void getHttpResponseOff() throws IOException {

        String url = "http://192.168.1.10:3000/ledOff";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

//        Response response = client.newCall(request).execute();
//        Log.e(TAG, response.body().string());

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String mMessage = response.body().string();
            }
        });
    }
}
