package xyz.yluo.ruisiapp.activity;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.myhttp.HttpUtil;
import xyz.yluo.ruisiapp.myhttp.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.IntentUtils;
import xyz.yluo.ruisiapp.widget.myhtmlview.HtmlView;


/**
 * Created by yluo on 2015/10/5 0005.
 * 关于页面
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));
        }
        TextView version = (TextView) findViewById(R.id.version);
        TextView serverVersion = (TextView) findViewById(R.id.server_version);

        findViewById(R.id.btn_back).setOnClickListener(view -> finish());


        String ss = "<b>西电睿思手机客户端</b><br />功能不断完善中，bug较多还请多多反馈......<br />" +
                "bug反馈:<br />" +
                "1.到 <a href=\"forum.php?mod=viewthread&tid=" + App.POST_TID + "&mobile=2\">本帖</a> 回复<br />" +
                "2.本站 <a href=\"home.php?mod=space&uid=252553&do=profile&mobile=2\">@谁用了FREEDOM</a><br />" +
                "3.本站 <a href=\"home.php?mod=space&uid=261098&do=profile&mobile=2\">@wangfuyang</a><br />" +
                "4.github提交 <a href=\"https://github.com/freedom10086/Ruisi\">点击这儿<br /></a><br />" +
                "5.xhinliang xhinliang@gmail.com";

        HtmlView htmlView = (HtmlView) findViewById(R.id.html_text);
        htmlView.setHtmlText(ss, false);

        PackageInfo info = null;
        PackageManager manager = getPackageManager();
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int versionCode = 0;
        if (info != null) {
            String version_name = info.versionName;
            versionCode = info.versionCode;
            String a = "当前版本:" + version_name;
            version.setText(a);
        }

        findViewById(R.id.fab).setOnClickListener(v -> Snackbar.make(v, "你要提交bug或者建议吗?", Snackbar.LENGTH_LONG)
                .setAction("确定", view -> {
                    String user = App.getName(AboutActivity.this);
                    if (user != null) {
                        user = "by:" + user;
                    }
                    IntentUtils.sendMail(getApplicationContext(), user);
                })
                .show());

        int finalVersionCode = versionCode;
        HttpUtil.get(this, App.CHECK_UPDATE_URL, new ResponseHandler() {
            @Override
            public void onSuccess(byte[] response) {
                String res = new String(response);
                int ih = res.indexOf("keywords");
                int h_start = res.indexOf('\"', ih + 15);
                int h_end = res.indexOf('\"', h_start + 1);
                String title = res.substring(h_start + 1, h_end);
                if (title.contains("code")) {
                    SharedPreferences.Editor editor = getSharedPreferences(App.MY_SHP_NAME, MODE_PRIVATE).edit();
                    editor.putLong(App.CHECK_UPDATE_KEY, System.currentTimeMillis());
                    editor.apply();
                    int st = title.indexOf("code");
                    int code = GetId.getNumber(title.substring(st));
                    if (code > finalVersionCode) {
                        serverVersion.setText("检测到新版本点击查看");
                        serverVersion.setOnClickListener(view -> PostActivity.open(AboutActivity.this, App.CHECK_UPDATE_URL, "谁用了FREEDOM"));
                        return;
                    }
                }

                serverVersion.setText("当前已是最新版本");
            }

            @Override
            public void onFailure(Throwable e) {
                super.onFailure(e);
                serverVersion.setText("检测新版本失败...");
            }
        });
    }

}
