package com.android.didilashi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.didilashi.Entity.NewAccount;
import com.android.didilashi.Utils.LogUtil;
import com.android.didilashi.Utils.OkHttp3Util;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.R.attr.id;
import static android.os.Build.VERSION_CODES.N;
import static com.android.didilashi.Constant.Constants.codeUrl;
import static com.android.didilashi.Constant.Constants.signUrl;
import static com.android.didilashi.R.id.btn_sign;
import static com.android.didilashi.R.id.btn_signup;
import static com.android.didilashi.R.id.input_password;
import static com.android.didilashi.R.id.input_phone;
import static com.android.didilashi.R.id.phoneWrapper;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    public String code;

    @BindView(R.id.phoneWrapper)
    TextInputLayout phoneWrapper;
    @BindView(R.id.usernameWrapper)
    TextInputLayout usernameWrapper;
    @BindView(R.id.passwordWrapper)
    TextInputLayout passwordWrapper;
    @BindView(R.id.captchaWrapper)
    TextInputLayout captchaWrapper;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(input_phone)
    EditText signPhone;
    @BindView(R.id.username)
    EditText signUsername;
    @BindView(input_password)
    EditText signPassword;
    @BindView(R.id.input_captcha)
    EditText signCaptcha;
    @BindView(R.id.btn_sign)
    Button signButton;

    @OnClick(R.id.link_login)
    public void toLogin(){
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.sendCode)
    public void sendCode(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String phone=signPhone.getText().toString();
                OkHttp3Util.SendRequest(codeUrl + phone, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                            final String getCode=response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                code=getCode;
                                LogUtil.d("test",code);
                            }
                        });
                    }
                });
            }
        }).start();
    }


    @OnClick(R.id.btn_sign)
    public void signAccount(){
        sign();
    }

    private void sign() {

        if (!validate()){
            onLoginFailed();
            return;
        }
        signButton.setEnabled(false);

        final ProgressDialog progressDialog=new ProgressDialog(SignupActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating....");
        progressDialog.show();

        final String phone=signPhone.getText().toString();
        final String username=signUsername.getText().toString();
        final String password=signPassword.getText().toString();
        NewAccount account=new NewAccount(phone,username,password,code);
        Gson gson=new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
       final String data=gson.toJson(account);
        LogUtil.d("Test",data);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onSign(data);
                 onLoginSuccess();
                progressDialog.dismiss();
                signButton.setEnabled(true);
            }
        },2000);

    }

    public void onSign(final String data){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttp3Util.SendRequest(signUrl + data, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res=response.body().string();
                        if (res.equals("登陆成功")){
                            onLoginSuccess();
                        }
                    }
                });
            }
        }).start();

    }

    private void onLoginSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                signButton.setEnabled(true);
                Toast.makeText(SignupActivity.this, "success", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(SignupActivity.this,MainActivity.class);
                setResult(RESULT_OK, null);
                startActivity(intent);
                finish();
            }
        });

    }
    //验证帐号是否符合
    public boolean validate(){
        boolean valid=true;
        String mInput_phone=signPhone.getText().toString();
        String mInput_password=signPassword.getText().toString();
        String username=signUsername.getText().toString();
        String inputCode=signCaptcha.getText().toString();
        if (!mInput_phone.isEmpty()){
            //Toast.makeText(this, "账号不为空", Toast.LENGTH_SHORT).show();
            if (!isMobileNO(mInput_phone)){
                signPhone.setError("账号不存在");
                valid=false;
            }else{
                signPhone.setError(null);
            }

        }else {
            signPhone.setError("账号为空");
            valid=false;
        }

        if (mInput_password.isEmpty() || mInput_password.length()<4 || mInput_password.length()>10){
            signPassword.setError("密码长度为4~10位");
            valid=false;
        }

        if (username.isEmpty() || username.length()<4 || username.length()>10){
            signUsername.setError("密码长度为4~10位");
            valid=false;
        }

        if (inputCode.isEmpty()||!inputCode.equals(code)) {
           valid=false;
            Toast.makeText(SignupActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
        }
        return valid;
    }


    private void onLoginFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SignupActivity.this,"Login failed",Toast.LENGTH_SHORT).show();
                signButton.setEnabled(true);
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        passwordWrapper.setHint("密码");
        usernameWrapper.setHint("用户名");
        phoneWrapper.setHint("手机号");
        captchaWrapper.setHint("验证码");
        toolbar.setTitle("注册");
        setSupportActionBar(toolbar);

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
