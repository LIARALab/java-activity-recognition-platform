package org.domus.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.lang.NonNull;

import org.springframework.web.bind.annotation.RequestParam;

import org.domus.api.data.repository.SensorRepository;
import org.domus.api.data.entity.Sensor;

@RestController
public final class SensorCollectionController {
  @Autowired
  private SensorRepository sensorRepository;

  @GetMapping("/sensors")
  public List<Sensor> index (@NonNull final HttpServletRequest request) {
    return Lists.newArrayList(this.sensorRepository.findAll());
  }

  @GetMapping("/sensors/{identifier}")
  public Sensor get (@PathVariable final int identifier) {
    return this.sensorRepository.findById(identifier).get();
  }
}
