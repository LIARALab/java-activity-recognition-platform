package org.domus.api.data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
//import org.hibernate.annotations.GeneratedValue;
//import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Column;

import java.util.Date;

@Entity
@Table(name="sensors")
public class Sensor {
  private int identifier;
  private Date creationDate;
  private Date updateDate;
  private Date deletionDate;
  private String name;
  private String type;
  private String unit;
  private String valueLabel;
  private String ipv4Address;
  private String ipv6Address;

  @Id
  //@GeneratedValue(generator="increment")
  //@GenericGenerator(name="increment", strategy = "increment")
  public int getIdentifier () {
    return this.identifier;
  }

  public void setIdentifier (int identifier) {
    this.identifier = identifier;
  }

/*
  @Column(name = "created_at")
  public Date getCreationDate () {
    return this.creationDate;
  }

  @Column(name = "updated_at")
  public Date getUpdateDate () {
    return this.lastUpdateDate;
  }

  @Column(name = "deleted_at")
  public Date getDeletionDate () {
    return this.deletionDate;
  }
*/

  @Column(name = "ipv4_address")
  public String getIpv4Address () {
    return this.ipv4Address;
  }

  public void setIpv4Address (String ipv4Address) {
    this.ipv4Address = ipv4Address;
  }

  @Column(name = "ipv6_address")
  public String getIpv6Address () {
    return this.ipv6Address;
  }

  public void setIpv6Address (String ipv6Address) {
    this.ipv6Address = ipv6Address;
  }

  @Column(name = "name")
  public String getName () {
    return this.name;
  }

  public void setName (String name) {
    this.name = name;
  }

  @Column(name = "type")
  public String getType () {
    return this.type;
  }

  public void setType (String type) {
    this.type = type;
  }

  @Column(name = "unit")
  public String getUnit () {
    return this.unit;
  }

  public void setUnit (String unit) {
    this.unit = unit;
  }

  @Column(name = "value_label")
  public String getValueLabel () {
    return this.valueLabel;
  }

  public void setValueLabel (String valueLabel) {
    this.valueLabel = valueLabel;
  }
}
