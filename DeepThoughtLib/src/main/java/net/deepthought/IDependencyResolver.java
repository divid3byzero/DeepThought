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
import net.deepthought.platform.IPlatformTools;
import net.deepthought.plugin.IPluginManager;
import net.deepthought.util.IThreadPool;

/**
 * Created by ganymed on 05/01/15.
 */
public interface IDependencyResolver {

  IThreadPool createThreadPool();

  IEntityManager createEntityManager(EntityManagerConfiguration configuration) throws Exception;

  IDataManager createDataManager(IEntityManager entityManager);

  IPlatformTools createPlatformTools();

  IBackupManager createBackupManager();

  IDataComparer createDataComparer();

  IDataMerger createDataMerger();

  ILanguageDetector createLanguageDetector();

  ISearchEngine createSearchEngine();

  IHtmlHelper createHtmlHelper();

  IFileDownloader createDownloader();

  IPluginManager createPluginManager();

  IContentExtractorManager createContentExtractorManager();

  IDeepThoughtsConnector createDeepThoughtsConnector();

}
