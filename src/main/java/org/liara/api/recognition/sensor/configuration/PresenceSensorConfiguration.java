package org.liara.api.recognition.sensor.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.liara.api.data.collection.NodeCollection;
import org.liara.api.data.entity.node.Node;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.validation.IdentifierOfEntityInCollection;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;

public class PresenceSensorConfiguration implements SensorConfiguration
{
  @NonNull
  private final Set<Long> _potentiallyOutdoorNodes;
  
  @NonNull
  private Long _outdoorNode;
  
  public PresenceSensorConfiguration () {
    _potentiallyOutdoorNodes = new HashSet<>();
    _outdoorNode = null;
  }
  
  public PresenceSensorConfiguration (@NonNull final PresenceSensorConfiguration toCopy) {
    _potentiallyOutdoorNodes = new HashSet<>(toCopy.getPotentiallyOutdoorNodes());
    _outdoorNode = toCopy.getOutdoorNode();
  }
  
  public void setPotentiallyOutdoorNodes (@NonNull final Set<Long> nodes) {
    _potentiallyOutdoorNodes.clear();
    _potentiallyOutdoorNodes.addAll(nodes);
  }
  
  @IdentifierOfEntityInCollection(collection = NodeCollection.class)
  public Set<Long> getPotentiallyOutdoorNodes () {
    return Collections.unmodifiableSet(_potentiallyOutdoorNodes);
  }
  
  public Iterable<Long> potentiallyOutdoorNodes () {
    return Collections.unmodifiableSet(_potentiallyOutdoorNodes);
  }
  
  public boolean isPotentiallyOutdoorNode (@NonNull final Long node) {
    return _potentiallyOutdoorNodes.contains(node);
  }
  
  public boolean isPotentiallyOutdoorNode (@NonNull final Node node) {
    return _potentiallyOutdoorNodes.contains(node.getIdentifier());
  }
  
  public void setPotentiallyOutdoorNodes (@NonNull final Iterable<Long> nodes) {
    _potentiallyOutdoorNodes.clear();
    nodes.forEach(_potentiallyOutdoorNodes::add);
  }
  
  @JsonSetter
  public void setPotentiallyOutdoorNodes (@NonNull final Collection<Long> nodes) {
    _potentiallyOutdoorNodes.addAll(nodes);
  }
  
  @IdentifierOfEntityInCollection(collection = NodeCollection.class)
  public Long getOutdoorNode () {
    return _outdoorNode;
  }
  
  @JsonSetter
  public void setOutdoorNode (@Nullable final Long identifier) {
    _outdoorNode = identifier;
  }
  
  public void setOutdoorNode (@Nullable final Node node) {
    _outdoorNode = node.getIdentifier();
  }

  @Override
  public PresenceSensorConfiguration clone () {
    return new PresenceSensorConfiguration(this);
  }
}
