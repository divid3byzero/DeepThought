package net.deepthought.data.model.ui;

import net.deepthought.Application;
import net.deepthought.data.model.DeepThought;
import net.deepthought.data.model.Entry;
import net.deepthought.data.model.listener.EntityListener;
import net.deepthought.data.persistence.db.BaseEntity;
import net.deepthought.data.search.SearchCompletedListener;
import net.deepthought.util.Localization;

import java.util.Collection;
import java.util.List;

/**
 * Created by ganymed on 09/12/14.
 */
public class EntriesWithoutTagsSystemTag extends SystemTag {

  public final static Long ID = -43L;


  public EntriesWithoutTagsSystemTag(DeepThought deepThought) {
    super(deepThought, Localization.getLocalizedString("system.tag.entries.with.no.tags"));

    deepThought.addEntityListener(deepThoughtListener);

    Application.getSearchEngine().getEntriesWithoutTags(new SearchCompletedListener<Collection<Entry>>() {
      @Override
      public void completed(Collection<Entry> results) {
        filteredEntries = results;
//        callEntityAddedListeners(entries, entry);
      }
    });
  }


  @Override
  public Long getId() {
    return ID;
  }

  protected EntityListener deepThoughtListener = new EntityListener() {
    @Override
    public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

    }

    @Override
    public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {
      if(collection == deepThought.getEntries()) {
        Entry entry = (Entry)addedEntity;
        entry.addEntityListener(entryListener);

        if(entry.hasTags() == false) {
          if(filteredEntries instanceof List)
            ((List) filteredEntries).add(0, entry);
          else
            filteredEntries.add(entry);
          callEntityAddedListeners(filteredEntries, entry);
        }
      }
    }

    @Override
    public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {

    }

    @Override
    public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {
      if(collection == deepThought.getEntries()) {
        Entry entry = (Entry)removedEntity;
        entry.removeEntityListener(entryListener);

        if(entry.hasTags() == false) {
          filteredEntries.remove(entry);
          callEntityRemovedListeners(filteredEntries, entry);
        }
      }
    }
  };


  protected EntityListener entryListener = new EntityListener() {
    @Override
    public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

    }

    @Override
    public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {
      Entry entry = ((Entry)collectionHolder);
      if(collection == entry.getTags()) {
        if(entry.getTags().size() == 1) {
          filteredEntries.remove(entry);
          callEntityRemovedListeners(entries, entry);
        }
      }
    }

    @Override
    public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {

    }

    @Override
    public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {
      Entry entry = ((Entry)collectionHolder);
      if(collection == entry.getTags()) {
        if(entry.hasTags() == false) {
          if(filteredEntries instanceof List)
            ((List) filteredEntries).add(0, entry);
          else
            filteredEntries.add(entry);
          callEntityAddedListeners(entries, entry);
        }
      }
    }
  };

}
