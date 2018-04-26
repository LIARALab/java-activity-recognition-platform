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

import org.liara.api.data.collection.NodeCollection;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.liara.api.validation.Required;
import org.liara.api.validation.RestGroup;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;

public class NodeModifier
{
  @NonNull
  @Required(groups = { RestGroup.EntityCreation.class })
  private Optional<String> _name = Optional.empty();
  
  @NonNull
  @Required(groups = { RestGroup.EntityCreation.class })
  private Optional<String> _type = Optional.empty();
  
  @NonNull
  @IdentifierOfEntityInCollection(
    collection = NodeCollection.class, 
    groups = { RestGroup.Any.class }
  )
  private Optional<Long> _parent = Optional.empty();
 
  public Optional<String> getName () {
    return _name;
  }
  
  @JsonSetter
  public void setName (@Nullable final String name) {
    _name = Optional.of(name);
  }

  public void setName (@NonNull final Optional<String> name) {
    _name = name;
  }
  
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
  
  public Optional<Long> getParent () {
    return _parent;
  }
  
  @JsonSetter
  public void setParent (final Long parent) {
    _parent = Optional.of(parent);
  }
  
  public void setParent (@Nullable final Node parent) {
    if (parent == null) {
      _parent = Optional.of(null);
    } else {
      _parent = Optional.of(parent.getIdentifier());
    }
  }

  public void setParent (@NonNull final Optional<Long> parent) {
    _parent = parent;
  }
}
