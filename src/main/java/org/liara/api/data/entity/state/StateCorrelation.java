package org.liara.api.data.entity.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.liara.api.data.entity.ApplicationEntity;
import org.liara.api.data.entity.reference.ApplicationEntityReference;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "state_correlations")
public class StateCorrelation
  extends ApplicationEntity
{
  @Nullable
  private ApplicationEntityReference<? extends State> _baseIdentifier;

  @Nullable
  private ApplicationEntityReference<? extends State> _correlatedIdentifier;

  @Nullable
  private String _name;

  public StateCorrelation () {
    _baseIdentifier = null;
    _correlatedIdentifier = null;
    _name = null;
  }

  public StateCorrelation (@NonNull final StateCorrelation toCopy) {
    _baseIdentifier = toCopy.getBaseIdentifier();
    _correlatedIdentifier = toCopy.getCorrelatedIdentifier();
    _name = toCopy.getName();
  }

  public @Nullable ApplicationEntityReference<? extends State> getBaseIdentifier () {
    return _baseIdentifier;
  }

  public void setBaseIdentifier (@Nullable final ApplicationEntityReference<? extends State> baseIdentifier) {
    _baseIdentifier = baseIdentifier;
  }

  public @Nullable ApplicationEntityReference<? extends State> getCorrelatedIdentifier () {
    return _correlatedIdentifier;
  }

  public void setCorrelatedIdentifier (
    @Nullable final ApplicationEntityReference<? extends State> correlatedIdentifier
  )
  {
    _correlatedIdentifier = correlatedIdentifier;
  }

  public @Nullable String getName () {
    return _name;
  }

  public void setName (@Nullable final String name) {
    _name = name;
  }

  @Override
  public @NonNull StateCorrelation clone () {
    return new StateCorrelation(this);
  }
}
