package net.oschina.app.improve.detail.sign;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import net.oschina.app.R;
import net.oschina.app.improve.base.fragments.BaseFragment;
import net.oschina.app.improve.bean.SignUpEventOptions;
import net.oschina.app.improve.widget.SimplexToast;

import java.util.List;

/**
 * Created by haibin
 * on 2016/12/5.
 */

public class SignUpFragment extends BaseFragment implements SignUpContract.View {
    private SignUpContract.Presenter mPresenter;
    private LinearLayout mLayoutRoot;
    private long mSourceId;

    public static SignUpFragment newInstance(long sourceId) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("sourceId", sourceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sign_up;
    }

    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mSourceId = bundle.getLong("sourceId", 0);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mLayoutRoot = (LinearLayout) mRoot.findViewById(R.id.ll_root);
    }

    @Override
    public void showGetSignUpOptionsSuccess(List<SignUpEventOptions> options) {
        mLayoutRoot.removeAllViews();
        for (SignUpEventOptions option : options) {
            View view = ViewFactory.createView(getActivity(), mInflater, option);
            if (view != null)
                mLayoutRoot.addView(view);
        }
        View view = mInflater.inflate(R.layout.event_sign_up_button, null);
        view.findViewById(R.id.btn_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.signUpEvent(mSourceId);
            }
        });
        mLayoutRoot.addView(view);
    }

    @Override
    public void showSignUpSuccess() {
        SimplexToast.show(mContext, "报名成功");
    }

    @Override
    public void showSignUpError(String message) {
        SimplexToast.show(mContext, message);
    }

    @Override
    public void showInputEmpty(String message) {
        SimplexToast.show(mContext, message);
    }

    @Override
    public void setPresenter(SignUpContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showNetworkError(int strId) {
        SimplexToast.show(mContext, strId);
    }
}
