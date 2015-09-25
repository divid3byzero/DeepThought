package net.deepthought.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.deepthought.R;
import net.deepthought.data.contentextractor.IOnlineArticleContentExtractor;
import net.deepthought.data.contentextractor.preview.ArticlesOverviewItem;
import net.deepthought.data.contentextractor.preview.ArticlesOverviewListener;
import net.deepthought.util.IconManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ganymed on 25/09/15.
 */
public class ArticlesOverviewAdapter extends BaseAdapter {

  private final static Logger log = LoggerFactory.getLogger(OnlineArticleContentExtractorsWithArticleOverviewAdapter.class);


  protected Activity context;

  protected IOnlineArticleContentExtractor extractorWithArticleOverview;

  protected List<ArticlesOverviewItem> articlesOverviewItems = new ArrayList<>();


  public ArticlesOverviewAdapter(Activity context, IOnlineArticleContentExtractor extractorWithArticleOverview) {
    this.context = context;
    this.extractorWithArticleOverview = extractorWithArticleOverview;

    retrieveArticles();
  }

  private void retrieveArticles() {
    articlesOverviewItems.clear();
    extractorWithArticleOverview.getArticlesOverviewAsync(new ArticlesOverviewListener() {
      @Override
      public void overviewItemsRetrieved(IOnlineArticleContentExtractor contentExtractor, Collection<ArticlesOverviewItem> items, boolean isDone) {
        articlesOverviewItems.addAll(items);
        notifyDataSetChangedThreadSafe();
      }
    });
  }


  @Override
  public int getCount() {
    return articlesOverviewItems.size();
  }

  public ArticlesOverviewItem getArticleAt(int position) {
    return articlesOverviewItems.get(position);
  }

  @Override
  public Object getItem(int position) {
    return getArticleAt(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if(convertView == null)
      convertView = context.getLayoutInflater().inflate(R.layout.list_item_articles_overview_item, parent, false);

    ArticlesOverviewItem article = getArticleAt(position);

    ImageView imgvwArticlePreviewImage = (ImageView) convertView.findViewById(R.id.imgvwArticlePreviewImage);
    if(article.hasPreviewImageUrl()) {
      IconManager.getInstance().setImageViewToImageFromUrl(imgvwArticlePreviewImage, article.getPreviewImageUrl());
    }
    else
      imgvwArticlePreviewImage.setImageBitmap(null);

    TextView txtvwArticleSubTitle = (TextView)convertView.findViewById(R.id.txtvwArticleSubTitle);
    RelativeLayout.LayoutParams subTitleParams = (RelativeLayout.LayoutParams)txtvwArticleSubTitle.getLayoutParams();

    if(article.hasSubTitle()) {
      txtvwArticleSubTitle.setVisibility(View.VISIBLE);
      txtvwArticleSubTitle.setText(article.getSubTitle());
    }
    else
      txtvwArticleSubTitle.setVisibility(View.GONE);

    TextView txtvwArticleTitle = (TextView)convertView.findViewById(R.id.txtvwArticleTitle);
    RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams)txtvwArticleTitle.getLayoutParams();
    txtvwArticleTitle.setText(article.getTitle());
    if(article.hasSubTitle())
      titleParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
    else
      titleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

    TextView txtvwArticleSummary = (TextView)convertView.findViewById(R.id.txtvwArticleSummary);
    if(article.hasSummary()) {
      txtvwArticleSummary.setVisibility(View.VISIBLE);
      subTitleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
      txtvwArticleSummary.setText(article.getSummary());
    }
    else {
      txtvwArticleSummary.setVisibility(View.GONE);
      subTitleParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
    }

    txtvwArticleSubTitle.setLayoutParams(subTitleParams);

    return convertView;
  }


  protected void notifyDataSetChangedThreadSafe() {
    context.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        notifyDataSetChanged();
      }
    });
  }

}