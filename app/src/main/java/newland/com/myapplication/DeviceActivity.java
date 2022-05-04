package newland.com.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;
import cn.com.newland.nle_sdk.responseEntity.SensorInfo;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private static NetWorkBusiness netWorkBusiness = null;

    private SeekBar seekBarR,seekBarG,seekBarB;

    private TextView RGBview,show_Temp,show_Hum;

    private static Map<String,Integer> RGB = new HashMap<>();

    private static StringBuffer sb = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        RGBview = findViewById(R.id.RGBView);
        show_Hum = findViewById(R.id.show_Hum);
        show_Temp = findViewById(R.id.show_Temp);

        String accessToken = getIntent().getStringExtra("accessToken");

        netWorkBusiness = new NetWorkBusiness(accessToken,"http://api.nlecloud.com/");

        seekBarR = findViewById(R.id.R);
        seekBarG = findViewById(R.id.G);
        seekBarB = findViewById(R.id.B);

        seekBarR.setOnSeekBarChangeListener(this);
        seekBarG.setOnSeekBarChangeListener(this);
        seekBarB.setOnSeekBarChangeListener(this);

    }

    //数值改变
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar.getId() == R.id.R){
            RGB.put("R",i);
        } else if (seekBar.getId() == R.id.G){
            RGB.put("G",i);
        } else if (seekBar.getId() == R.id.B){
            RGB.put("B",i);
        }

        RGBview.setText(RGB.get("R")+"." + RGB.get("G") + "." + RGB.get("B"));

        sb.append(RGB.get("R"));
        sb.append(".");
        sb.append(RGB.get("G"));
        sb.append(".");
        sb.append(RGB.get("B"));

        netWorkBusiness.control("474213", "led_display", sb, new NCallBack<BaseResponseEntity>(getApplicationContext()) {
            @Override
            protected void onResponse(BaseResponseEntity response) {
            }
        });
    }

    //开始拖动
    boolean isTrue = true;
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (isTrue){
            timer.schedule(timerTask,1000,1000);
            isTrue = false;
        }
    }

    //停止拖动
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        sb.delete(0,sb.length());
    }

    Timer timer = new Timer();
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                netWorkBusiness.getSensor("474213", "z_temp", new NCallBack<BaseResponseEntity<SensorInfo>>(getApplicationContext()) {
                    @Override
                    protected void onResponse(BaseResponseEntity<SensorInfo> response) {
                        SensorInfo resultObj = response.getResultObj();

                        String value = resultObj.getValue();

                        show_Temp.setText(value);
                    }
                });

                netWorkBusiness.getSensor("474213", "z_hum", new NCallBack<BaseResponseEntity<SensorInfo>>(getApplicationContext()) {
                    @Override
                    protected void onResponse(BaseResponseEntity<SensorInfo> response) {
                        SensorInfo resultObj = response.getResultObj();

                        String value = resultObj.getValue();

                        show_Hum.setText(value);
                    }
                });
            }
        }
    };
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };


}
