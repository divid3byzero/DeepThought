package net.deepthought.data.model;

import net.deepthought.Application;
import net.deepthought.data.helper.TestDependencyResolver;
import net.deepthought.data.model.enums.BackupFileServiceType;
import net.deepthought.data.model.enums.Language;
import net.deepthought.data.model.enums.NoteType;
import net.deepthought.data.model.enums.PersonRole;
import net.deepthought.data.model.enums.ReferenceCategory;
import net.deepthought.data.model.enums.ReferenceIndicationUnit;
import net.deepthought.data.model.enums.ReferenceSubDivisionCategory;
import net.deepthought.data.model.enums.SeriesTitleCategory;
import net.deepthought.data.model.settings.DeepThoughtSettings;
import net.deepthought.data.model.settings.SettingsBase;
import net.deepthought.data.model.settings.enums.SelectedAndroidTab;
import net.deepthought.data.model.settings.enums.SelectedTab;
import net.deepthought.data.persistence.IEntityManager;
import net.deepthought.data.persistence.db.TableConfig;

import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by ganymed on 09/11/14.
 */
public abstract class DeepThoughtTestBase extends DataModelTestBase {


  @Test
  public void addCategory_CategoryGetsPersisted() throws Exception {
    Category category = new Category("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addCategory(category);

    // assert categories really got written to database
    Assert.assertNotEquals(0, getRowFromTable(TableConfig.CategoryTableName, category.getId()));
  }

  @Test
  public void addCategory_RelationsGetSet() throws Exception {
    Category category = new Category("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addCategory(category);

    Assert.assertNotNull(category.getId());
    Assert.assertEquals(deepThought, category.getDeepThought());
    Assert.assertTrue(deepThought.getCategories().contains(category));
    Assert.assertEquals(deepThought.getTopLevelCategory(), category.getParentCategory());
    Assert.assertTrue(deepThought.getTopLevelCategory().getSubCategories().contains(category));
  }

  @Test
  public void addCategoryHierarchy_RelationsGetSet() throws Exception {
    Category topLevelCategory1 = new Category("Top Level 1");
    Category topLevelCategory2 = new Category("Top Level 2");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addCategory(topLevelCategory1);
    deepThought.addCategory(topLevelCategory2);

    for(int i = 1; i < 5; i++) {
      Category subLevel1Category = new Category("Sub Level 1-" + i);
      topLevelCategory1.addSubCategory(subLevel1Category);

      Category subLevel2Category = new Category("Sub Level 2-" + i);
      topLevelCategory2.addSubCategory(subLevel2Category);

      for(int j = 1; j < 4; j++) {
        subLevel1Category.addSubCategory(new Category("Sub sub 1-" + j));
        subLevel2Category.addSubCategory(new Category("Sub sub 2-" + j));
      }
    }


    Assert.assertNotNull(deepThought.getTopLevelCategory());
    Assert.assertNotNull(deepThought.getTopLevelCategory().getId());
    Assert.assertNull(deepThought.getTopLevelCategory().getParentCategory());

    Assert.assertEquals(deepThought.getTopLevelCategory(), topLevelCategory1.getParentCategory());
    Assert.assertEquals(deepThought.getTopLevelCategory(), topLevelCategory2.getParentCategory());

    for(Category subCategory : topLevelCategory1.getSubCategories()) {
      Assert.assertNotNull(subCategory.getId());
      Assert.assertEquals(topLevelCategory1, subCategory.getParentCategory());
      Assert.assertTrue(topLevelCategory1.containsSubCategory(subCategory));

      for(Category subSubCategory : subCategory.getSubCategories()) {
        Assert.assertNotNull(subSubCategory.getId());
        Assert.assertEquals(subCategory, subSubCategory.getParentCategory());
        Assert.assertTrue(subCategory.containsSubCategory(subSubCategory));
        Assert.assertEquals(0, subSubCategory.getSubCategories().size());
      }
    }

    for(Category subCategory : topLevelCategory2.getSubCategories()) {
      Assert.assertNotNull(subCategory.getId());
      Assert.assertEquals(topLevelCategory2, subCategory.getParentCategory());
      Assert.assertTrue(topLevelCategory2.containsSubCategory(subCategory));

      for(Category subSubCategory : subCategory.getSubCategories()) {
        Assert.assertNotNull(subSubCategory.getId());
        Assert.assertEquals(subCategory, subSubCategory.getParentCategory());
        Assert.assertTrue(subCategory.containsSubCategory(subSubCategory));
        Assert.assertEquals(0, subSubCategory.getSubCategories().size());
      }
    }
  }

  @Test
  public void removeCategory_CategoryGetsNotDeletedFromDB() throws Exception {
    Category category = new Category("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addCategory(category);

    Long categoryId = category.getId();
    deepThought.removeCategory(category);

    // assert category still exists in database (no data ever gets deleted from db, only its 'Deleted' flag gets set to true)
    Assert.assertTrue(category.isDeleted());
    Assert.assertNotEquals(0, getRowFromTable(TableConfig.CategoryTableName, categoryId));
  }

  @Test
  public void removeCategory_RelationsGetRemoved() throws Exception {
    Category category = new Category("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addCategory(category);

    deepThought.removeCategory(category);

    Assert.assertNull(category.getDeepThought());
    Assert.assertFalse(deepThought.getCategories().contains(category));

    Assert.assertTrue(category.isDeleted());
  }

  @Test
  public void addEntry_EntryGetsPersisted() throws Exception {
    Entry entry = new Entry("test", "no content");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addEntry(entry);

    // assert entry really got written to database
    Assert.assertNotEquals(0, getRowFromTable(TableConfig.EntryTableName, entry.getId()));
  }

  @Test
  public void addEntry_RelationsGetSet() throws Exception {
    Entry entry = new Entry("test", "no content");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addEntry(entry);

    Assert.assertNotNull(entry.getId());
    Assert.assertEquals(deepThought, entry.getDeepThought());
    Assert.assertTrue(deepThought.getEntries().contains(entry));
  }

  @Test
  public void removeEntry_EntryGetsNotDeletedFromDB() throws Exception {
    Entry entry = new Entry("test", "no content");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addEntry(entry);

    Long entryId = entry.getId();
    deepThought.removeEntry(entry);

    // assert entry still exists in database (no data ever gets deleted from db, only its 'Deleted' flag gets set to true)
    Assert.assertTrue(entry.isDeleted());
    Assert.assertNotEquals(0, getRowFromTable(TableConfig.EntryTableName, entryId));
  }

  @Test
  public void removeEntry_RelationsGetRemoved() throws Exception {
    Entry entry = new Entry("test", "no content");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addEntry(entry);

    deepThought.removeEntry(entry);

    Assert.assertNull(entry.getDeepThought());
    Assert.assertFalse(deepThought.getEntries().contains(entry));

    Assert.assertTrue(entry.isDeleted());
  }

  @Test
  public void addEntryToCategory_RemoveEntryFromDeepThought_RelationsGetRemoved() throws Exception {
    Entry entry = new Entry("test", "no content");
    Category category = new Category("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addCategory(category);
    deepThought.addEntry(entry);
    category.addEntry(entry);

    Assert.assertEquals(1, deepThought.getCategories().size());
    Assert.assertEquals(1, deepThought.getEntries().size());
    Assert.assertEquals(1, category.getEntries().size());

    deepThought.removeEntry(entry);

    Assert.assertEquals(1, deepThought.getCategories().size());
    Assert.assertEquals(0, deepThought.getEntries().size());
    Assert.assertEquals(0, category.getEntries().size());
  }

  @Test
  public void addEntryToTag_RemoveEntryFromDeepThought_RelationsGetRemoved() throws Exception {
    Entry entry = new Entry("test", "no content");
    Tag tag = new Tag("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addTag(tag);
    deepThought.addEntry(entry);
    entry.addTag(tag);

    Assert.assertEquals(1, deepThought.getTags().size());
    Assert.assertEquals(1, deepThought.getEntries().size());
    Assert.assertEquals(1, tag.getEntries().size());

    deepThought.removeEntry(entry);

    Assert.assertEquals(1, deepThought.getTags().size());
    Assert.assertEquals(0, deepThought.getEntries().size());
    Assert.assertEquals(0, tag.getEntries().size());
  }

  @Test
  // this cannot work with a in memory database
  public void addEntry_ClosePersistenceManager_NextEntryIndexHasBeenIncremented() throws Exception {
    Entry entry = new Entry("test", "no content");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addEntry(entry);

    Assert.assertEquals(2, deepThought.getNextEntryIndex());

    entityManager.close();

    IEntityManager entityManager2 = getEntityManager(configuration);
    Application.instantiate(new TestDependencyResolver(entityManager2));

    DeepThought deepThought2 = Application.getDeepThought();

    Assert.assertEquals(2, deepThought2.getNextEntryIndex());
  }
  

  @Test
  public void addTag_TagGetsPersisted() throws Exception {
    Tag tag = new Tag("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addTag(tag);

    // assert tag really got written to database
    Assert.assertNotEquals(0, getRowFromTable(TableConfig.TagTableName, tag.getId()));
  }

  @Test
  public void addTag_RelationsGetSet() throws Exception {
    Tag tag = new Tag("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addTag(tag);

    Assert.assertNotNull(tag.getId());
    Assert.assertEquals(deepThought, tag.getDeepThought());
    Assert.assertTrue(deepThought.getTags().contains(tag));
  }

  @Test
  public void removeTag_TagGetsNotDeletedFromDB() throws Exception {
    Tag tag = new Tag("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addTag(tag);

    Long tagId = tag.getId();
    deepThought.removeTag(tag);

    // assert tag still exists in database (no data ever gets deleted from db, only its 'Deleted' flag gets set to true)
    Assert.assertTrue(tag.isDeleted());
    Assert.assertNotEquals(0, getRowFromTable(TableConfig.TagTableName, tagId));

    Assert.assertTrue(tag.isDeleted());
  }

  @Test
  public void removeTag_RelationsGetRemoved() throws Exception {
    Tag tag = new Tag("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addTag(tag);

    deepThought.removeTag(tag);

    Assert.assertNull(tag.getDeepThought());
    Assert.assertFalse(deepThought.getTags().contains(tag));
  }


  @Test
  public void addIndexTerm_IndexTermGetsPersisted() throws Exception {
    IndexTerm indexTerm = new IndexTerm("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addIndexTerm(indexTerm);

    // assert IndexTerm really got written to database
    Assert.assertNotEquals(0, getRowFromTable(TableConfig.IndexTermTableName, indexTerm.getId()));
  }

  @Test
  public void addIndexTerm_RelationsGetSet() throws Exception {
    IndexTerm indexTerm = new IndexTerm("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addIndexTerm(indexTerm);

    Assert.assertNotNull(indexTerm.getId());
    Assert.assertEquals(deepThought, indexTerm.getDeepThought());
    Assert.assertTrue(deepThought.getIndexTerms().contains(indexTerm));
  }

  @Test
  public void removeIndexTerm_IndexTermGetsNotDeletedFromDB() throws Exception {
    IndexTerm indexTerm = new IndexTerm("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addIndexTerm(indexTerm);

    Long indexTermId = indexTerm.getId();
    deepThought.removeIndexTerm(indexTerm);

    // assert IndexTerm still exists in database (no data ever gets deleted from db, only its 'Deleted' flag gets set to true)
    Assert.assertTrue(indexTerm.isDeleted());
    Assert.assertNotEquals(0, getRowFromTable(TableConfig.IndexTermTableName, indexTermId));
  }

  @Test
  public void removeIndexTerm_RelationsGetRemoved() throws Exception {
    IndexTerm indexTerm = new IndexTerm("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addIndexTerm(indexTerm);

    deepThought.removeIndexTerm(indexTerm);

    Assert.assertNull(indexTerm.getDeepThought());
    Assert.assertFalse(deepThought.getIndexTerms().contains(indexTerm));

    Assert.assertTrue(indexTerm.isDeleted());
  }
  

  @Test
  public void updateLastViewedCategory_UpdatedLastViewedCategoryGetsPersistedInDb() throws Exception {
    DeepThought deepThought = Application.getDeepThought();

    Category newLastViewedCategory = new Category("New last viewed Category");
    deepThought.addCategory(newLastViewedCategory);
    deepThought.getSettings().setLastViewedCategory(newLastViewedCategory);

    DeepThoughtSettings settings = SettingsBase.createSettingsFromString(deepThought.settingsString, DeepThoughtSettings.class);

    Assert.assertNotNull(settings);
    Assert.assertTrue(doIdsEqual(newLastViewedCategory.getId(), settings.getLastViewedCategory().getId()));
  }

  @Test
  public void updateLastViewedTag_UpdatedLastViewedTagGetsPersistedInDb() throws Exception {
    DeepThought deepThought = Application.getDeepThought();

    Tag newLastViewedTag = new Tag("New last viewed Category");
    deepThought.addTag(newLastViewedTag);
    deepThought.getSettings().setLastViewedTag(newLastViewedTag);

    DeepThoughtSettings settings = SettingsBase.createSettingsFromString(deepThought.settingsString, DeepThoughtSettings.class);

    Assert.assertNotNull(settings);
    Assert.assertTrue(doIdsEqual(newLastViewedTag.getId(), settings.getLastViewedTag().getId()));
  }

  @Test
  public void updateLastViewedEntry_UpdatedLastViewedEntryGetsPersistedInDb() throws Exception {
    DeepThought deepThought = Application.getDeepThought();

    Entry newLastViewedEntry = new Entry("New last viewed Entry", "");
    deepThought.addEntry(newLastViewedEntry);
    deepThought.getSettings().setLastViewedEntry(newLastViewedEntry);

    DeepThoughtSettings settings = SettingsBase.createSettingsFromString(deepThought.settingsString, DeepThoughtSettings.class);

    Assert.assertNotNull(settings);
    Assert.assertTrue(doIdsEqual(newLastViewedEntry.getId(), settings.getLastViewedEntry().getId()));
  }

  @Test
  public void updateLastSelectedTab_UpdatedLastSelectedTabGetsPersistedInDb() throws Exception {
    DeepThought deepThought = Application.getDeepThought();

    SelectedTab newLastSelectedTab = SelectedTab.Search;
    deepThought.getSettings().setLastSelectedTab(newLastSelectedTab);

    DeepThoughtSettings settings = SettingsBase.createSettingsFromString(deepThought.settingsString, DeepThoughtSettings.class);

    Assert.assertNotNull(settings);
    Assert.assertEquals(newLastSelectedTab, settings.getLastSelectedTab());
  }

  @Test
  public void updateLastSelectedAndroidTab_UpdatedLastSelectedAndroidTabGetsPersistedInDb() throws Exception {
    DeepThought deepThought = Application.getDeepThought();

    SelectedAndroidTab newLastSelectedAndroidTab = SelectedAndroidTab.EntriesOverview;
    deepThought.getSettings().setLastSelectedAndroidTab(newLastSelectedAndroidTab);

    DeepThoughtSettings settings = SettingsBase.createSettingsFromString(deepThought.settingsString, DeepThoughtSettings.class);

    Assert.assertNotNull(settings);
    Assert.assertEquals(newLastSelectedAndroidTab, settings.getLastSelectedAndroidTab());
  }


  @Test
  public void addSeriesTitle_EntityGetsPersisted() throws Exception {
    SeriesTitle seriesTitle = new SeriesTitle("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addSeriesTitle(seriesTitle);

    Assert.assertNotEquals(0, getRowFromTable(TableConfig.SeriesTitleTableName, seriesTitle.getId()));
  }

  @Test
  public void addSeriesTitle_RelationsGetSet() throws Exception {
    SeriesTitle seriesTitle = new SeriesTitle("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addSeriesTitle(seriesTitle);

    Assert.assertNotNull(seriesTitle.getId());
    Assert.assertEquals(deepThought, seriesTitle.getDeepThought());
    Assert.assertTrue(deepThought.getSeriesTitles().contains(seriesTitle));
  }

  @Test
  public void removeSeriesTitle_EntityGetsNotDeletedFromDB() throws Exception {
    SeriesTitle seriesTitle = new SeriesTitle("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addSeriesTitle(seriesTitle);

    Long seriesTitleId = seriesTitle.getId();
    deepThought.removeSeriesTitle(seriesTitle);

    Assert.assertTrue(seriesTitle.isDeleted());
    Assert.assertNotEquals(0, getRowFromTable(TableConfig.SeriesTitleTableName, seriesTitleId));
  }

  @Test
  public void removeSeriesTitle_RelationsGetRemoved() throws Exception {
    SeriesTitle seriesTitle = new SeriesTitle("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addSeriesTitle(seriesTitle);

    deepThought.removeSeriesTitle(seriesTitle);

    Assert.assertNull(seriesTitle.getDeepThought());
    Assert.assertFalse(deepThought.getSeriesTitles().contains(seriesTitle));

    Assert.assertTrue(seriesTitle.isDeleted());
  }


  @Test
  public void addReference_EntityGetsPersisted() throws Exception {
    Reference reference = new Reference("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReference(reference);

    Assert.assertNotEquals(0, getRowFromTable(TableConfig.ReferenceTableName, reference.getId()));
  }

  @Test
  public void addReference_RelationsGetSet() throws Exception {
    Reference reference = new Reference("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReference(reference);

    Assert.assertNotNull(reference.getId());
    Assert.assertEquals(deepThought, reference.getDeepThought());
    Assert.assertTrue(deepThought.getReferences().contains(reference));
  }

  @Test
  public void removeReference_EntityGetsNotDeletedFromDB() throws Exception {
    Reference reference = new Reference("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReference(reference);

    Long referenceId = reference.getId();
    deepThought.removeReference(reference);

    Assert.assertTrue(reference.isDeleted());
    Assert.assertNotEquals(0, getRowFromTable(TableConfig.ReferenceTableName, referenceId));
  }

  @Test
  public void removeReference_RelationsGetRemoved() throws Exception {
    Reference reference = new Reference("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReference(reference);

    deepThought.removeReference(reference);

    Assert.assertNull(reference.getDeepThought());
    Assert.assertFalse(deepThought.getReferences().contains(reference));

    Assert.assertTrue(reference.isDeleted());
  }


  @Test
  public void addSeriesTitleCategory_RelationGetsPersisted() throws Exception {
    SeriesTitleCategory seriesTitleCategory = new SeriesTitleCategory("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addSeriesTitleCategory(seriesTitleCategory);

    // assert SeriesTitleCategory really got written to database
    Object persistedDeepThoughtId = getValueFromTable(TableConfig.SeriesTitleCategoryTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, seriesTitleCategory.getId());
    Assert.assertTrue(doIdsEqual(seriesTitleCategory.getDeepThought().getId(), persistedDeepThoughtId));
  }

  @Test
  public void addSeriesTitleCategory_EntitiesGetAddedToRelatedCollections() throws Exception {
    SeriesTitleCategory seriesTitleCategory = new SeriesTitleCategory("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addSeriesTitleCategory(seriesTitleCategory);

    Assert.assertNotNull(seriesTitleCategory.getDeepThought());
    Assert.assertTrue(deepThought.getSeriesTitleCategories().contains(seriesTitleCategory));
  }

  @Test
  public void removeSeriesTitleCategory_RelationGetsDeleted() throws Exception {
    SeriesTitleCategory seriesTitleCategory = new SeriesTitleCategory("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addSeriesTitleCategory(seriesTitleCategory);

    deepThought.removeSeriesTitleCategory(seriesTitleCategory);

    // assert SeriesTitleCategory really got deleted from database
    Object persistedDeepThoughtId = getValueFromTable(TableConfig.SeriesTitleCategoryTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, seriesTitleCategory.getId());
    Assert.assertNull(persistedDeepThoughtId);

    Assert.assertTrue(seriesTitleCategory.isDeleted());
  }

  @Test
  public void removeSeriesTitleCategory_EntitiesGetRemovedFromRelatedCollections() throws Exception {
    SeriesTitleCategory seriesTitleCategory = new SeriesTitleCategory("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addSeriesTitleCategory(seriesTitleCategory);

    deepThought.removeSeriesTitleCategory(seriesTitleCategory);

    Assert.assertNull(seriesTitleCategory.getDeepThought());
    Assert.assertFalse(deepThought.getSeriesTitleCategories().contains(seriesTitleCategory));
  }


  @Test
  public void addReferenceCategory_RelationGetsPersisted() throws Exception {
    ReferenceCategory referenceCategory = new ReferenceCategory("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReferenceCategory(referenceCategory);

    Object persistedDeepThoughtId = getValueFromTable(TableConfig.ReferenceCategoryTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, referenceCategory.getId());
    Assert.assertTrue(doIdsEqual(referenceCategory.getDeepThought().getId(), persistedDeepThoughtId));
  }

  @Test
  public void addReferenceCategory_EntitiesGetAddedToRelatedCollections() throws Exception {
    ReferenceCategory referenceCategory = new ReferenceCategory("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReferenceCategory(referenceCategory);

    Assert.assertNotNull(referenceCategory.getDeepThought());
    Assert.assertTrue(deepThought.getReferenceCategories().contains(referenceCategory));
  }

  @Test
  public void removeReferenceCategory_RelationGetsDeleted() throws Exception {
    ReferenceCategory referenceCategory = new ReferenceCategory("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReferenceCategory(referenceCategory);

    deepThought.removeReferenceCategory(referenceCategory);

    Object persistedDeepThoughtId = getValueFromTable(TableConfig.ReferenceCategoryTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, referenceCategory.getId());
    Assert.assertNull(persistedDeepThoughtId);

    Assert.assertTrue(referenceCategory.isDeleted());
  }

  @Test
  public void removeReferenceCategory_EntitiesGetRemovedFromRelatedCollections() throws Exception {
    ReferenceCategory referenceCategory = new ReferenceCategory("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReferenceCategory(referenceCategory);

    deepThought.removeReferenceCategory(referenceCategory);

    Assert.assertNull(referenceCategory.getDeepThought());
    Assert.assertFalse(deepThought.getReferenceCategories().contains(referenceCategory));
  }


  @Test
  public void addReferenceSubDivisionCategory_RelationGetsPersisted() throws Exception {
    ReferenceSubDivisionCategory referenceSubDivisionCategory = new ReferenceSubDivisionCategory("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReferenceSubDivisionCategory(referenceSubDivisionCategory);

    Object persistedDeepThoughtId = getValueFromTable(TableConfig.ReferenceSubDivisionCategoryTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, referenceSubDivisionCategory.getId());
    Assert.assertTrue(doIdsEqual(referenceSubDivisionCategory.getDeepThought().getId(), persistedDeepThoughtId));
  }

  @Test
  public void addReferenceSubDivisionCategory_EntitiesGetAddedToRelatedCollections() throws Exception {
    ReferenceSubDivisionCategory referenceSubDivisionCategory = new ReferenceSubDivisionCategory("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReferenceSubDivisionCategory(referenceSubDivisionCategory);

    Assert.assertNotNull(referenceSubDivisionCategory.getDeepThought());
    Assert.assertTrue(deepThought.getReferenceSubDivisionCategories().contains(referenceSubDivisionCategory));
  }

  @Test
  public void removeReferenceSubDivisionCategory_RelationGetsDeleted() throws Exception {
    ReferenceSubDivisionCategory referenceSubDivisionCategory = new ReferenceSubDivisionCategory("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReferenceSubDivisionCategory(referenceSubDivisionCategory);

    deepThought.removeReferenceSubDivisionCategory(referenceSubDivisionCategory);

    Object persistedDeepThoughtId = getValueFromTable(TableConfig.ReferenceSubDivisionCategoryTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, referenceSubDivisionCategory.getId());
    Assert.assertNull(persistedDeepThoughtId);

    Assert.assertTrue(referenceSubDivisionCategory.isDeleted());
  }

  @Test
  public void removeReferenceSubDivisionCategory_EntitiesGetRemovedFromRelatedCollections() throws Exception {
    ReferenceSubDivisionCategory referenceSubDivisionCategory = new ReferenceSubDivisionCategory("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReferenceSubDivisionCategory(referenceSubDivisionCategory);

    deepThought.removeReferenceSubDivisionCategory(referenceSubDivisionCategory);

    Assert.assertNull(referenceSubDivisionCategory.getDeepThought());
    Assert.assertFalse(deepThought.getReferenceSubDivisionCategories().contains(referenceSubDivisionCategory));
  }


  @Test
  public void addReferenceIndicationUnit_RelationGetsPersisted() throws Exception {
    ReferenceIndicationUnit referenceIndicationUnit = new ReferenceIndicationUnit("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReferenceIndicationUnit(referenceIndicationUnit);

    Object persistedDeepThoughtId = getValueFromTable(TableConfig.ReferenceIndicationUnitTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, referenceIndicationUnit.getId());
    Assert.assertTrue(doIdsEqual(referenceIndicationUnit.getDeepThought().getId(), persistedDeepThoughtId));
  }

  @Test
  public void addReferenceIndicationUnit_EntitiesGetAddedToRelatedCollections() throws Exception {
    ReferenceIndicationUnit referenceIndicationUnit = new ReferenceIndicationUnit("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReferenceIndicationUnit(referenceIndicationUnit);

    Assert.assertNotNull(referenceIndicationUnit.getDeepThought());
    Assert.assertTrue(deepThought.getReferenceIndicationUnits().contains(referenceIndicationUnit));
  }

  @Test
  public void removeReferenceIndicationUnit_RelationGetsDeleted() throws Exception {
    ReferenceIndicationUnit referenceIndicationUnit = new ReferenceIndicationUnit("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReferenceIndicationUnit(referenceIndicationUnit);

    deepThought.removeReferenceIndicationUnit(referenceIndicationUnit);

    Object persistedDeepThoughtId = getValueFromTable(TableConfig.ReferenceIndicationUnitTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, referenceIndicationUnit.getId());
    Assert.assertNull(persistedDeepThoughtId);

    Assert.assertTrue(referenceIndicationUnit.isDeleted());
  }

  @Test
  public void removeReferenceIndicationUnit_EntitiesGetRemovedFromRelatedCollections() throws Exception {
    ReferenceIndicationUnit referenceIndicationUnit = new ReferenceIndicationUnit("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addReferenceIndicationUnit(referenceIndicationUnit);

    deepThought.removeReferenceIndicationUnit(referenceIndicationUnit);

    Assert.assertNull(referenceIndicationUnit.getDeepThought());
    Assert.assertFalse(deepThought.getReferenceIndicationUnits().contains(referenceIndicationUnit));
  }

  @Test
  public void removeSystemReferenceIndicationUnit_EntityGetsNotDeleted() throws Exception {
    DeepThought deepThought = Application.getDeepThought();
    ReferenceIndicationUnit systemReferenceIndicationUnit = new ArrayList<ReferenceIndicationUnit>(deepThought.getReferenceIndicationUnits()).get(0);

    boolean removeResult = deepThought.removeReferenceIndicationUnit(systemReferenceIndicationUnit);

    Assert.assertFalse(removeResult);
    Assert.assertNotNull(systemReferenceIndicationUnit.getDeepThought());
    Assert.assertTrue(deepThought.getReferenceIndicationUnits().contains(systemReferenceIndicationUnit));

    Assert.assertFalse(systemReferenceIndicationUnit.isDeleted());
  }


  @Test
  public void addLanguage_RelationGetsPersisted() throws Exception {
    Language language = new Language("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addLanguage(language);

    // assert Language really got written to database
    Object persistedDeepThoughtId = getValueFromTable(TableConfig.LanguageTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, language.getId());
    Assert.assertTrue(doIdsEqual(language.getDeepThought().getId(), persistedDeepThoughtId));
  }

  @Test
  public void addLanguage_EntitiesGetAddedToRelatedCollections() throws Exception {
    Language language = new Language("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addLanguage(language);

    Assert.assertNotNull(language.getDeepThought());
    Assert.assertTrue(deepThought.getLanguages().contains(language));
  }

  @Test
  public void removeLanguage_RelationGetsDeleted() throws Exception {
    Language language = new Language("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addLanguage(language);

    deepThought.removeLanguage(language);

    // assert Language really got deleted from database
    Object persistedDeepThoughtId = getValueFromTable(TableConfig.LanguageTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, language.getId());
    Assert.assertNull(persistedDeepThoughtId);

    Assert.assertTrue(language.isDeleted());
  }

  @Test
  public void removeLanguage_EntitiesGetRemovedFromRelatedCollections() throws Exception {
    Language language = new Language("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addLanguage(language);

    deepThought.removeLanguage(language);

    Assert.assertNull(language.getDeepThought());
    Assert.assertFalse(deepThought.getLanguages().contains(language));
  }


  @Test
  public void addPersonRole_RelationGetsPersisted() throws Exception {
    PersonRole personRole = new PersonRole("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addPersonRole(personRole);

    Object persistedDeepThoughtId = getValueFromTable(TableConfig.PersonRoleTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, personRole.getId());
    Assert.assertTrue(doIdsEqual(personRole.getDeepThought().getId(), persistedDeepThoughtId));
  }

  @Test
  public void addPersonRole_EntitiesGetAddedToRelatedCollections() throws Exception {
    PersonRole personRole = new PersonRole("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addPersonRole(personRole);

    Assert.assertNotNull(personRole.getDeepThought());
    Assert.assertTrue(deepThought.getPersonRoles().contains(personRole));
  }

  @Test
  public void removePersonRole_RelationGetsDeleted() throws Exception {
    PersonRole personRole = new PersonRole("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addPersonRole(personRole);

    deepThought.removePersonRole(personRole);

    Object persistedDeepThoughtId = getValueFromTable(TableConfig.PersonRoleTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, personRole.getId());
    Assert.assertNull(persistedDeepThoughtId);

    Assert.assertTrue(personRole.isDeleted());
  }

  @Test
  public void removePersonRole_EntitiesGetRemovedFromRelatedCollections() throws Exception {
    PersonRole personRole = new PersonRole("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addPersonRole(personRole);

    deepThought.removePersonRole(personRole);

    Assert.assertNull(personRole.getDeepThought());
    Assert.assertFalse(deepThought.getPersonRoles().contains(personRole));
  }


  @Test
  public void addNoteType_RelationGetsPersisted() throws Exception {
    NoteType noteType = new NoteType("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addNoteType(noteType);

    Object persistedDeepThoughtId = getValueFromTable(TableConfig.NoteTypeTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, noteType.getId());
    Assert.assertTrue(doIdsEqual(noteType.getDeepThought().getId(), persistedDeepThoughtId));
  }

  @Test
  public void addNoteType_EntitiesGetAddedToRelatedCollections() throws Exception {
    NoteType noteType = new NoteType("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addNoteType(noteType);

    Assert.assertNotNull(noteType.getDeepThought());
    Assert.assertTrue(deepThought.getNoteTypes().contains(noteType));
  }

  @Test
  public void removeNoteType_RelationGetsDeleted() throws Exception {
    NoteType noteType = new NoteType("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addNoteType(noteType);

    deepThought.removeNoteType(noteType);

    Object persistedDeepThoughtId = getValueFromTable(TableConfig.NoteTypeTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, noteType.getId());
    Assert.assertNull(persistedDeepThoughtId);

    Assert.assertTrue(noteType.isDeleted());
  }

  @Test
  public void removeNoteType_EntitiesGetRemovedFromRelatedCollections() throws Exception {
    NoteType noteType = new NoteType("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addNoteType(noteType);

    deepThought.removeNoteType(noteType);

    Assert.assertNull(noteType.getDeepThought());
    Assert.assertFalse(deepThought.getNoteTypes().contains(noteType));
  }

  @Test
  public void removeSystemNoteType_EntityGetsNotDeleted() throws Exception {
    DeepThought deepThought = Application.getDeepThought();
    NoteType systemNoteType = new ArrayList<NoteType>(deepThought.getNoteTypes()).get(0);

    boolean removeResult = deepThought.removeNoteType(systemNoteType);

    Assert.assertFalse(removeResult);
    Assert.assertNotNull(systemNoteType.getDeepThought());
    Assert.assertTrue(deepThought.getNoteTypes().contains(systemNoteType));

    Assert.assertFalse(systemNoteType.isDeleted());
  }


  @Test
  public void addBackupFileServiceType_RelationGetsPersisted() throws Exception {
    BackupFileServiceType fileServiceType = new BackupFileServiceType("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addBackupFileServiceType(fileServiceType);

    Object persistedDeepThoughtId = getValueFromTable(TableConfig.BackupFileServiceTypeTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, fileServiceType.getId());
    Assert.assertTrue(doIdsEqual(fileServiceType.getDeepThought().getId(), persistedDeepThoughtId));
  }

  @Test
  public void addBackupFileServiceType_EntitiesGetAddedToRelatedCollections() throws Exception {
    BackupFileServiceType fileServiceType = new BackupFileServiceType("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addBackupFileServiceType(fileServiceType);

    Assert.assertEquals(deepThought, fileServiceType.getDeepThought());
    Assert.assertTrue(deepThought.getBackupFileServiceTypes().contains(fileServiceType));
  }

  @Test
  public void removeBackupFileServiceType_RelationGetsDeleted() throws Exception {
    BackupFileServiceType fileServiceType = new BackupFileServiceType("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addBackupFileServiceType(fileServiceType);

    deepThought.removeBackupFileServiceType(fileServiceType);

    Object persistedDeepThoughtId = getValueFromTable(TableConfig.BackupFileServiceTypeTableName, TableConfig.ExtensibleEnumerationDeepThoughtJoinColumnName, fileServiceType.getId());
    Assert.assertNull(persistedDeepThoughtId);

    Assert.assertTrue(fileServiceType.isDeleted());
  }

  @Test
  public void removeBackupFileServiceType_EntitiesGetRemovedFromRelatedCollections() throws Exception {
    BackupFileServiceType fileServiceType = new BackupFileServiceType("test");

    DeepThought deepThought = Application.getDeepThought();
    deepThought.addBackupFileServiceType(fileServiceType);

    deepThought.removeBackupFileServiceType(fileServiceType);

    Assert.assertNull(fileServiceType.getDeepThought());
    Assert.assertFalse(deepThought.getBackupFileServiceTypes().contains(fileServiceType));
  }

  @Test
  public void removeSystemBackupFileServiceType_EntityGetsNotDeleted() throws Exception {
    DeepThought deepThought = Application.getDeepThought();
    BackupFileServiceType systemBackupFileServiceType = new ArrayList<BackupFileServiceType>(deepThought.getBackupFileServiceTypes()).get(0);

    boolean removeResult = deepThought.removeBackupFileServiceType(systemBackupFileServiceType);

    Assert.assertFalse(removeResult);
    Assert.assertNotNull(systemBackupFileServiceType.getDeepThought());
    Assert.assertTrue(deepThought.getBackupFileServiceTypes().contains(systemBackupFileServiceType));

    Assert.assertFalse(systemBackupFileServiceType.isDeleted());
  }


  protected boolean doesDeepThoughtFavoriteEntryTemplateJoinTableEntryExist(Long deepThoughtId, Long entryTemplateId) throws SQLException {
    return doesJoinTableEntryExist(TableConfig.DeepThoughtFavoriteEntryTemplateJoinTableName, TableConfig.DeepThoughtFavoriteEntryTemplateJoinTableDeepThoughtIdColumnName,
        deepThoughtId, TableConfig.DeepThoughtFavoriteEntryTemplateJoinTableEntryTemplateIdColumnName, entryTemplateId);
  }
  
}
