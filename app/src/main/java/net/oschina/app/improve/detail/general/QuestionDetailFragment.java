package net.oschina.app.improve.detail.general;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.simple.Author;
import net.oschina.app.improve.bean.simple.UserRelation;
import net.oschina.app.improve.detail.v2.DetailFragment;
import net.oschina.app.improve.user.activities.OtherUserHomeActivity;
import net.oschina.app.improve.widget.PortraitView;
import net.oschina.app.improve.widget.SimplexToast;
import net.oschina.app.util.StringUtils;
import net.oschina.common.widget.FlowLayout;

import butterknife.Bind;
import butterknife.OnLongClick;

/**
 * Created by haibin
 * on 2016/12/15.
 */

public class QuestionDetailFragment extends DetailFragment {
    @Bind(R.id.tv_title)
    TextView mTextTitle;
    @Bind(R.id.tv_author)
    TextView mTextAuthor;
    @Bind(R.id.tv_pub_date)
    TextView mTextPubDate;
//    @Bind(R.id.tv_info_view)
//    TextView mTextViewCount;
//    @Bind(R.id.tv_info_comment)
//    TextView mTextCommentCount;
    @Bind(R.id.btn_relation)
    Button mBtnRelation;
    @Bind(R.id.iv_avatar)
    PortraitView mImageAvatar;
    public static QuestionDetailFragment newInstance() {
        return new QuestionDetailFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_question_detail_v2;
    }

    @Override
    protected void initData() {
        super.initData();
        CACHE_CATALOG = OSChinaApi.CATALOG_QUESTION;
        mImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBean != null && mBean.getAuthor() != null) {
                    OtherUserHomeActivity.show(mContext, mBean.getAuthor());
                }
            }
        });
        mBtnRelation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBean.getAuthor() != null) {
                    mPresenter.addUserRelation(mBean.getAuthor().getId());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void showGetDetailSuccess(SubBean bean) {
        super.showGetDetailSuccess(bean);
        mTextTitle.setText(bean.getTitle());
        if (bean.getAuthor() != null)
            mTextAuthor.setText(bean.getAuthor().getName());
        mTextPubDate.setText(StringUtils.formatYearMonthDay(bean.getPubDate()));
//        mTextCommentCount.setText(String.valueOf(bean.getStatistics().getComment()));
//        mTextViewCount.setText(String.valueOf(bean.getStatistics().getView()));
        mBtnRelation.setText(bean.getAuthor().getRelation() < UserRelation.RELATION_ONLY_HER
                ? "已关注" : "关注");

        Author author = bean.getAuthor();
        if (author != null) {
            mTextAuthor.setText(author.getName());
            mImageAvatar.setup(author);
        }
    }

    @Override
    public void showAddRelationSuccess(boolean isRelation, int strId) {
        mBtnRelation.setText(isRelation ? "已关注" : "关注");
        SimplexToast.show(mContext, strId);
    }

    @Override
    protected int getCommentOrder() {
        return OSChinaApi.COMMENT_NEW_ORDER;
    }

    @OnLongClick(R.id.tv_title)
    boolean onLongClickTitle() {
        showCopyTitle();
        return true;
    }
}
