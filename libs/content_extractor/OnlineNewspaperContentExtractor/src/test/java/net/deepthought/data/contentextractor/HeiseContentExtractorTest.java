package net.deepthought.data.contentextractor;

import net.deepthought.data.contentextractor.preview.ArticlesOverviewItem;
import net.deepthought.data.contentextractor.preview.ArticlesOverviewListener;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ganymed on 13/06/15.
 */
public class HeiseContentExtractorTest extends GermanOnlineNewspaperContentExtractorTestBase {

  protected HeiseContentExtractor heiseContentExtractor = null;


  @Override
  protected OnlineNewspaperContentExtractorBase createOnlineNewspaperContentExtractor() {
    heiseContentExtractor = new HeiseContentExtractor();
    return heiseContentExtractor;
  }


  @Test
  public void importMerkelAufDemItGipfelArticle() {
    EntryCreationResult creationResult = testImportArticle("http://www.heise.de/newsticker/meldung/Merkel-auf-dem-IT-Gipfel-Datenschutz-darf-Big-Data-nicht-verhindern-2980126.html");
    testImportedArticleValues(creationResult, 5685, "19.11.2015", "Datenschutz darf Big Data nicht verhindern", "Merkel auf dem IT-Gipfel",
        "Bundeskanzlerin Angela Merkel setzt sich dafür ein, Prinzipien wie Zweckbindung und Datensparsamkeit im EU-Datenschutzrecht auszuhebeln. Es sei \"existenziell notwendig\", den entsprechenden Kompromiss zu erhalten.");
  }

  @Test
  public void importArticleContainingImages() {
    EntryCreationResult creationResult = testImportArticle("http://www.heise.de/newsticker/meldung/IT-Gipfel-Gabriel-plaediert-fuer-Datensouveraenitaet-statt-Datenschutz-2966141.html");
    testImportedArticleValues(creationResult, 6027, "19.11.2015", "Gabriel plädiert für Datensouveränität statt Datenschutz", "IT-Gipfel",
        "\"Wir brauchen ein anderes Verständnis von Datenschutz\", verkündete Wirtschaftsminister Sigmar Gabriel zur Eröffnung des 9. Nationalen IT-Gipfels. Datensparsamkeit passe nicht zum Geschäftsmodell Big Data.");
  }

  @Test
  public void importArticleContainingVideo() {
    EntryCreationResult creationResult = testImportArticle("http://www.heise.de/newsticker/meldung/Hacker-starten-Stratosphaerenballon-um-Drohnen-Funk-mitzuschneiden-2823100.html");
    testImportedArticleValues(creationResult, 5514, "22.09.2015", "Hacker starten Stratosphärenballon, um Drohnen-Funk mitzuschneiden", "",
        "Mit der geeigneten Technik kann man große Teile des Spektrums scannen und Funkverkehr aufspüren – vom Boden aus fehlt aber das, was in großen Höhen gefunkt wird, zum Beispiel von Drohnen zu Satelliten.");
  }


  @Test
  public void testGetArticlesOverview() {
    final List<ArticlesOverviewItem> allItems = new ArrayList<>();
    final AtomicInteger partialItemsExtractionCall = new AtomicInteger();
    final CountDownLatch getArticlesOverviewLatch = new CountDownLatch(1);

    contentExtractor.getArticlesOverviewAsync(new ArticlesOverviewListener() {
      @Override
      public void overviewItemsRetrieved(IOnlineArticleContentExtractor contentExtractor, Collection<ArticlesOverviewItem> items, boolean isDone) {
        partialItemsExtractionCall.incrementAndGet();
        allItems.addAll(items);

        Assert.assertNotNull(contentExtractor);
        Assert.assertFalse(items.size() == 0);

        if (isDone)
          getArticlesOverviewLatch.countDown();
      }
    });

    try { getArticlesOverviewLatch.await(100, TimeUnit.SECONDS); } catch(Exception ex) { }

    Assert.assertEquals(2, partialItemsExtractionCall.get());
    Assert.assertTrue(allItems.size() > 10);
  }


}
