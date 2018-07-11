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
import javax.persistence.Transient;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;

import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.ActivationState;
import org.liara.api.data.entity.tree.LocalNestedSetTree;
import org.liara.api.data.entity.tree.NestedSetCoordinates;
import org.liara.api.data.entity.tree.NestedSetTree;
import org.liara.api.data.entity.tree.NestedSetTreeNode;
import org.liara.api.data.schema.UseCreationSchema;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "nodes")
@UseCreationSchema(NodeCreationSchema.class)
@JsonPropertyOrder({ "identifier", "name", "type", "coordinates" })
public class      Node 
       extends    ApplicationEntity 
       implements NestedSetTreeNode<Node>
{  
  @NonNull
  @Column(name = "name", nullable = false, updatable = true, unique = false)
  private String                _name;

  @NonNull
  @Column(name = "type", nullable = false, updatable = false, unique = false)
  private String                _type;

  @NonNull
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "_node")
  private Set<Sensor>          _sensors = new HashSet<>();

  @NonNull
  @OneToMany(mappedBy = "_node", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
  private Set<ActivationState> _presences = new HashSet<>();
  
  @NonNull
  @Transient
  private NestedSetTree<Node> _tree;
  
  @Nullable
  @Embedded
  private NestedSetCoordinates _coordinates;
  
  protected Node () {
    _tree = DatabaseNodeTree.getInstance();
    _name = null;
    _type = null;
    _coordinates = new NestedSetCoordinates();
  }
  
  public Node (@NonNull final NodeCreationSchema schema) {
    _tree = DatabaseNodeTree.getInstance();
    _name = schema.getName();
    _type = schema.getType();
    _coordinates = new NestedSetCoordinates();
  }
  
  public Node (
    @NonNull final LocalNestedSetTree<Node> tree,
    @NonNull final NodeCreationSchema schema
  ) {
    setTree(tree);
    _name = schema.getName();
    _type = schema.getType();
  }
  
  /**
   * Create a new node from a creation schema.
   * 
   * @param schema Schema to use in order to create this node.
   * @param collection Future collection of this node.
   */
  public Node (
    @NonNull final NodeCreationSchema schema,
    @NonNull final NodeCollection collection
  ) {
    _name = schema.getName();
    _type = schema.getType();
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
   * Return all sensors directly attached to this node.
   * 
   * @return All sensors directly attached to this node.
   */
  @JsonIgnore
  public Iterable<Sensor> sensors () {
    return Collections.unmodifiableSet(_sensors);
  }
  
  /**
   * Add a sensor to this node.
   * 
   * @param sensor A sensor to add to this node.
   */
  public void addSensor (@NonNull final Sensor sensor) {
    if (!_sensors.contains(sensor)) {
      _sensors.add(sensor);
      sensor.setNode(this);
    }
  }
  
  /**
   * Remove a sensor from this node.
   * 
   * @param sensor A sensor to remove from this node.
   */
  public void removeSensor (@NonNull final Sensor sensor) {
    if (_sensors.contains(sensor)) {
      _sensors.remove(sensor);
      sensor.setNode(null);
    }
  }
  
  public boolean hasSensor (@NonNull final Sensor sensor) {
    return _sensors.contains(sensor);
  }
  
  @Override
  public NodeSnapshot snapshot () {
    return new NodeSnapshot(this);
  }

  @Override
  @JsonIgnore
  public NestedSetTree<Node> getTree () {
    return _tree;
  }

  @Override
  public void setTree (@Nullable final NestedSetTree<Node> tree) {
    if (!Objects.equals(_tree, tree)) {
      if (!Objects.equals(_tree, null)) {
        final NestedSetTree<Node> oldTree = _tree;
        _tree = null;
        oldTree.removeNode(this);
      }
      
      _tree = tree;
      
      if (!Objects.equals(_tree, null)) {
        _tree.addNode(this);
      }
    }
  }

  @Override
  public NestedSetCoordinates getCoordinates () {
    if (_tree == null) {
      return new NestedSetCoordinates();
    } else {
      return _tree.getCoordinatesOf(this);
    }
  }

  @Override
  @JsonIgnore
  public Set<Node> getChildren () {
    return _tree.getChildrenOf(this);
  }

  @Override
  @JsonIgnore
  public Set<Node> getAllChildren () {
    return _tree.getAllChildrenOf(this);
  }

  @Override
  public void addChild (@NonNull final Node node) {
    _tree.addNode(node, this);
  }

  @Override
  public void removeChild (@NonNull final Node node) {
    _tree.removeNode(node);
  }

  @Override
  @JsonIgnore
  public Node getParent () {
    return _tree.getParentOf(this);
  }

  @Override
  public void setParent (@NonNull final Node node) {
    _tree.addNode(this, node);
  }
}
