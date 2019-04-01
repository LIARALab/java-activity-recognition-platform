package org.liara.api.data.repository.database;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Node;
import org.liara.api.data.repository.NodeRepository;
import org.liara.api.data.tree.DatabaseNestedSetRepository;
import org.liara.api.data.tree.NestedSet;
import org.liara.api.io.WritingSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Primary
public class DatabaseNodeRepository
       extends DatabaseApplicationEntityRepository<Node>
       implements NodeRepository
{
  @NonNull
  private final DatabaseNestedSetRepository _tree;
  
  @Autowired
  public DatabaseNodeRepository (@NonNull final WritingSession writingSession) {
    super(writingSession, Node.class);
    _tree = new DatabaseNestedSetRepository(writingSession);
  }

  @Override
  public @NonNull <Node extends NestedSet> List<@NonNull Node> getChildrenOf (@NonNull final Node node) {
    return _tree.getChildrenOf(node);
  }

  @Override
  public @NonNull <Node extends NestedSet> List<@NonNull Node> getAllChildrenOf (@NonNull final Node node) {
    return _tree.getAllChildrenOf(node);
  }

  @Override
  public <Node extends NestedSet> @Nullable Node getParentOf (@NonNull final Node node) {
    return _tree.getParentOf(node);
  }

  @Override
  public @NonNull <Node extends NestedSet> List<@NonNull Node> getParentsOf (@NonNull final Node node) {
    return _tree.getParentsOf(node);
  }

  @Override
  public void attachChild (@NonNull NestedSet node) {
    _tree.attachChild(node);
  }

  @Override
  public void attachChild (
    @NonNull NestedSet node, @Nullable NestedSet parent
  )
  {
    _tree.attachChild(node, parent);
  }

  @Override
  public void removeChild (@NonNull NestedSet node) {
    _tree.removeChild(node);
  }

  @Override
  public <Node extends NestedSet> Node getRoot (@NonNull final Node node) {
    return _tree.getRoot(node);
  }
}
