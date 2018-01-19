package net.oschina.app.improve.main.synthesize.english.detail;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.Article;
import net.oschina.app.improve.bean.Collection;
import net.oschina.app.improve.bean.base.PageBean;
import net.oschina.app.improve.bean.base.ResultBean;
import net.oschina.app.improve.bean.comment.Comment;
import net.oschina.app.improve.main.update.OSCSharedPreference;
import net.oschina.app.ui.empty.EmptyLayout;
import net.oschina.common.utils.CollectionUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 英文详情界面
 * Created by huanghaibin on 2018/1/15.
 */

class EnglishArticleDetailPresenter implements EnglishArticleDetailContract.Presenter {
    private final EnglishArticleDetailContract.View mView;
    private final EnglishArticleDetailContract.EmptyView mEmptyView;
    private static final int TYPE_ENGLISH = 8000;//获取英文
    private Article mArticle;
    private String mNextToken;

    EnglishArticleDetailPresenter(EnglishArticleDetailContract.View mView,
                                  EnglishArticleDetailContract.EmptyView emptyView,
                                  Article mArticle) {
        this.mView = mView;
        this.mEmptyView = emptyView;
        this.mArticle = mArticle;
        this.mView.setPresenter(this);
    }

    @Override
    public void onRefreshing() {
        OSChinaApi.getArticleRecommends(
                mArticle.getKey(),
                OSCSharedPreference.getInstance().getDeviceUUID(),
                TYPE_ENGLISH,
                "",
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        try {
                            mView.showMoreMore();
                            mView.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean<PageBean<Article>>>() {
                            }.getType();
                            ResultBean<PageBean<Article>> bean = new Gson().fromJson(responseString, type);
                            if (bean != null && bean.isSuccess()) {
                                PageBean<Article> pageBean = bean.getResult();
                                mNextToken = pageBean.getNextPageToken();
                                List<Article> list = pageBean.getItems();
                                for (Article article : list) {
                                    article.setImgs(removeImgs(article.getImgs()));
                                }
                                mView.onRefreshSuccess(list);
                                if (list.size() == 0) {
                                    mView.showMoreMore();
                                }
                            } else {
                                mView.showMoreMore();
                            }
                            mView.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mView.showMoreMore();
                            mView.onComplete();
                        }
                    }
                });
    }

    @Override
    public void onLoadMore() {
        OSChinaApi.getArticleRecommends(
                mArticle.getKey(),
                OSCSharedPreference.getInstance().getDeviceUUID(),
                TYPE_ENGLISH,
                mNextToken,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        try {
                            mView.showMoreMore();
                            mView.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean<PageBean<Article>>>() {
                            }.getType();
                            ResultBean<PageBean<Article>> bean = new Gson().fromJson(responseString, type);
                            if (bean != null && bean.isSuccess()) {
                                PageBean<Article> pageBean = bean.getResult();
                                mNextToken = pageBean.getNextPageToken();
                                List<Article> list = pageBean.getItems();
                                for (Article article : list) {
                                    article.setImgs(removeImgs(article.getImgs()));
                                }
                                mView.onLoadMoreSuccess(list);
                                if (list.size() == 0) {
                                    mView.showMoreMore();
                                }
                            } else {
                                mView.showNetworkError(R.string.footer_type_net_error);
                            }
                            mView.onComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mView.showMoreMore();
                            mView.onComplete();
                        }
                    }
                });
    }

    @Override
    public void getArticleDetail() {
        addClickCount();
        OSChinaApi.getArticleDetail(mArticle.getKey(),
                OSCSharedPreference.getInstance().getDeviceUUID(),
                TYPE_ENGLISH,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mEmptyView.showErrorLayout(EmptyLayout.NETWORK_ERROR);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean<Article>>() {
                            }.getType();
                            ResultBean<Article> bean = new Gson().fromJson(responseString, type);
                            if (bean != null && bean.isSuccess() && bean.getResult() != null) {
                                mArticle = bean.getResult();
                                mView.showGetDetailSuccess(mArticle);
                                mEmptyView.showGetDetailSuccess(mArticle);
                            } else {
                                mEmptyView.showErrorLayout(EmptyLayout.NODATA);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mEmptyView.showErrorLayout(EmptyLayout.NODATA);
                        }
                    }
                });
    }

    @Override
    public void putArticleComment(String content, long referId, long reAuthorId) {
        OSChinaApi.pubArticleComment(mArticle.getKey(),
                content,
                TYPE_ENGLISH,
                referId,
                reAuthorId,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mView.showNetworkError(R.string.tip_network_error);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean<Comment>>() {
                            }.getType();

                            ResultBean<Comment> resultBean = AppOperator.createGson().fromJson(responseString, type);
                            if (resultBean.isSuccess()) {
                                mArticle.setCommentCount(mArticle.getCommentCount() + 1);
                                Comment respComment = resultBean.getResult();
                                if (respComment != null) {
                                    getArticleDetail();
                                    mView.showCommentSuccess(respComment);
                                    mEmptyView.showCommentSuccess(respComment);
                                }
                            } else {
                                mView.showCommentError(resultBean.getMessage());
                                mEmptyView.showCommentError(resultBean.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(statusCode, headers, responseString, e);
                            mView.showCommentError("评论失败");
                            mEmptyView.showCommentError("评论失败");
                        }
                    }
                });
    }

    @Override
    public void addClickCount() {
        OSChinaApi.addClickCount(mArticle.getKey(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });
    }

    @Override
    public void fav() {
        OSChinaApi.articleFav(new Gson().toJson(mArticle),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        mEmptyView.showFavError();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            Type type = new TypeToken<ResultBean<Collection>>() {
                            }.getType();
                            ResultBean<Collection> resultBean = AppOperator.createGson().fromJson(responseString, type);
                            if (resultBean != null && resultBean.isSuccess()) {
                                Collection collection = resultBean.getResult();
                                mArticle.setFavorite(collection.isFavorite());
                                mEmptyView.showFavReverseSuccess(collection.isFavorite());
                            } else {
                                mEmptyView.showFavError();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onFailure(statusCode, headers, responseString, e);
                        }
                    }
                });
    }

    @Override
    public void scrollToTop() {
        mView.showScrollToTop();
    }

    private static String[] removeImgs(String[] imgs) {
        if (imgs == null || imgs.length == 0)
            return null;
        List<String> list = new ArrayList<>();
        for (String img : imgs) {
            if (!TextUtils.isEmpty(img)) {
                if (img.startsWith("http")) {
                    list.add(img);
                }
            }
        }
        return CollectionUtil.toArray(list, String.class);
    }
}
