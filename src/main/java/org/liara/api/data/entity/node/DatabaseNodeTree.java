package org.liara.api.data.entity.node;

import javax.persistence.EntityManager;

import org.liara.api.data.entity.tree.DatabaseNestedSetTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DatabaseNodeTree extends DatabaseNestedSetTree<Node>
{
  @Nullable
  private static DatabaseNodeTree INSTANCE = null;
  
  public static DatabaseNodeTree getInstance () {
    return DatabaseNodeTree.INSTANCE;
  }
  
  @Autowired
  public DatabaseNodeTree(
    @NonNull final EntityManager entityManager
  ) { super(entityManager, Node.class); }
  
  @Autowired
  protected void registerInstance (@NonNull final DatabaseNodeTree tree) {
    DatabaseNodeTree.INSTANCE = tree;
  }
}
