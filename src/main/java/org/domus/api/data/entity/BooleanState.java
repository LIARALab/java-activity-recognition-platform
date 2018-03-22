package org.domus.api.data.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name="`states<boolean>`")
public class BooleanState extends State {
  private boolean value;

  @Column(name = "value")
  public boolean getValue () {
    return this.value;
  }

  public void setValue (final boolean value) {
    this.value = value;
  }
}
