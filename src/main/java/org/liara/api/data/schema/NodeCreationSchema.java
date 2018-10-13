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
package org.liara.api.data.schema;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.Node;
import org.liara.api.data.entity.reference.ApplicationEntityReference;
import org.liara.api.validation.Required;
import org.liara.api.validation.ValidApplicationEntityReference;

@Schema(Node.class)
public final class NodeCreationSchema
  extends ApplicationEntityCreationSchema
{
  @Nullable
  private String _name;
  
  @Nullable
  private String _type;

  @Nullable
  private ApplicationEntityReference<Node> _parent;

  public NodeCreationSchema () {
    super();
    _type = null;
    _name = null;
    _parent = null;
  }

  public NodeCreationSchema (@NonNull final NodeCreationSchema toCopy) {
    super(toCopy);
    _type = toCopy.getType();
    _name = toCopy.getName();
    _parent = toCopy.getParent();
  }
  
  /**
   * Return the name that will be assigned to the created node.
   * 
   * @return The name that will be assigned to the created node.
   */
  @Required
  public @Nullable String getName () {
    return _name;
  }
  
  /**
   * Set the name that will be assigned to the created node.
   * 
   * @param name The name that will be assigned to the created node.
   */
  public void setName (@Nullable final String name) {
    _name = name;
  }
  
  /**
   * Return the type that will be assigned to the created node.
   * 
   * @return The type that will be assigned to the created node.
   */
  @Required
  public @Nullable String getType () {
    return _type;
  }

  public void setType (@Nullable final String type) {
    _type = type;
  }

  @ValidApplicationEntityReference
  @Required
  public @Nullable ApplicationEntityReference<Node> getParent () {
    return _parent;
  }

  public void setParent (@Nullable final ApplicationEntityReference<Node> parent) {
    _parent = parent;
  }
}
