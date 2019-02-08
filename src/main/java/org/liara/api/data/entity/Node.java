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
package org.liara.api.data.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.tree.NestedSet;
import org.liara.api.data.tree.NestedSetCoordinates;
import org.liara.api.relation.RelationFactory;
import org.liara.collection.operator.Composition;
import org.liara.collection.operator.Operator;
import org.liara.collection.operator.filtering.Filter;
import org.liara.collection.operator.joining.Join;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "nodes")
@JsonPropertyOrder({"identifier", "uuid", "type", "name", "coordinates"})
public class Node
  extends ApplicationEntity
  implements NestedSet
{
  @RelationFactory(Tag.class)
  public static @NonNull Operator tags () {
    return ApplicationEntity.tags(Node.class);
  }

  public static @NonNull Operator tags (@NonNull final Node node) {
    return ApplicationEntity.tags(Node.class, node);
  }

  @RelationFactory(TagRelation.class)
  public static @NonNull Operator tagRelations () {
    return ApplicationEntity.tagRelations(Node.class);
  }

  public static @NonNull Operator tagRelations (@NonNull final Node node) {
    return ApplicationEntity.tagRelations(Node.class, node);
  }

  @RelationFactory(Node.class)
  public static @NonNull Operator parent () {
    return Join.inner(
      Node.class, "parent"
    ).filter(Filter.expression(":this.coordinates.start < :super.coordinates.start "))
             .filter(Filter.expression(":this.coordinates.end > :super.coordinates.end"))
             .filter(Filter.expression(":this.coordinates.depth = :super.coordinates.depth - 1"));
  }

  public static @NonNull Operator parent (@NonNull final Node node) {
    return Composition.of(
      Node.parents(node),
      Filter.expression(":this.coordinates.depth = :depth - 1")
        .setParameter("depth", node.getCoordinates().getDepth())
    );
  }

  @RelationFactory(Node.class)
  public static @NonNull Operator parents () {
    return Composition.of(
      Filter.expression(":this.coordinates.start < :super.coordinates.start"),
      Filter.expression(":this.coordinates.end > :super.coordinates.end")
    );
  }

  public static @NonNull Operator parents (@NonNull final Node node) {
    return Composition.of(
      Filter.expression(":this.coordinates.start < :start")
        .setParameter("start", node.getCoordinates().getStart()),
      Filter.expression(":this.coordinates.end > :end")
        .setParameter("end", node.getCoordinates().getEnd())
    );
  }

  @RelationFactory(Node.class)
  public static @NonNull Operator deepChildren () {
    return Composition.of(
      Filter.expression(":this.coordinates.start > :super.coordinates.start"),
      Filter.expression(":this.coordinates.end < :super.coordinates.end")
    );
  }

  public static @NonNull Operator deepChildren (@NonNull final Node node) {
    return Composition.of(
      Filter.expression(":this.coordinates.start > :start")
        .setParameter("start", node.getCoordinates().getStart()),
      Filter.expression(":this.coordinates.end < :end")
        .setParameter("end", node.getCoordinates().getEnd())
    );
  }

  @RelationFactory(Node.class)
  public static @NonNull Operator children () {
    return Composition.of(
      Node.deepChildren(),
      Filter.expression(":this.coordinates.depth = :super.coordinates.depth + 1")
    );
  }

  public static @NonNull Operator children (@NonNull final Node node) {
    return Composition.of(
      Node.deepChildren(node),
      Filter.expression(":this.coordinates.depth = :depth + 1")
        .setParameter("depth", node.getCoordinates().getDepth())
    );
  }

  @Nullable
  private String _name;

  @NonNull
  private NestedSetCoordinates _coordinates;

  public Node () {
    _name = null;
    _coordinates = new NestedSetCoordinates();
  }

  public Node (@NonNull final Node toCopy) {
    super(toCopy);
    _name = toCopy.getName();
    _coordinates = new NestedSetCoordinates(toCopy.getCoordinates());
  }

  /**
   * Return the name of this node.
   *
   * It's only a label in order to easily know what this node represents.
   *
   * @return The name of this node.
   */
  @Column(name = "name", nullable = false)
  public @Nullable String getName () {
    return _name;
  }

  /**
   * Rename this node.
   *
   * @param name The new name to apply to this node.
   */
  public void setName (@Nullable final String name) {
    _name = name;
  }

  @Embedded
  public @NonNull NestedSetCoordinates getCoordinates () {
    return _coordinates;
  }

  public void setCoordinates (@Nullable final NestedSetCoordinates coordinates) {
    _coordinates = coordinates == null ? new NestedSetCoordinates()
                                       : coordinates;
  }
}
