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

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.schema.Schema;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.liara.api.validation.Required;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;

@Schema(Node.class)
public final class NodeCreationSchema
{
  @Nullable
  @Required
  private String _name = null;
  
  @Nullable
  @Required
  private String _type = null;
  
  @NonNull
  @ValidApplicationEntityReference
  private ApplicationEntityReference<Node> _parent = ApplicationEntityReference.empty(Node.class);
  
  /**
   * Return the name that will be assigned to the created node.
   * 
   * @return The name that will be assigned to the created node.
   */
  public String getName () {
    return _name;
  }
  
  /**
   * Set the name that will be assigned to the created node.
   * 
   * @param name The name that will be assigned to the created node.
   */
  @JsonSetter
  public void setName (@Nullable final String name) {
    _name = name;
  }

  /**
   * Set the name that will be assigned to the created node.
   * 
   * @param name The name that will be assigned to the created node.
   */
  public void setName (@NonNull final Optional<String> name) {
    _name = name.orElse(null);
  }
  
  /**
   * Return the type that will be assigned to the created node.
   * 
   * @return The type that will be assigned to the created node.
   */
  public String getType () {
    return _type;
  }
  
  @JsonSetter
  public void setType (@Nullable final String type) {
    _type = type;
  }

  public void setType (@NonNull final Optional<String> type) {
    _type = type.orElse(null);
  }
  
  public ApplicationEntityReference<Node> getParent () {
    return _parent;
  }
  
  @JsonSetter
  public void setParent (final Long parent) {
    _parent = ApplicationEntityReference.of(Node.class, parent);
  }
  
  public void setParent (@Nullable final Node parent) {
    _parent = (parent == null) ? ApplicationEntityReference.empty(Node.class) 
                               : ApplicationEntityReference.of(parent);
  }
}
