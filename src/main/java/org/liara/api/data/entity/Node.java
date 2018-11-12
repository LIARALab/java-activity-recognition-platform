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
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.data.entity.schema.NodeSchema;
import org.liara.api.data.tree.NestedSet;
import org.liara.api.data.tree.NestedSetCoordinates;

import javax.persistence.*;

@Entity
@Table(name = "nodes")
@JsonPropertyOrder({"identifier", "type", "name", "coordinates"})
public class Node
  extends ApplicationEntity
  implements NestedSet
{
  @Nullable
  private String _name;

  @NonNull
  private NestedSetCoordinates _coordinates;

  public Node () {
    _name = null;
    _coordinates = new NestedSetCoordinates();
  }

  public Node (@NonNull final NodeSchema toCopy) {
    _name = toCopy.getName();
    _coordinates = new NestedSetCoordinates();
  }

  public Node (@NonNull final Node toCopy) {
    super(toCopy);
    _name = toCopy.getName();
    _coordinates = new NestedSetCoordinates(toCopy.getCoordinates());
  }

  /**
   * Return the name of this node.
   * <p>
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
    return new NestedSetCoordinates(_coordinates);
  }

  public void setCoordinates (@Nullable final NestedSetCoordinates coordinates) {
    _coordinates = (coordinates == null) ? new NestedSetCoordinates() : new NestedSetCoordinates(coordinates);
  }

  @Override
  @Transient
  public @NonNull ApplicationEntityReference<? extends Node> getReference () {
    return ApplicationEntityReference.of(this);
  }
}
