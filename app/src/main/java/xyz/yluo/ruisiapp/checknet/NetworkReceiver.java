package xyz.yluo.ruisiapp.checknet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import xyz.yluo.ruisiapp.PublicData;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-4-13.
 * 检测网络变化切换内外网
 */
public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        checkNetWork(context);
    }

    //检测网络状态 有无/校园网/外网
    private void checkNetWork(Context context){
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            new CheckNet(context).startCheck(new CheckNetResponse() {
                @Override
                public void onFinish(int type, String response) {
                    checknet(type);
                }
            });
        }
        else {
            Toast.makeText(context, "与服务器断开连接,请打开网络", Toast.LENGTH_SHORT).show();
        }
    }
    private void checknet(int type){
        if(type==1){
            //校园网
            System.out.println("school net");
            PublicData.IS_SCHOOL_NET = true;
            PublicData.BASE_URL = UrlUtils.getBaseUrl(true);
        }else if(type==2){
            //外网
            System.out.println("out net");
            PublicData.BASE_URL = UrlUtils.getBaseUrl(false);
            PublicData.IS_SCHOOL_NET = false;
        }
    }
}