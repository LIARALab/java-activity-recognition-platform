package org.liara.api.recognition.sensor.common.virtual.presence;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.liara.api.data.entity.ApplicationEntityReference;
import org.liara.api.data.entity.node.Node;
import org.liara.api.recognition.sensor.SensorConfiguration;
import org.liara.api.validation.ValidApplicationEntityReference;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonSetter;

public class PresenceSensorConfiguration implements SensorConfiguration
{
  @NonNull
  private final Set<ApplicationEntityReference<Node>> _potentiallyOutdoorNodes;
  
  @NonNull
  private ApplicationEntityReference<Node> _outdoorNode;
  
  public PresenceSensorConfiguration () {
    _potentiallyOutdoorNodes = new HashSet<>();
    _outdoorNode = ApplicationEntityReference.empty(Node.class);
  }
  
  public PresenceSensorConfiguration (@NonNull final PresenceSensorConfiguration toCopy) {
    _potentiallyOutdoorNodes = new HashSet<>(toCopy.getPotentiallyOutdoorNodes());
    _outdoorNode = toCopy.getOutdoorNode();
  }
  
  public void setPotentiallyOutdoorNodes (@NonNull final Set<Long> nodes) {
    _potentiallyOutdoorNodes.clear();
    _potentiallyOutdoorNodes.addAll(ApplicationEntityReference.of(Node.class, nodes));
  }
  
  @ValidApplicationEntityReference
  public Set<ApplicationEntityReference<Node>> getPotentiallyOutdoorNodes () {
    return Collections.unmodifiableSet(_potentiallyOutdoorNodes);
  }
  
  public Iterable<ApplicationEntityReference<Node>> potentiallyOutdoorNodes () {
    return Collections.unmodifiableSet(_potentiallyOutdoorNodes);
  }
  
  public boolean isPotentiallyOutdoorNode (@NonNull final Long node) {
    return _potentiallyOutdoorNodes.contains(ApplicationEntityReference.of(Node.class, node));
  }
  
  public boolean isPotentiallyOutdoorNode (@NonNull final Node node) {
    return _potentiallyOutdoorNodes.contains(ApplicationEntityReference.of(node));
  }
  
  public void setPotentiallyOutdoorNodes (@NonNull final Iterable<Long> nodes) {
    _potentiallyOutdoorNodes.clear();
    _potentiallyOutdoorNodes.addAll(ApplicationEntityReference.of(Node.class, nodes));
  }
  
  @JsonSetter
  public void setPotentiallyOutdoorNodes (@NonNull final Collection<Long> nodes) {
    _potentiallyOutdoorNodes.clear();
    _potentiallyOutdoorNodes.addAll(ApplicationEntityReference.of(Node.class, nodes));
  }
  
  @ValidApplicationEntityReference
  public ApplicationEntityReference<Node> getOutdoorNode () {
    return _outdoorNode;
  }
  
  @JsonSetter
  public void setOutdoorNode (@Nullable final Long identifier) {
    _outdoorNode = ApplicationEntityReference.of(Node.class, identifier);
  }
  
  public void setOutdoorNode (@Nullable final Node node) {
    _outdoorNode = (node == null) ? ApplicationEntityReference.empty(Node.class)
                                  : ApplicationEntityReference.of(node);
  }

  @Override
  public PresenceSensorConfiguration clone () {
    return new PresenceSensorConfiguration(this);
  }
}
