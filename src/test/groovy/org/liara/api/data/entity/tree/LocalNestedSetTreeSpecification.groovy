package org.liara.api.data.entity.tree

import java.security.InvalidParameterException

import spock.lang.Specification
import org.mockito.Mockito

public class LocalNestedSetTreeSpecification extends Specification
{
  def "it allows you to instantiate an empty tree with the default constructor" () {
    expect: "to instantiate an empty tree when you use the default constructor" 
      final LocalNestedSetTree tree = new LocalNestedSetTree()
      
      tree.size == 0
      tree.getNodes().empty == true
  }
  
  def "it allow you to register new nodes into the tree" () {
    given: "an empty tree"
      final LocalNestedSetTree tree = new LocalNestedSetTree()
      
    and: "a node"
      final NestedSetTreeNode node = Mockito.mock(NestedSetTreeNode.class)
      Mockito.when(node.getParent()).thenReturn(null)
      Mockito.when(node.getIdentifier()).thenReturn(1L)
      
    when: "we add the node to the tree"
      tree.addNode(node)
      
    then: "we expect the node to have been added to the tree"
      tree.size == 1
      tree.getNodes().contains(node) == true
      tree.getCoordinatesOf(node).equals(
        new NestedSetCoordinates(1, 2, 1)
      ) == true
      tree.getNode(1L).equals(node) == true
      Mockito.verify(node, Mockito.times(1)).setTree(tree)
      
    when: "we add the node to the tree another time"
      tree.addNode(node)
      
    then: "we expect that the tree instance does nothing"
      tree.size == 1
      Mockito.verify(node, Mockito.times(1)).setTree(tree)
  }
  
  def "it allow you to register new nodes as children of a node of the tree" () {
    given: "a tree"
      final LocalNestedSetTree tree = new LocalNestedSetTree()
      
    and: "with some registered nodes"
      for (long index = 1; index <= 10; ++index) {
        final NestedSetTreeNode node = Mockito.mock(NestedSetTreeNode.class)
        Mockito.when(node.getParent()).thenReturn(null)
        Mockito.when(node.getIdentifier()).thenReturn(index)
        tree.addNode(node)
      }
      
    and: "a child node"
      final NestedSetTreeNode child = Mockito.mock(NestedSetTreeNode.class)
      Mockito.when(child.getParent()).thenReturn(null)
      Mockito.when(child.getIdentifier()).thenReturn(11L)
      
    when: "we add the node as a children of another node of the tree"
      tree.addNode(child, tree.getNode(6L))
      
    then: "we expect the node to have been added to the tree"
      tree.size == 11
      tree.getNodes().contains(child) == true
      tree.getNode(11L).equals(child) == true
      
      tree.getCoordinatesOf(child).equals(
        new NestedSetCoordinates(12, 13, 2)
      ) == true
      
      tree.getCoordinatesOf(tree.getNode(6L)).equals(
        new NestedSetCoordinates(11, 14, 1)
      ) == true
       
      for (int index = 0; index < 4; ++index) {
        tree.getCoordinatesOf(tree.getNode(index + 7L)).equals(
          new NestedSetCoordinates(15 + index * 2, 15 + index * 2 + 1, 1)
        ) == true
      }
      
      tree.getParentOf(child).equals(tree.getNode(6L)) == true
      tree.getChildrenOf(tree.getNode(6L)).size() == 1
      tree.getChildrenOf(tree.getNode(6L)).contains(child) == true
      
      Mockito.verify(child, Mockito.times(1)).setTree(tree)
      
    when: "we add the node to the tree another time"
      tree.addNode(child, tree.getNode(6L))
      
    then: "we expect that the tree instance does nothing"
      tree.size == 11
      Mockito.verify(child, Mockito.times(1)).setTree(tree)
  }
  
  def "it allow to move nodes into the tree" () {
    given: "a tree"
      final LocalNestedSetTree tree = new LocalNestedSetTree()
      
    and: "with some registered nodes"
      for (long index = 1; index <= 8; ++index) {
        final NestedSetTreeNode node = Mockito.mock(NestedSetTreeNode.class)
        Mockito.when(node.getParent()).thenReturn(null)
        Mockito.when(node.getIdentifier()).thenReturn(index)
        tree.addNode(node)
      }
      
    when: "we move nodes into the tree"
      tree.addNode(tree.getNode(2L), tree.getNode(1L))
      tree.addNode(tree.getNode(3L), tree.getNode(1L))
      tree.addNode(tree.getNode(4L), tree.getNode(2L))
      tree.addNode(tree.getNode(7L), tree.getNode(6L))
      tree.addNode(tree.getNode(8L), tree.getNode(6L))
      tree.addNode(tree.getNode(6L), tree.getNode(5L))
      
    then: "we expect the tree to mutate accordingly"
      final Map<Long, NestedSetCoordinates> coordinates = [
        1L: new NestedSetCoordinates(1, 8, 1),
        2L: new NestedSetCoordinates(2, 5, 2),
        3L: new NestedSetCoordinates(6, 7, 2),
        4L: new NestedSetCoordinates(3, 4, 3),
        5L: new NestedSetCoordinates(9, 16, 1),
        6L: new NestedSetCoordinates(10, 15, 2),
        7L: new NestedSetCoordinates(11, 12, 3),
        8L: new NestedSetCoordinates(13, 14, 3)
      ]
      
      for (final Map.Entry<Long, NestedSetCoordinates> entry : coordinates.entrySet()) {
        tree.getCoordinatesOf(tree.getNode(entry.key)).equals(entry.value) == true
      }
      
      final Map<Long, List<Long>> childrens = [
        1L: [2L, 3L],
        2L: [4L],
        3L: [],
        4L: [],
        5L: [6L],
        6L: [7L, 8L],
        7L: [],
        8L: [] 
      ]
      
      for (final Map.Entry<Long, List<Long>> entry : childrens.entrySet()) {
        tree.getChildrenOf(tree.getNode(entry.key)).size() == entry.value.size()
        for (final Long child : entry.value) {
          tree.getChildrenOf(tree.getNode(entry.key)).contains(tree.getNode(child)) == true
          tree.getParentOf(tree.getNode(child)).equals(tree.getNode(entry.key)) == true
        }
      }
  }
}
