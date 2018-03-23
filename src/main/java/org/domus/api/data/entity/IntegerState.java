package org.domus.api.data.entity;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name="`states<int>`")
@PrimaryKeyJoinColumn(name="state_identifier")
public class IntegerState extends State {
  private int value;

  @Column(name = "value")
  public int getValue () {
    return this.value;
  }

  public void setValue (final int value) {
    this.value = value;
  }
}
