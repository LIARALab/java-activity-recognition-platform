package org.liara.api.data.entity.tree

import java.security.InvalidParameterException

import spock.lang.Specification

public class NestedSetCoordinatesSpecification extends Specification
{
  def "it instantiate the coordinates of a leaf element of a root node when you use its default constructor" () {
    given: "a NestedSetCoordinates object instantiated from its default constructor"
      final NestedSetCoordinates instance = new NestedSetCoordinates()
      
    expect: "the instance to have valid default coordinates"
      instance.start == 1
      instance.end == 2
      instance.depth == 1
  }
  
  def "it can be instanciated with valid coordinates values" () {
    given: "a NestedSetCoordinates object instantiated with valid values"
      NestedSetCoordinates instance = new NestedSetCoordinates(5, 8, 1)
      
    expect: "the instance to have the given coordinates"
      instance.start == 5
      instance.end == 8
      instance.depth == 1
      
    /*
    when: "an invalid NestedSetCoordinates object instantiation with a starting coordinate greater or equal to the ending coordinate of the set"
      instance = new NestedSetCoordinates(5, 5, 1)
      
    then: "the constructor to throw an error"
      thrown(InvalidParameterException.class)
       
    when: "an invalid NestedSetCoordinates object instantiation with an invalid ending coordinate"
      instance = new NestedSetCoordinates(1, 3, 1)
      
    then: "the constructor to throw an error"
      thrown(InvalidParameterException.class)
    */
  }
  
  def "it allows you to intantiate a copy of another instance of NestedSetCoordinates" () {
    given: "a NestedSetCoordinates object instantiated with valid values"
      final NestedSetCoordinates instance = new NestedSetCoordinates(5, 8, 1)
      
    when: "we use the copy constructor of NestedSetCoordinates"
      final NestedSetCoordinates copy = new NestedSetCoordinates(instance)
      
    then: "we expect to get a clone of the copied instance"
      instance.start == copy.start
      instance.end == copy.end
      instance.depth == copy.depth
      instance.is(copy) == false
  }
  
  def "it allows you to get the size of the described set" () {
    expect: "to get a valid size for each created valid set coordinates"
      for (int size = 0; size < 10; ++size) {
        new NestedSetCoordinates(5, 5 + size * 2 + 1, 1).size == size
      }
  }
  
  def "it allows you to change the coordinates of the set" () {
    given: "a NestedSetCoordinates object"
      final NestedSetCoordinates instance = new NestedSetCoordinates(); 
    
    expect: "to change the coordinates of the set instance when setters are called"
      instance.start == 1
      instance.end == 2
      instance.depth == 1
      
      instance.setEnd(instance.start + 8 + 1).end == 10
      instance.setStart(instance.start + 4).start == 5
      instance.setDepth(5).depth == 5
      
      instance.set(3, 4, 3)
      instance.start == 3
      instance.end == 4
      instance.depth == 3
      
      instance.set(new NestedSetCoordinates())
      instance.start == 1
      instance.end == 2
      instance.depth == 1
  }
  
  def "it allows you to know if the described set is a leaf" () {
    expect: "to return true if the described set is a leaf, false otherwise"
      new NestedSetCoordinates(1, 2, 3).isLeaf() == true
      new NestedSetCoordinates(1, 4, 3).isLeaf() == false
  }
  
  def "it allows you to know if the described set has some children" () {
    expect: "to return true if the described set has some children, false otherwise"
      new NestedSetCoordinates(1, 2, 3).hasChildren() == false
      new NestedSetCoordinates(1, 4, 3).hasChildren() == true
  }
  
  def "it allows you to know if another set is potentially a child set of the current set" () {
    expect: "to return true if the described set is a child set of the current set, false otherwise"
      new NestedSetCoordinates(1, 6, 3).isChildSet(new NestedSetCoordinates(2, 3, 5)) == true
      new NestedSetCoordinates(1, 6, 3).isChildSet(new NestedSetCoordinates(2, 5, 4)) == true
      new NestedSetCoordinates(1, 6, 3).isChildSet(new NestedSetCoordinates(2, 5, 2)) == false
      new NestedSetCoordinates(1, 6, 3).isChildSet(new NestedSetCoordinates(2, 7, 5)) == false
      new NestedSetCoordinates(1, 6, 3).isChildSet(new NestedSetCoordinates(1, 6, 5)) == false
      new NestedSetCoordinates(3, 4, 3).isChildSet(new NestedSetCoordinates(2, 5, 2)) == false
  }
  
  def "it allows you to know if another set is potentially a direct child set of the current set" () {
    expect: "to return true if the described set is a direct child set of the current set, false otherwise"
      new NestedSetCoordinates(1, 6, 3).isDirectChildSet(new NestedSetCoordinates(2, 3, 5)) == false
      new NestedSetCoordinates(1, 6, 3).isDirectChildSet(new NestedSetCoordinates(2, 5, 4)) == true
      new NestedSetCoordinates(1, 6, 3).isDirectChildSet(new NestedSetCoordinates(2, 5, 2)) == false
      new NestedSetCoordinates(1, 6, 3).isDirectChildSet(new NestedSetCoordinates(2, 7, 5)) == false
      new NestedSetCoordinates(1, 6, 3).isDirectChildSet(new NestedSetCoordinates(1, 6, 5)) == false
      new NestedSetCoordinates(3, 4, 3).isDirectChildSet(new NestedSetCoordinates(2, 5, 2)) == false
  }
  
  def "it allows you to know if another set is potentially a parent set of the current set" () {
    expect: "to return true if the described set is a parent set of the current set, false otherwise"
      new NestedSetCoordinates(5, 8, 3).isParentSet(new NestedSetCoordinates(4, 9, 2)) == true
      new NestedSetCoordinates(5, 8, 3).isParentSet(new NestedSetCoordinates(3, 10, 1)) == true
      new NestedSetCoordinates(5, 8, 3).isParentSet(new NestedSetCoordinates(4, 9, 3)) == false
      new NestedSetCoordinates(5, 8, 3).isParentSet(new NestedSetCoordinates(5, 8, 3)) == false
      new NestedSetCoordinates(5, 8, 3).isParentSet(new NestedSetCoordinates(6, 11, 2)) == false
      new NestedSetCoordinates(5, 8, 3).isParentSet(new NestedSetCoordinates(4, 7, 2)) == false
  }
  
  def "it allows you to know if another set is potentially a direct parent set of the current set" () {
    expect: "to return true if the described set is a direct parent set of the current set, false otherwise"
      new NestedSetCoordinates(5, 8, 3).isDirectParentSet(new NestedSetCoordinates(4, 9, 2)) == true
      new NestedSetCoordinates(5, 8, 3).isDirectParentSet(new NestedSetCoordinates(3, 10, 1)) == false
      new NestedSetCoordinates(5, 8, 3).isDirectParentSet(new NestedSetCoordinates(5, 8, 2)) == false
      new NestedSetCoordinates(5, 8, 3).isDirectParentSet(new NestedSetCoordinates(5, 8, 3)) == false
      new NestedSetCoordinates(5, 8, 3).isDirectParentSet(new NestedSetCoordinates(6, 11, 2)) == false
      new NestedSetCoordinates(5, 8, 3).isDirectParentSet(new NestedSetCoordinates(4, 7, 2)) == false
  }
  
  def "it allows you to reset the coordinates to their default values" () {
    expect: "to reset the given coordinates to their default values when setDefault is called"
      final NestedSetCoordinates coordinates = new NestedSetCoordinates(5, 8, 3)
      coordinates.depth == 3
      coordinates.start == 5
      coordinates.end == 8
      
      coordinates.setDefault()
      coordinates.depth == 1
      coordinates.start == 1
      coordinates.end == 2 
  }
  
  def "it allows you to move the coordinates to the left" () {
    expect: "to move the coordinates to the left when moveLeft is called"
      final NestedSetCoordinates coordinates = new NestedSetCoordinates(5, 8, 3)
      coordinates.moveLeft(5)
      coordinates.start == 0
      coordinates.end == 3
      coordinates.depth == 3
  }

  def "it allows you to move the coordinates to the right" () {
    expect: "to move the coordinates to the right when moveLeft is called"
      final NestedSetCoordinates coordinates = new NestedSetCoordinates(5, 8, 3)
      coordinates.moveRight(5)
      coordinates.start == 10
      coordinates.end == 13
      coordinates.depth == 3
  }
  
  def "it define its own hash code method" () {
    expect: "to return the equivalent of Objects.hash with start, end and depth coordinates"
      final NestedSetCoordinates coordinates = new NestedSetCoordinates(5, 8, 3)
      coordinates.hashCode() == Objects.hash(5, 8, 3)
      new NestedSetCoordinates(23, 26, 2).hashCode() == new NestedSetCoordinates(23, 26, 2).hashCode()
  }
  
  def "it define its own equals method" () {
    expect: "to return true when equals is called and both instance have the same coordinates"
      new NestedSetCoordinates(23, 26, 2).equals(new NestedSetCoordinates(23, 26, 2)) == true
      new NestedSetCoordinates(23, 26, 2).equals(new NestedSetCoordinates(23, 26, 8)) == false
      new NestedSetCoordinates(23, 26, 2).equals(new NestedSetCoordinates(21, 26, 2)) == false
      new NestedSetCoordinates(23, 26, 2).equals(new NestedSetCoordinates(23, 28, 2)) == false
      new NestedSetCoordinates(23, 26, 2).equals(null) == false
      new NestedSetCoordinates(23, 26, 2).equals(new Object()) == false
      NestedSetCoordinates instance = new NestedSetCoordinates(23, 26, 2)
      instance.equals(instance) == true
  }
}
