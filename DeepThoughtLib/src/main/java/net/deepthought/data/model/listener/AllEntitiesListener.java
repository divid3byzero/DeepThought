package net.deepthought.data.model.listener;

import net.deepthought.data.persistence.db.BaseEntity;

import java.util.Collection;

/**
 * Created by ganymed on 31/01/15.
 */
public interface AllEntitiesListener {

  public void entityCreated(BaseEntity entity);

  public void entityUpdated(BaseEntity entity, String propertyName, Object previousValue, Object newValue);

  public void entityDeleted(BaseEntity entity);

  public void entityAddedToCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity addedEntity);

  public void entityRemovedFromCollection(BaseEntity collectionHolder, Collection<? extends BaseEntity> collection, BaseEntity removedEntity);

}
