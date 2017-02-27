package com.android.didilashi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.didilashi.Utils.LogUtil;
import com.android.didilashi.Utils.OkHttp3Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.android.didilashi.Constant.Constants.loginUrl;

public class LoginActivity extends BaseActivity {

    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.input_phone)
    EditText phone;
    @BindView(R.id.input_password)
    EditText password;
    @BindView(R.id.btn_signup)
    Button btn_signup;


    @OnClick(R.id.btn_signup)
    public void signup(){
        login();
    }

    @OnClick(R.id.link_signup)
    public void toSignup(){
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        toolbar.setTitle("登录滴滴拉屎");
        setSupportActionBar(toolbar);
    }

    private void login() {

        if (!validate()){
            onLoginFailed();
            return;
        }
        btn_signup.setEnabled(false);

        final ProgressDialog progressDialog=new ProgressDialog(LoginActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating....");
        progressDialog.show();

        final String input_phone=phone.getText().toString();
        final String input_password=password.getText().toString();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onLogin(input_phone,input_password);

                progressDialog.dismiss();
                btn_signup.setEnabled(true);
            }
        },2000);

    }

    private void onLogin(final String phone,final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttp3Util.SendRequest(loginUrl+"/"+phone+"/"+password, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        LogUtil.d("Test1",loginUrl+"/"+phone+"/"+password);
                        String res=response.body().string();
                        LogUtil.d("Test1",res);
                       if (res.equals("登录成功")){
                           onLoginSuccess();
                       }else {
                           onLoginFailed();
                       }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void onLoginSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_signup.setEnabled(true);
                Toast.makeText(LoginActivity.this, "success", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }



    private void onLoginFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_SHORT).show();
                btn_signup.setEnabled(true);
            }
        });

    }





    //验证帐号是否符合
    public boolean validate(){
        boolean valid=true;
        String mInput_phone=phone.getText().toString();
        String mInput_password=password.getText().toString();
        if (!mInput_phone.isEmpty()){
            //Toast.makeText(this, "账号不为空", Toast.LENGTH_SHORT).show();
            if (!isMobileNO(mInput_phone)){
                phone.setError("账号不存在");
                valid=false;
            }else{
                phone.setError(null);
            }

        }else {
            phone.setError("账号为空");
            valid=false;
        }

        if (mInput_password.isEmpty() || mInput_password.length()<4 || mInput_password.length()>10){
            password.setError("密码长度为4~10位");
            valid=false;
        }
        return  valid;
    }


    /**
     * 整清楚现在已经开放了多少个号码段
     * 第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles){
        String telRegex = "[1][358]\\d{9}";
        return mobiles.matches(telRegex);
    }
}
