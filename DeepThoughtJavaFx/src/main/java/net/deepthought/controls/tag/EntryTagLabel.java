package net.deepthought.controls.tag;

import net.deepthought.controls.CollectionItemLabel;
import net.deepthought.controls.event.CollectionItemLabelEvent;
import net.deepthought.data.model.Entry;
import net.deepthought.data.model.Tag;
import net.deepthought.data.model.listener.EntityListener;
import net.deepthought.data.persistence.db.BaseEntity;
import net.deepthought.util.JavaFxLocalization;

import java.util.Collection;

import javafx.event.EventHandler;

/**
 * Created by ganymed on 01/02/15.
 */
public class EntryTagLabel extends CollectionItemLabel {

  protected Entry entry;

  protected Tag tag;


  public EntryTagLabel(Entry entry, Tag tag, EventHandler<CollectionItemLabelEvent> onButtonRemoveItemFromCollectionEventHandler) {
    super(onButtonRemoveItemFromCollectionEventHandler);
    this.entry = entry;
    this.tag = tag;

    tag.addEntityListener(tagListener);

    setUserData(tag);
    JavaFxLocalization.bindControlToolTip(btnRemoveItemFromCollection, "tool.tip.click.to.remove.tag.from.entry", tag.getName(), entry.getPreview());
    itemDisplayNameUpdated();
  }

//  @Override
//  public void onButtonRemoveItemFromCollectionAction(ActionEvent event) {
//    entry.removeTag(tag);
//
//    super.onButtonRemoveItemFromCollectionAction(event);
//  }

  @Override
  protected String getItemDisplayName() {
    if(tag != null)
      return tag.getName();
    return "";
  }

  @Override
  protected String getToolTipText() {
    if(tag != null) {
      if(tag.getDescription() != null && tag.getDescription().isEmpty() == false)
        return tag.getName() + " (" + tag.getDescription() + ")";
      return tag.getName();
    }
    return "";
  }


  protected EntityListener tagListener = new EntityListener() {
    @Override
    public void propertyChanged(BaseEntity entity, String propertyName, Object previousValue, Object newValue) {
      itemDisplayNameUpdated();
    }

    @Override
    public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity) {
      if(collection == tag.getEntries())
        itemDisplayNameUpdated();
    }

    @Override
    public void entityOfCollectionUpdated(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity updatedEntity) {

    }

    @Override
    public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity) {
      if(collection == tag.getEntries())
        itemDisplayNameUpdated();
    }
  };

}
