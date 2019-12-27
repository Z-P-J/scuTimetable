package com.scu.timetable.ui.activity;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.scu.timetable.R;
import com.scu.timetable.utils.AnimatorUtil;
import com.scu.timetable.utils.CaptchaFetcher;
import com.scu.timetable.utils.EncryptionUtils;
import com.scu.timetable.utils.LoginUtil;
import com.scu.timetable.utils.TimetableHelper;
import com.scu.timetable.utils.content.SPHelper;
import com.zpj.popupmenuview.CustomPopupMenuView;

import org.json.JSONObject;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * @author Z-P-J
 * @date 2019
 */
public final class LoginActivity extends SupportActivity implements View.OnClickListener, LoginUtil.LoginCallback {

    private EditText userName;
    private EditText password;
    private EditText captcha;
    private ImageView captchaImg;
//    private String[] shijian = {"8:15-9:00", "9:10-9:55", "10:15-11:00", "11:10-11:55", "11:55-1:50", "13:50-14:35", "14:45-15:30", "15:40-16:25", "16:50-17:35", "17:45-18:30", "18:30-19:20", "19:20-20:05", "20:15-21:00", "21:10-21:55", "22:05-22:50"};

    private String cookie;

    private View progress;

    private TextView msgText;

    private RelativeLayout middleLayout;

    private LinearLayout visitorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TimetableHelper.closeVisitorMode();
        LoginUtil.with().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TimetableHelper.isLogined(LoginActivity.this)) {
                    updateWidget(true);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    updateWidget(false);
                    initView();
//                    getCookie();
                    LoginUtil.with()
                            .setLoginCallback(LoginActivity.this)
                            .getCookie();
                }
            }
        }, 500);

    }

    private void updateWidget(boolean isLogined) {
        //发送广播。更新桌面插件
        Intent intent = new Intent();
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        intent.putExtra("islogined", isLogined);
        getApplicationContext().sendBroadcast(intent);
    }

    public void initView() {
        middleLayout = findViewById(R.id.layout_middle);

//        middleLayout.setVisibility(View.VISIBLE);
        AnimatorUtil.showViewAnimator(middleLayout, 1000);

        TextView mBtnLogin = findViewById(R.id.main_btn_login);
        mBtnLogin.setOnClickListener(this);

        visitorLayout = findViewById(R.id.layout_visitor);
        visitorLayout.setVisibility(View.VISIBLE);
        TextView visitorLogin = findViewById(R.id.visitor_mode);
        //下划线
        visitorLogin.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        //抗锯齿
        visitorLogin.getPaint().setAntiAlias(true);
        visitorLogin.setOnClickListener(this);
        ImageView btnInfo = findViewById(R.id.btn_info);
        btnInfo.setOnClickListener(this);


        progress = findViewById(R.id.layout_progress);
        msgText = findViewById(R.id.id_tv_loading_dialog_text);
        userName = findViewById(R.id.user_name);
        password = findViewById(R.id.password);
        captcha = findViewById(R.id.captcha);
        captchaImg = findViewById(R.id.img_captcha);
        captchaImg.setOnClickListener(this);

        TextView changeCaptcha = findViewById(R.id.change_captcha);
        changeCaptcha.setOnClickListener(this);

        //http://202.115.47.141/img/captcha.jpg?60

        userName.setText(EncryptionUtils.decryptByAES(SPHelper.getString("user_name", "")));
        password.setText(EncryptionUtils.decryptByAES(SPHelper.getString("password", "")));

    }

    private void onError() {
        AnimatorUtil.hideViewAnimator(progress, 500, new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                progress.setVisibility(View.GONE);
                AnimatorUtil.showViewAnimator(middleLayout, 500);
                visitorLayout.setVisibility(View.VISIBLE);
                captcha.setText("");
                CaptchaFetcher.fetchCaptcha(cookie, captchaImg);
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });
    }

    private void login() {
        if (progress.getVisibility() == View.VISIBLE) {
            return;
        }
        if (userName.getText().toString().isEmpty() || password.getText().toString().isEmpty() || captcha.getText().toString().isEmpty()) {
            AToast.normal("请输入正确的信息");
        } else {
            AnimatorUtil.hideViewAnimator(middleLayout, 500, new AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) { }

                @Override
                public void onAnimationEnd(Animator animation) {
                    progress.setVisibility(View.VISIBLE);
                    AnimatorUtil.shakeAnimator(progress, 1000);
                    middleLayout.setVisibility(View.INVISIBLE);
                    visitorLayout.setVisibility(View.GONE);
                    msgText.setText("登录中...");
//                        login();
                    LoginUtil.with()
                            .setLoginCallback(LoginActivity.this)
                            .login(
                                    userName.getText().toString(),
                                    password.getText().toString(),
                                    captcha.getText().toString()
                            );
                }

                @Override
                public void onAnimationCancel(Animator animation) { }

                @Override
                public void onAnimationRepeat(Animator animation) { }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.change_captcha || id == R.id.img_captcha) {
            if (!TextUtils.isEmpty(cookie)) {
                CaptchaFetcher.fetchCaptcha(cookie, captchaImg);
            }
        } else if (id == R.id.main_btn_login) {
            login();
        } else if (id == R.id.visitor_mode){
            if (TimetableHelper.startVisitorMode(this)) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                //发送广播。更新桌面插件
                updateWidget(true);
            }
        } else if (id == R.id.btn_info) {
            CustomPopupMenuView.with(this, R.layout.layout_text)
                    .setOrientation(LinearLayout.VERTICAL)
//                    .setBackgroundAlpha(this, 0.9f, 500)
                    .setPopupViewBackgroundColor(Color.parseColor("#eeffffff"))
                    .initViews(
                            1,
                            (popupMenuView, itemView, position) -> {
                                TextView titleView = itemView.findViewById(R.id.title);
                                titleView.setText("关于游客模式");
                                TextView contentView = itemView.findViewById(R.id.content);
                                contentView.setText("在该模式下会显示软件内置的一个课程表，并且在该模式下有些功能不能使用！");
                                ImageView btnClose = itemView.findViewById(R.id.btn_close);
                                btnClose.setOnClickListener(v1 -> popupMenuView.dismiss());
                            })
                    .show(v);
        }
    }

    @Override
    public void onGetCookie(String cookie) {
        this.cookie = cookie;
//        Toast.makeText(LoginActivity.this, "cookie=" + cookie, Toast.LENGTH_SHORT).show();
        CaptchaFetcher.fetchCaptcha(cookie, captchaImg);
    }

    @Override
    public void onLoginSuccess() {
//        Toast.makeText(LoginActivity.this, "登录成功!获取课表信息中。。。", Toast.LENGTH_SHORT).show();
        msgText.setText("获取课表数据中...");
        SPHelper.putString("user_name", EncryptionUtils.encryptByAES(userName.getText().toString()));
        SPHelper.putString("password", EncryptionUtils.encryptByAES(password.getText().toString()));
    }

    @Override
    public void onLoginFailed() {
        AToast.normal("登录失败！");
        onError();
    }

    @Override
    public void onLoginError(String errorMsg) {
        AToast.normal("错误信息:" + errorMsg);
        onError();
    }

    @Override
    public void onGetTimetable(JSONObject jsonObject) {
        try {
            TimetableHelper.writeToJson(LoginActivity.this, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void onGetTimetableFinished() {
        msgText.setText("获取课表数据成功！");
        SPHelper.putBoolean("logined", true);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        //发送广播。更新桌面插件
        updateWidget(true);
        finish();
    }

    @Override
    public void onGetSemesters(String json) {
        try {
            TimetableHelper.writeSemesterFile(LoginActivity.this, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}