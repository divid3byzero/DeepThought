package net.deepthought.data.search;

import net.deepthought.data.model.Entry;
import net.deepthought.data.model.Tag;
import net.deepthought.data.search.specific.FindAllEntriesHavingTheseTagsResult;

import java.util.Collection;

/**
 * <p>
 *  Uses that methods from LuceneSearchEngine and DatabaseSearchEngine which in my tests turned out to be the fastest for a specific task
 *  in order to get the fastest and most efficient search engine possible.
 * </p>
 */
public class LuceneAndDatabaseSearchEngine extends LuceneSearchEngine {

  protected DatabaseSearchEngine databaseSearchEngine = new DatabaseSearchEngine();


  public LuceneAndDatabaseSearchEngine() {

  }

  @Override
  public void getEntriesWithoutTags(SearchCompletedListener<Collection<Entry>> listener) {
    databaseSearchEngine.getEntriesWithoutTags(listener);
  }

  @Override
  protected void findAllEntriesHavingTheseTagsAsync(Collection<Tag> tagsToFilterFor, SearchCompletedListener<FindAllEntriesHavingTheseTagsResult> listener) {
    databaseSearchEngine.findAllEntriesHavingTheseTagsAsync(tagsToFilterFor, listener);
  }

  // no, don't filter Tags and Persons with db, even though it's faster, as SQLite does not support case insensitive Unicode sorting
  // (see for example https://stackoverflow.com/questions/3317672/how-to-order-sqlite-results-containing-umlauts-and-other-special-characters // }

//  @Override
//  protected void filterTags(net.deepthought.data.search.specific.FilterTagsSearch search, String[] tagNamesToFilterFor) {
//    databaseSearchEngine.filterTags(search, tagNamesToFilterFor);
//  }
//
//  @Override
//  protected void filterPersons(Search<Person> search, String lastNameFilter, String firstNameFilter) {
//    databaseSearchEngine.filterPersons((Search<Person>) search, (String) lastNameFilter, (String) firstNameFilter);
//  }
//
//  @Override
//  protected void filterPersons(Search<Person> search, String personFilter) {
//    databaseSearchEngine.filterPersons(search, personFilter);
//  }
}
