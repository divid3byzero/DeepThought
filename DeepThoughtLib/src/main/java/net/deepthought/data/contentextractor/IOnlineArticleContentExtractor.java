package net.deepthought.data.contentextractor;

import net.deepthought.data.contentextractor.preview.ArticlesOverviewListener;

/**
 * Created by ganymed on 25/04/15.
 */
public interface IOnlineArticleContentExtractor extends IContentExtractor {

  String NoIcon = "No_Icon";


  String getSiteBaseUrl();

  String getIconUrl();

  boolean hasArticlesOverview();

  void getArticlesOverviewAsync(ArticlesOverviewListener listener);

}
