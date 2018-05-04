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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.Column;

import org.hibernate.annotations.Formula;
import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.schema.UseCreationSchema;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "nodes")
@UseCreationSchema(NodeCreationSchema.class)
public class Node extends ApplicationEntity
{
  @Column(name = "name", nullable = false, updatable = true, unique = false)
  @NonNull
  private String                _name;

  @Column(name = "type", nullable = false, updatable = false, unique = false)
  @NonNull
  private String                _type;

  @Column(name = "set_start", nullable = true, updatable = true, unique = false)
  private int                   _setStart;

  @Column(name = "set_end", nullable = true, updatable = true, unique = false)
  private int                   _setEnd;

  @OneToMany(
    cascade = CascadeType.ALL, 
    fetch = FetchType.LAZY,
    mappedBy = "_node"
  )
  private List<Sensor>          _sensors;

  @OneToMany(mappedBy = "_node", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
  private List<ActivationState> _presences;

  @Formula("(SELECT (COUNT(*) - 1) FROM nodes AS parent WHERE set_start BETWEEN parent.set_start AND parent.set_end)")
  private int                   _depth;

  public Node () { }
  
  /**
   * Create a new node from a creation schema.
   * 
   * @param schema Schema to use in order to create this node.
   * @param collection Future collection of this node.
   */
  public Node(
    @NonNull final NodeCreationSchema schema,
    @NonNull final NodeCollection collection
  ) {
    _name = schema.getName();
    _type = schema.getType();
    
    if (schema.getParent() == null) {
      _setStart = collection.getRootSetEnd();
      _depth = 0;
    } else {
      final Node parent = collection.findByIdentifier(schema.getParent()).get();
      _setStart = parent.getSetEnd();
      _depth = parent.getDepth() + 1;
    }
    
    _setEnd = _setStart + 1;
    _sensors = Collections.emptyList();
    _presences = Collections.emptyList();
  }

  /**
   * Return the type of this node.
   * 
   * A type is a generic name linked to a class that allows to do special operations on this node.
   * 
   * @return The type of this node.
   */
  public String getType () {
    return _type;
  }

  /**
   * Return the name of this node.
   * 
   * It's only a label in order to easily know what this node represents.
   * 
   * @return The name of this node.
   */
  public String getName () {
    return _name;
  }

  /**
   * Rename this node.
   * 
   * @param name The new name to apply to this node.
   */
  public void setName (@NonNull final String name) {
    _name = name;
  }

  /**
   * Return the start index of the nested-set that represent this tree node.
   * 
   * @see https://en.wikipedia.org/wiki/Nested_set_model
   * 
   * @return The start index of the nested-set that represent this tree node.
   */
  public int getSetStart () {
    return _setStart;
  }

  /**
   * Return the end index of the nested-set that represent this tree node.
   * 
   * @see https://en.wikipedia.org/wiki/Nested_set_model
   * 
   * @return The end index of the nested-set that represent this tree node.
   */
  public int getSetEnd () {
    return _setEnd;
  }

  /**
   * Return the depth of this node from its root into the application tree.
   * 
   * It's also the number of parents that this node have.
   * 
   * @return The depth of this node from its root into the application tree.
   */
  public int getDepth () {
    return _depth;
  }

  /**
   * Return all sensors directly attached to this node.
   * 
   * @return All sensors directly attached to this node.
   */
  @JsonIgnore
  public List<Sensor> getSensors () {
    return _sensors;
  }
  
  @Override
  public NodeSnapshot snapshot () {
    return new NodeSnapshot(this);
  }
}
