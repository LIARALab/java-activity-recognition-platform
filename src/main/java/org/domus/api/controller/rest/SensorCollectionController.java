package org.domus.api.controller;

import java.util.List;

import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestParam;

import org.domus.api.data.SensorRepository;
import org.domus.api.data.Sensor;

@RestController
public final class SensorCollectionController {
  @Autowired
  private SensorRepository sensorRepository;

  @RequestMapping("/sensors")
  public List<Sensor> index () {
    return Lists.newArrayList(this.sensorRepository.findAll());
  }
}
