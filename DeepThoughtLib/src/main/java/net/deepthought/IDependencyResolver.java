package net.deepthought;

import net.deepthought.communication.IDeepThoughtsConnector;
import net.deepthought.data.IDataManager;
import net.deepthought.data.backup.IBackupManager;
import net.deepthought.data.compare.IDataComparer;
import net.deepthought.data.contentextractor.IContentExtractorManager;
import net.deepthought.data.download.IFileDownloader;
import net.deepthought.data.html.IHtmlHelper;
import net.deepthought.data.merger.IDataMerger;
import net.deepthought.data.persistence.EntityManagerConfiguration;
import net.deepthought.data.persistence.IEntityManager;
import net.deepthought.data.search.ISearchEngine;
import net.deepthought.language.ILanguageDetector;
import net.deepthought.platform.IPlatformConfiguration;
import net.deepthought.plugin.IPluginManager;
import net.deepthought.util.IThreadPool;

/**
 * Created by ganymed on 05/01/15.
 */
public interface IDependencyResolver {

  IThreadPool createThreadPool();

  public IEntityManager createEntityManager(EntityManagerConfiguration configuration) throws Exception;

  public IDataManager createDataManager(IEntityManager entityManager);

  public IBackupManager createBackupManager();

  public IDataComparer createDataComparer();

  public IDataMerger createDataMerger();

  public ILanguageDetector createLanguageDetector();

  public ISearchEngine createSearchEngine();

  public IHtmlHelper createHtmlHelper();

  public IFileDownloader createDownloader();

  public IPluginManager createPluginManager();

  public IContentExtractorManager createContentExtractorManager();

  public IDeepThoughtsConnector createDeepThoughtsConnector();

}
