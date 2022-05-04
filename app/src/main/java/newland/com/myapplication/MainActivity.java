package newland.com.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cn.com.newland.nle_sdk.requestEntity.SignIn;
import cn.com.newland.nle_sdk.responseEntity.User;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editUserName,editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editUserName = findViewById(R.id.editUserName);
        editPassword = findViewById(R.id.editPassword);

        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                NetWorkBusiness netWorkBusiness = new NetWorkBusiness("","http://api.nlecloud.com/");
                SignIn signIn = new SignIn(editUserName.getText().toString(),editPassword.getText().toString());
                netWorkBusiness.signIn(signIn, new NCallBack<BaseResponseEntity<User>>(getApplicationContext()) {
                    @Override
                    protected void onResponse(BaseResponseEntity<User> response) {
                        User user = (User) response.getResultObj();

                        
                        if (user == null){
                            Toast.makeText(MainActivity.this, "请检查用户名密码是否正确......", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent deviceActivity = new Intent(MainActivity.this,DeviceActivity.class);

                        deviceActivity.putExtra("accessToken",user.getAccessToken());

                        startActivity(deviceActivity);
                    }
                });
                break;
        }
    }
}
