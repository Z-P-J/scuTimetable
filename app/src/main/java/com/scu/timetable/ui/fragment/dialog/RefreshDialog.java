package com.scu.timetable.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scu.timetable.R;
import com.scu.timetable.utils.CaptchaFetcher;
import com.scu.timetable.utils.EventBus;
import com.scu.timetable.utils.LoginUtil;
import com.scu.timetable.utils.TimetableHelper;
import com.zpj.fragmentation.dialog.base.CardDialogFragment;
import com.zpj.toast.ZToast;

public class RefreshDialog extends CardDialogFragment {

    private static final String TAG = "RefreshPopup";

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_refresh;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        LinearLayout container = findViewById(R.id.container);
        LinearLayout statusLayout = findViewById(R.id.layout_status);
        TextView loadingDialogText = findViewById(R.id.loading_dialog_text);

        ImageView imgCatpcha = findViewById(R.id.img_captcha);
        CaptchaFetcher.fetchCaptcha(imgCatpcha);
        ImageView btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> dismiss());
        TextView changeCatpcha = findViewById(R.id.change_captcha);
        changeCatpcha.setOnClickListener(v -> CaptchaFetcher.fetchCaptcha(imgCatpcha));
        TextView btnRefresh = findViewById(R.id.btn_refresh);
        EditText captchaEdit = findViewById(R.id.captcha);

        btnRefresh.setOnClickListener(v -> {
            String captcha = captchaEdit.getText().toString();
            if (TextUtils.isEmpty(captcha)) {
                ZToast.normal("验证码为空！");
                return;
            }
            if (TimetableHelper.isVisitorMode()) {
                ZToast.normal("您当前正处于游客模式，无法刷新课表！");
                return;
            }
//            if (dialog instanceof ZDialog) {
//                ZDialog zDialog = ((ZDialog) dialog);
//                zDialog.setCancelable(false);
//                zDialog.setCanceledOnTouchOutside(false);
//            }
            statusLayout.setVisibility(View.VISIBLE);
            LoginUtil.with()
                    .setLoginCallback(new LoginUtil.LoginCallback() {

                        private void onError() {
                            loadingDialogText.setText("登录失败！");
                            statusLayout.setVisibility(View.GONE);
                            ZToast.normal("登录失败，请重试！");
                            CaptchaFetcher.fetchCaptcha(imgCatpcha);
                            captchaEdit.setText("");
//                            if (dialog instanceof ZDialog) {
//                                ZDialog zDialog = ((ZDialog) dialog);
//                                zDialog.setCancelable(true);
//                                zDialog.setCanceledOnTouchOutside(true);
//                            }
                        }

                        @Override
                        public void onGetCookie(String cookie) { }

                        @Override
                        public void onLoginSuccess() {
                            Log.d(TAG, "onLoginSuccess");
                            loadingDialogText.setText("登录成功!获取课表信息中。。。");
                        }

                        @Override
                        public void onLoginFailed() {
                            Log.d(TAG, "onLoginFailed");
                            onError();
                        }

                        @Override
                        public void onLoginError(String errorMsg) {
                            Log.d(TAG, "onLoginError");
                            onError();
                        }

                        @Override
                        public void onGetTimetableFinished() {
                            Log.d(TAG, "onGetTimetable onGetTimetableFinished");
                            ZToast.normal("刷新课表成功！");
                            EventBus.sendRefreshEvent();
                            dismiss();

                        }

                    })
                    .login(captcha, TimetableHelper.getCurrentSemesterCode());
        });
    }

}
