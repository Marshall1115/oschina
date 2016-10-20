package net.oschina.app.improve.share.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import net.oschina.app.R;
import net.oschina.app.improve.share.adapter.ShareActionAdapter;
import net.oschina.app.improve.share.bean.ShareItem;
import net.oschina.app.improve.share.constant.OpenConstant;
import net.oschina.app.util.TDevice;
import net.oschina.open.bean.Share;
import net.oschina.open.constants.OpenConstants;
import net.oschina.open.factory.OpenShare;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fei on 2016/10/10.
 * desc:
 */

public class ShareDialogBuilder extends AlertDialog.Builder implements DialogInterface
                                                                               .OnCancelListener,
                                                                       DialogInterface
                                                                               .OnDismissListener,
                                                                       ShareActionAdapter
                                                                               .OnItemClickListener {

    private static final String TAG = "ShareDialogBuilder";

    private Share mShare;
    private Activity mActivity;
    private AlertDialog mAlertDialog;
    private int openType;

    private IUiListener mIUiListener;


    public ShareDialogBuilder(@NonNull Context context) {
        super(context);
        // initListener();
    }

    public ShareDialogBuilder(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        // initListener();
    }

    public void addIUiListener(IUiListener IUiListener) {
        mIUiListener = IUiListener;
    }

    //    private void initListener() {
//        setOnCancelListener(this);
//        setOnDismissListener(this);
//    }

    private List<ShareItem> initAdapterData() {
        List<ShareItem> shareActions = new ArrayList<>(6);

        //1.朋友圈
        ShareItem shareAction0 = new ShareItem();
        shareAction0.setIconId(R.drawable.share_icon_wechatfriends_selector);
        shareAction0.setNameId(R.string.platform_weichat_circle);

        shareActions.add(shareAction0);

        //2.微信
        ShareItem shareAction1 = new ShareItem();
        shareAction1.setIconId(R.drawable.share_icon_wechat_selector);
        shareAction1.setNameId(R.string.platform_wechat);

        shareActions.add(shareAction1);

        //3.新浪微博
        ShareItem shareAction2 = new ShareItem();
        shareAction2.setIconId(R.drawable.share_icon_sinaweibo_selector);
        shareAction2.setNameId(R.string.platform_sina);

        shareActions.add(shareAction2);

        //4.QQ
        ShareItem shareAction3 = new ShareItem();
        shareAction3.setIconId(R.drawable.share_icon_qq_selector);
        shareAction3.setNameId(R.string.platform_qq);

        shareActions.add(shareAction3);

        //5.复制链接
        ShareItem shareAction4 = new ShareItem();
        shareAction4.setIconId(R.drawable.share_icon_copy_link_selector);
        shareAction4.setNameId(R.string.platform_copy_link);

        shareActions.add(shareAction4);

        //6.更多
        ShareItem shareAction5 = new ShareItem();
        shareAction5.setIconId(R.drawable.share_icon_more_selector);
        shareAction5.setNameId(R.string.platform_more_option);

        shareActions.add(shareAction5);

        return shareActions;
    }

    private ShareActionAdapter initAdapter() {
        ShareActionAdapter shareActionAdapter = new ShareActionAdapter(initAdapterData());
        shareActionAdapter.addOnItemClickListener(this);

        return shareActionAdapter;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AlertDialog create() {

        AlertDialog alertDialog = super.create();
        Window window = alertDialog.getWindow();

        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager m = window.getWindowManager();
            Display d = m.getDefaultDisplay();
            WindowManager.LayoutParams p = window.getAttributes();
            p.width = d.getWidth();
            window.setAttributes(p);
        }
        this.mAlertDialog = alertDialog;
        return alertDialog;
    }

    @Override
    public AlertDialog.Builder setView(int layoutResId) {

        AlertDialog.Builder builder = super.setView(layoutResId);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View contentView = inflater.inflate(layoutResId, null, false);
        RecyclerView shareRecycle = (RecyclerView) contentView.findViewById(R.id.share_recycler);
        shareRecycle.setAdapter(initAdapter());
        shareRecycle.setItemAnimator(new DefaultItemAnimator());
        shareRecycle.setLayoutManager(new GridLayoutManager(getContext(), 3));
        builder.setView(contentView);

        return builder;
    }

    public ShareDialogBuilder addShare(Share share) {
        this.mShare = share;
        return this;
    }

    public ShareDialogBuilder boundActivity(Activity activity) {
        this.mActivity = activity;
        return this;
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }


    @Override
    public void onItemClick(int position, long itemId) {

        Share share = getShare();

        switch (position) {
            //朋友圈
            case 0:
                openType = OpenConstants.WECHAT;

                OpenShare<IWXAPI> iwxapiOpenShare = new OpenShare<>();
                share.setShareScene(Share.SHARE_TIMELINE);
                iwxapiOpenShare.addAppId(OpenConstant.WECHAT_APP_ID)
                        .toShare(mActivity.getApplicationContext(), mActivity, OpenConstants.WECHAT, share)
                        .addSendReqCallback(null);
                break;
            //微信会话
            case 1:

                openType = OpenConstants.WECHAT;

                share.setShareScene(Share.SHARE_SESSION);

                OpenShare<IWXAPI> iwxapiOpenShare2 = new OpenShare<>();
                iwxapiOpenShare2.addAppId(OpenConstant.WECHAT_APP_ID)
                        .toShare(mActivity.getApplicationContext(), mActivity, OpenConstants.WECHAT, share)
                        .addSendReqCallback(null);

                break;
            //新浪微博
            case 2:

                openType = OpenConstants.SINA;

                share.setShareScene(Share.SHARE_SESSION);

                OpenShare<SsoHandler> openShare = new OpenShare<>();
                openShare.addAppKey(OpenConstant.WB_APP_KEY)
                        .toShare(mActivity.getApplicationContext(), mActivity, OpenConstants.SINA, share);

                break;
            //QQ
            case 3:

                openType = OpenConstants.TENCENT;

                share.setShareScene(Share.SHARE_SESSION);

                OpenShare<Tencent> tencentOpenShare = new OpenShare<>();
                tencentOpenShare.addAppId(OpenConstant.QQ_APP_ID)
                        .addAppKey(OpenConstant.QQ_APP_KEY)
                        .addIUiListener(mIUiListener)
                        .toShare(mActivity.getApplicationContext(), mActivity, OpenConstants.TENCENT, share);

                break;
            //复制链接
            case 4:
                TDevice.copyTextToBoard(share.getUrl());
                break;
            //更多(调用系统分享)
            case 5:
                OpenShare.showSystemShareOption(getContext(), share);
                break;
            default:
                OpenShare.showSystemShareOption(getContext(), share);
                break;
        }

        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
            mAlertDialog.cancel();
        }
    }

    public Share getShare() {
        return mShare;
    }

    public int getOpenType() {
        return openType;
    }

    /**
     * 调用系统安装的应用分享
     *
     * @param title title
     * @param url   url
     */
    private void showSystemShareOption(final String title, final String url) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
        intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
        getContext().startActivity(Intent.createChooser(intent, "选择分享"));
    }
}
