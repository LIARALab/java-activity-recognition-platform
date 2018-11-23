package org.liara.api.data.entity.state;

import org.liara.api.data.entity.ApplicationEntity;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Entity
@Table(name = "correlations_of_states")
public class Correlation
  extends ApplicationEntity
{
  @Nullable
  @ManyToOne(optional = false)
  @JoinColumn(name = "master_identifier", nullable = false, unique = false, updatable = true)
  private State _master;

  @Nullable
  @ManyToOne(optional = false)
  @JoinColumn(name = "slave_identifier", nullable = false, unique = false, updatable = true)
  private State _slave;

  @Nullable
  @Column(name = "label", nullable = false, updatable = true, unique = false)
  private String _label;

  public Correlation () {
    _master = null;
    _slave = null;
    _label = null;
  }

  public @Nullable
  State getMaster () {
    return _master;
  }

  public void setMaster (@Nullable final State master) {
    _master = master;
  }

  public @Nullable
  State getSlave () {
    return _slave;
  }

  public void setSlave (@Nullable final State slave) {
    _slave = slave;
  }

  public @Nullable
  String getLabel () {
    return _label;
  }

  public void setLabel (@Nullable final String label) {
    _label = label;
  }
}
