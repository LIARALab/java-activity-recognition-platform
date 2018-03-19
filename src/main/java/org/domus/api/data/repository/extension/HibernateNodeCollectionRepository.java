package org.domus.api.data.repository.extension;

import java.util.List;

import org.springframework.lang.NonNull;

import org.springframework.beans.factory.annotation.Autowired;

import org.hibernate.SessionFactory;

import org.domus.api.data.entity.Node;

public class HibernateNodeCollectionRepository
       implements NodeCollectionRepository
{
  @Autowired
  private SessionFactory sessionFactory;

  public List<Node> findParentsOf (@NonNull final Node node) {
    return this.findParentsOf(node.getIdentifier());
  }

  public List<Node> findParentsOf (final int id) {
    return null;
  }

  public List<Node> findChildrenOf (@NonNull final Node node) {
    return this.findChildrenOf(node.getIdentifier());
  }

  public List<Node> findChildrenOf (final int id) {
    return null;
  }
}
