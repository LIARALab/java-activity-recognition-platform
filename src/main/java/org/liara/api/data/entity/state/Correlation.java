package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.reference.ApplicationEntityReference;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "state_correlations")
public class Correlation
  extends ApplicationEntity
{
  @Nullable
  private ApplicationEntityReference<? extends State> _startStateIdentifier;

  @Nullable
  private ApplicationEntityReference<? extends State> _endStateIdentifier;

  @Nullable
  private String _name;

  public Correlation () {
    _startStateIdentifier = null;
    _endStateIdentifier = null;
    _name = null;
  }

  public Correlation (@NonNull final Correlation toCopy) {
    _startStateIdentifier = toCopy.getStartStateIdentifier();
    _endStateIdentifier = toCopy.getEndStateIdentifier();
    _name = toCopy.getName();
  }

  public @Nullable ApplicationEntityReference<? extends State> getStartStateIdentifier () {
    return _startStateIdentifier;
  }

  public void setStartStateIdentifier (@Nullable final ApplicationEntityReference<? extends State> startStateIdentifier) {
    _startStateIdentifier = startStateIdentifier;
  }

  public @Nullable ApplicationEntityReference<? extends State> getEndStateIdentifier () {
    return _endStateIdentifier;
  }

  public void setEndStateIdentifier (
    @Nullable final ApplicationEntityReference<? extends State> endStateIdentifier
  )
  {
    _endStateIdentifier = endStateIdentifier;
  }

  public @Nullable String getName () {
    return _name;
  }

  public void setName (@Nullable final String name) {
    _name = name;
  }
}
