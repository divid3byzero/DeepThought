package net.deepthought.controls.person;

import net.deepthought.data.model.Entry;
import net.deepthought.data.model.Person;
import net.deepthought.data.model.enums.PersonRole;
import net.deepthought.data.model.listener.EntityListener;
import net.deepthought.data.model.listener.EntryPersonListener;
import net.deepthought.data.persistence.db.BaseEntity;

import java.util.Collection;

/**
 * Created by ganymed on 27/12/14.
 */
public class EntryPersonListCell extends PersonListCell {

  protected Entry entry = null;


  public EntryPersonListCell(PersonsControl personsControl, Entry entry) {
    super(personsControl);

    setEntry(entry);
  }


  public void setEntry(Entry entry) {
    if(this.entry != null) {
      this.entry.removeEntityListener(entryListener);
      this.entry.removeEntryPersonListener(entryPersonListener);
    }

    this.entry = entry;

    if(entry != null) {
      entry.addEntityListener(entryListener);
      entry.addEntryPersonListener(entryPersonListener);
      setDefaultPersonRole(entry.getTemplate().getDefaultPersonRole());
    }
    else {
      setDefaultPersonRole(null);
    }
  }


  protected EntityListener entryListener = new EntityListener() {
    @Override
    public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {

    }

    @Override
    public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {
      if(addedEntity.equals(getItem()))
        itemChanged(getItem());
    }

    @Override
    public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {

    }

    @Override
    public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {
      if(removedEntity.equals(getItem()))
        itemChanged(getItem());
    }
  };

  protected EntryPersonListener entryPersonListener = new EntryPersonListener() {
    @Override
    public void personAdded(Entry entry, PersonRole role, Person addedPerson) {
      if(addedPerson.equals(getItem()))
        updateItem(addedPerson, addedPerson == null);
    }

    @Override
    public void personRemoved(Entry entry, PersonRole role, Person removedPerson) {
      if(removedPerson.equals(getItem()))
        updateItem(removedPerson, removedPerson == null);
    }
  };

}
