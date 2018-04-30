/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.liara.api.data.entity.node;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.liara.api.collection.EntityNotFoundException;
import org.liara.api.data.collection.NodeCollection;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

@Component
@Scope("prototype")
public final class NodeCreationSchema
{
  @NonNull
  @Required
  private Optional<String> _name = Optional.empty();
  
  @NonNull
  @Required
  private Optional<String> _type = Optional.empty();
  
  @NonNull
  @IdentifierOfEntityInCollection(collection = NodeCollection.class)
  private Optional<Long> _parentIdentifier = Optional.empty();
  
  @NonNull
  @Autowired
  private NodeCollection _collection;
  
  @Nullable
  private Node _parent;
 
  /**
   * Return the name that will be assigned to the created node.
   * 
   * @return The name that will be assigned to the created node.
   */
  public Optional<String> getName () {
    return _name;
  }
  
  @JsonSetter
  /**
   * Set the name that will be assigned to the created node.
   * 
   * @param name The name that will be assigned to the created node.
   */
  public void setName (@Nullable final String name) {
    _name = Optional.of(name);
  }

  /**
   * Set the name that will be assigned to the created node.
   * 
   * @param name The name that will be assigned to the created node.
   */
  public void setName (@NonNull final Optional<String> name) {
    _name = name;
  }
  
  /**
   * Return the type that will be assigned to the created node.
   * 
   * @return The type that will be assigned to the created node.
   */
  public Optional<String> getType () {
    return _type;
  }
  
  @JsonSetter
  public void setType (@Nullable final String type) {
    _type = Optional.of(type);
  }

  public void setType (@NonNull final Optional<String> type) {
    _type = type;
  }
  
  public Optional<Long> getParentIdentifier () {
    return _parentIdentifier;
  }
  
  @JsonIgnore
  public Node getParent () {
    if (!_parentIdentifier.isPresent() || _parentIdentifier.get() == null) {
      return null;
    }
    
    if (_parent == null) {
      try {
        _parent = _collection.findByIdentifierOrFail(_parentIdentifier.get());
      } catch (final EntityNotFoundException exception) {
        throw new IllegalStateException(String.join(
          "", 
          "Invalid NodeCreationSchema : the refered parent identifier does not ",
          "exists into this application's node collection"
        ));
      }
    }
    
    return _parent;
  }
  
  @JsonSetter
  public void setParent (final Long parent) {
    _parentIdentifier = Optional.ofNullable(parent);
    _parent = null;
  }
  
  public void setParent (@Nullable final Node parent) {
    if (parent == null) {
      _parentIdentifier = Optional.of(null);
    } else {
      _parentIdentifier = Optional.of(parent.getIdentifier());
    }
    _parent = null;
  }

  public void setParent (@NonNull final Optional<Long> parent) {
    _parentIdentifier = parent;
    _parent = null;
  }
  
  public int getSetStart () {
    if (_parentIdentifier.isPresent()) {
      return this.getParent().getSetEnd();      
    } else {
      return _collection.getRootSetEnd();
    }
  }
  
  public int getSetEnd () {
    return getSetStart() + 1;
  }

  @Transactional
  public Node build () {
    final EntityManager manager = _collection.getManager();
    
    if (getParent() != null) {
      manager.createQuery(
        "UPDATE Node SET _setStart = _setStart + 2 WHERE _setStart > :parentSetEnd"
      ).setParameter("parentSetEnd", getParent().getSetEnd()).executeUpdate();
      
      manager.createQuery(
        "UPDATE Node SET _setEnd = _setEnd + 2 WHERE _setEnd >= :parentSetEnd"
      ).setParameter("parentSetEnd", getParent().getSetEnd()).executeUpdate();
    }
    
    final Node node = new Node(this);
    
    _collection.getManager().persist(node);
    
    return node;
  }
}
