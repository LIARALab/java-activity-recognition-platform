package org.domus.api.data.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name="`states<double>`")
public class DoubleState extends State {
  private double value;

  @Column(name = "value")
  public double getValue () {
    return this.value;
  }

  public void setValue (final double value) {
    this.value = value;
  }
}
