/*******************************************************************************
 * Copyright (C) 2018 Cédric DEMONGIVERT <cedric.demongivert@gmail.com>
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
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;
import javax.persistence.Column;

import org.hibernate.annotations.Formula;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.sensor.Sensor;
import org.liara.api.data.entity.state.PresenceState;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
@Table(name = "nodes")
public class Node extends ApplicationEntity
{
  @Column(name = "name", nullable = false, updatable = true, unique = false)
  @NonNull
  private String        _name;

  @Column(name = "set_start", nullable = true, updatable = true, unique = false)
  private int           _setStart;

  @Column(name = "set_end", nullable = true, updatable = true, unique = false)
  private int           _setEnd;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "sensors_by_nodes",
      joinColumns = @JoinColumn(name = "node_identifier"),
      inverseJoinColumns = @JoinColumn(name = "sensor_identifier")
  )
  private List<Sensor>  _sensors;
  
  @OneToMany(mappedBy="_node", cascade = CascadeType.ALL, orphanRemoval = false)
  private List<PresenceState>  _presences;
  
  @Formula("(SELECT (COUNT(*) - 1) FROM nodes AS parent WHERE set_start BETWEEN parent.set_start AND parent.set_end)")
  private int _depth;
  
  public Node () {
    _name = null;
    _setStart = 0;
    _setEnd = 0;
  }
  
  public Node (final int setStart, final int setEnd) {
    _name = null;
    _setStart = setStart;
    _setEnd = setEnd;
  }
  
  public String getName () {
    return _name;
  }
  
  public void setName (@NonNull final String name) {
    _name = name;
  }
  
  public int getSetStart () {
    return _setStart;
  }

  public int getSetEnd () {
    return _setEnd;
  }
  
  public int getDepth () {
    return _depth;
  }

  @JsonIgnore
  public List<Sensor> getSensors () {
    return _sensors;
  }
}
