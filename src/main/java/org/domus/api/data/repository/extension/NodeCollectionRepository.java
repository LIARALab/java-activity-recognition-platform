package org.domus.api.data.repository.extension;

import java.util.List;
import org.springframework.lang.NonNull;
import org.domus.api.data.entity.Node;

public interface NodeCollectionRepository {
  public List<Node> findParentsOf (@NonNull final Node node);
  public List<Node> findParentsOf (final int id);
  public List<Node> findChildrenOf (@NonNull final Node node);
  public List<Node> findChildrenOf (final int id);
}
