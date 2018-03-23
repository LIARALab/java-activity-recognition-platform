package org.domus.api.data.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

@Entity
@Table(name = "sensors")
public class Sensor
{
  private int    identifier;
  private Date   creationDate;
  private Date   lastUpdateDate;
  private Date   deletionDate;
  private String name;
  private String type;
  private String unit;
  private String valueLabel;
  private String ipv4Address;
  private String ipv6Address;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public int getIdentifier () {
    return this.identifier;
  }

  public void setIdentifier (final int identifier) {
    this.identifier = identifier;
  }

  @Column(name = "created_at")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public Date getCreationDate () {
    return this.creationDate;
  }

  public void setCreationDate (@NonNull final Date creationDate) {
    this.creationDate = creationDate;
  }

  @Column(name = "updated_at")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public Date getLastUpdateDate () {
    return this.lastUpdateDate;
  }

  public void setLastUpdateDate (@NonNull final Date lastUpdateDate) {
    this.lastUpdateDate = lastUpdateDate;
  }

  @Column(name = "deleted_at")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public Date getDeletionDate () {
    return this.deletionDate;
  }

  public void setDeletionDate (@NonNull final Date deletionDate) {
    this.deletionDate = deletionDate;
  }

  @Column(name = "ipv4_address")
  public String getIpv4Address () {
    return this.ipv4Address;
  }

  public void setIpv4Address (@NonNull final String ipv4Address) {
    this.ipv4Address = ipv4Address;
  }

  @Column(name = "ipv6_address")
  public String getIpv6Address () {
    return this.ipv6Address;
  }

  public void setIpv6Address (@NonNull final String ipv6Address) {
    this.ipv6Address = ipv6Address;
  }

  @Column(name = "name")
  public String getName () {
    return this.name;
  }

  public void setName (@NonNull final String name) {
    this.name = name;
  }

  @Column(name = "type")
  public String getType () {
    return this.type;
  }

  public void setType (@NonNull final String type) {
    this.type = type;
  }

  @Column(name = "unit")
  public String getUnit () {
    return this.unit;
  }

  public void setUnit (@NonNull final String unit) {
    this.unit = unit;
  }

  @Column(name = "value_label")
  public String getValueLabel () {
    return this.valueLabel;
  }

  public void setValueLabel (@NonNull final String valueLabel) {
    this.valueLabel = valueLabel;
  }
}
