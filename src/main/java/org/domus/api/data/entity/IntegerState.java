package org.domus.api.data.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;

import java.util.Date;

@Entity
@Table(name="`states<int>`")
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
