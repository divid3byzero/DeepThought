package net.deepthought.data.model.enums;

import net.deepthought.data.persistence.EntityManagerConfiguration;
import net.deepthought.data.persistence.IEntityManager;
import net.deepthought.persistence.JpaEntityManager;

/**
 * Created by ganymed on 10/11/14.
 */
public class BackupFileServiceTypeTest extends BackupFileServiceTypeTestBase {

  @Override
  protected IEntityManager getEntityManager(EntityManagerConfiguration configuration) throws Exception {
    return new JpaEntityManager(configuration);
  }

}
