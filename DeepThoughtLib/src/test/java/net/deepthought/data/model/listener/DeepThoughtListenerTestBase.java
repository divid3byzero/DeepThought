package net.deepthought.data.model.listener;

import net.deepthought.Application;
import net.deepthought.data.model.Category;
import net.deepthought.data.model.DataModelTestBase;
import net.deepthought.data.model.DeepThought;
import net.deepthought.data.model.Entry;
import net.deepthought.data.model.IndexTerm;
import net.deepthought.data.model.Tag;
import net.deepthought.data.persistence.db.BaseEntity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

/**
 * Created by ganymed on 09/11/14.
 */
public abstract class DeepThoughtListenerTestBase extends DataModelTestBase {

  protected ListenerHasBeenCalled listenerCalled = null;

  @Before
  public void setup() throws Exception {
    super.setup();
    listenerCalled = new ListenerHasBeenCalled();
  }

  public class ListenerHasBeenCalled {
    protected boolean listenerHasBeenCalled = false;

    public boolean hasListenerBeenCalled() {
      return listenerHasBeenCalled;
    }

    public void setListenerHasBeenCalled(boolean listenerHasBeenCalled) {
      this.listenerHasBeenCalled = listenerHasBeenCalled;
    }

    @Override
    public String toString() {
      return "Listener has been called: " + listenerCalled;
    }
  }

  @Test
  public void addCategory_CategoryAddedListenerGetsCalled() throws Exception {
    final Category category = new Category("test");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {
        if(collection == deepThought.getCategories() && addedEntity == category)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {

      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    deepThought.addCategory(category);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void setCategoryName_CategoryUpdatedListenerGetsCalled() throws Exception {
    final Category category = new Category("test");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addCategory(category);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if(collection == deepThought.getCategories() && updatedEntity == category)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    category.setName("New name");

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void addSubCategoryToCategory_CategoryUpdatedListenerGetsCalled() throws Exception {
    final Category category = new Category("test");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addCategory(category);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getCategories() && updatedEntity == category)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    category.addSubCategory(new Category("sub"));

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void removeSubCategoryFromCategory_CategoryUpdatedListenerGetsCalled() throws Exception {
    final Category category = new Category("test");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addCategory(category);
    Category subCategory = new Category("sub");
    category.addSubCategory(subCategory);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if(collection == deepThought.getCategories() && updatedEntity == category)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    category.removeSubCategory(subCategory);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void addEntryToCategory_CategoryUpdatedListenerGetsCalled() throws Exception {
    final Category category = new Category("test");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addCategory(category);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getCategories() && updatedEntity == category)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    category.addEntry(new Entry("entry", "contentless"));

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void removeEntryFromCategory_CategoryUpdatedListenerGetsCalled() throws Exception {
    final Category category = new Category("test");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addCategory(category);
    Entry entry = new Entry("entry", "contentless");
    category.addEntry(entry);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getCategories() && updatedEntity == category)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    category.removeEntry(entry);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void removeCategory_CategoryRemovedListenerGetsCalled() throws Exception {
    final DeepThought deepThought = Application.getDeepThought();
    final Category category = new Category("test");
    deepThought.addCategory(category);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {

      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {
        if (collection == deepThought.getCategories() && removedEntity == category)
          listenerCalled.setListenerHasBeenCalled(true);
      }
    });

    deepThought.removeCategory(category);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }


  @Test
  public void addEntry_EntryAddedListenerGetsCalled() throws Exception {
    final Entry entry = new Entry("test", "no content");

    final DeepThought deepThought = Application.getDeepThought();

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {
        if (collection == deepThought.getEntries() && addedEntity == entry)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {

      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    deepThought.addEntry(entry);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void updateEntryTitle_EntryUpdatedListenerGetsCalled() throws Exception {
    final Entry entry = new Entry("test", "no content");

    final DeepThought deepThought = Application.getDeepThought();

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getEntries() && updatedEntity == entry)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    deepThought.addEntry(entry);
    entry.setTitle("New test");

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
    Assert.assertEquals("New test", entry.getTitle());
  }

  @Test
  public void updateEntryContent_EntryUpdatedListenerGetsCalled() throws Exception {
    final Entry entry = new Entry("test", "no content");

    final DeepThought deepThought = Application.getDeepThought();

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getEntries() && updatedEntity == entry)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    deepThought.addEntry(entry);
    entry.setContent("New content");

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
    Assert.assertEquals("New content", entry.getContent());
  }

  @Test
  public void addCategoryToEntry_EntryUpdatedListenerGetsCalled() throws Exception {
    Category category = new Category("test");
    final Entry entry = new Entry("test", "no content");

    final DeepThought deepThought = Application.getDeepThought();

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getEntries() && updatedEntity == entry)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    deepThought.addCategory(category);
    deepThought.addEntry(entry);

    category.addEntry(entry);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void removeCategoryFromEntry_EntryUpdatedListenerGetsCalled() throws Exception {
    Category category = new Category("test");
    final Entry entry = new Entry("test", "no content");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addCategory(category);
    deepThought.addEntry(entry);

    deepThought.addCategory(category);
    deepThought.addEntry(entry);

    category.addEntry(entry);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getEntries() && updatedEntity == entry)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    category.removeEntry(entry);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void addTagToEntry_EntryUpdatedListenerGetsCalled() throws Exception {
    final Entry entry = new Entry("test", "no content");

    final DeepThought deepThought = Application.getDeepThought();

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getEntries() && updatedEntity == entry)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    deepThought.addEntry(entry);

    Tag tag = new Tag("test");
    entry.addTag(tag);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void removeTagFromEntry_EntryUpdatedListenerGetsCalled() throws Exception {
    final Entry entry = new Entry("test", "no content");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addEntry(entry);

    Tag tag = new Tag("test");
    deepThought.addTag(tag);
    entry.addTag(tag);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getEntries() && updatedEntity == entry)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    entry.removeTag(tag);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void addIndexTermToEntry_EntryUpdatedListenerGetsCalled() throws Exception {
    final Entry entry = new Entry("test", "no content");

    final DeepThought deepThought = Application.getDeepThought();

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getEntries() && updatedEntity == entry)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    deepThought.addEntry(entry);

    IndexTerm indexTerm = new IndexTerm("test");
    entry.addIndexTerm(indexTerm);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void removeIndexTermFromEntry_EntryUpdatedListenerGetsCalled() throws Exception {
    final Entry entry = new Entry("test", "no content");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addEntry(entry);

    IndexTerm indexTerm = new IndexTerm("test");
    deepThought.addIndexTerm(indexTerm);
    entry.addIndexTerm(indexTerm);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getEntries() && updatedEntity == entry)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    entry.removeIndexTerm(indexTerm);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void removeEntry_EntryRemovedListenerGetsCalled() throws Exception {
    final Entry entry = new Entry("test", "no content");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addEntry(entry);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {

      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {
        if (collection == deepThought.getEntries() && removedEntity == entry)
          listenerCalled.setListenerHasBeenCalled(true);
      }
    });

    deepThought.removeEntry(entry);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }
  

  @Test
  public void addTag_TagAddedListenerGetsCalled() throws Exception {
    final Tag tag = new Tag("test");

    final DeepThought deepThought = Application.getDeepThought();

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {
        if (collection == deepThought.getTags() && addedEntity == tag)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {

      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    deepThought.addTag(tag);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void setTagName_TagUpdatedListenerGetsCalled() throws Exception {
    final Tag tag = new Tag("test");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addTag(tag);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getTags() && updatedEntity == tag)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    tag.setName("New name");

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void setTagDescription_TagUpdatedListenerGetsCalled() throws Exception {
    final Tag tag = new Tag("test");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addTag(tag);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getTags() && updatedEntity == tag)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    tag.setDescription("New description");

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void removeTag_TagRemovedListenerGetsCalled() throws Exception {
    final Tag tag = new Tag("test");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addTag(tag);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {

      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {
        if (collection == deepThought.getTags() && removedEntity == tag)
          listenerCalled.setListenerHasBeenCalled(true);
      }
    });

    deepThought.removeTag(tag);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }


  @Test
  public void addIndexTerm_IndexTermAddedListenerGetsCalled() throws Exception {
    final IndexTerm indexTerm = new IndexTerm("test");

    final DeepThought deepThought = Application.getDeepThought();

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {
        if (collection == deepThought.getIndexTerms() && addedEntity == indexTerm)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {

      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    deepThought.addIndexTerm(indexTerm);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void setIndexTermName_IndexTermUpdatedListenerGetsCalled() throws Exception {
    final IndexTerm indexTerm = new IndexTerm("test");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addIndexTerm(indexTerm);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getIndexTerms() && updatedEntity == indexTerm)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    indexTerm.setName("New name");

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void setIndexTermDescription_IndexTermUpdatedListenerGetsCalled() throws Exception {
    final IndexTerm indexTerm = new IndexTerm("test");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addIndexTerm(indexTerm);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {
        if (collection == deepThought.getIndexTerms() && updatedEntity == indexTerm)
          listenerCalled.setListenerHasBeenCalled(true);
      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {

      }
    });

    indexTerm.setDescription("New description");

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }

  @Test
  public void removeIndexTerm_IndexTermRemovedListenerGetsCalled() throws Exception {
    final IndexTerm indexTerm = new IndexTerm("test");

    final DeepThought deepThought = Application.getDeepThought();
    deepThought.addIndexTerm(indexTerm);

    deepThought.addEntityListener(new EntityListener() {
      @Override
      public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

      }

      @Override
      public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {

      }

      @Override
      public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {

      }

      @Override
      public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {
        if (collection == deepThought.getIndexTerms() && removedEntity == indexTerm)
          listenerCalled.setListenerHasBeenCalled(true);
      }
    });

    deepThought.removeIndexTerm(indexTerm);

    Assert.assertTrue(listenerCalled.hasListenerBeenCalled());
  }
  
}
