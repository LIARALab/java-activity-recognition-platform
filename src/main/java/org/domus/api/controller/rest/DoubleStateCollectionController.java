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

import org.domus.api.data.repository.DoubleStateRepository;
import org.domus.api.data.entity.DoubleState;

@RestController
public final class DoubleStateCollectionController {
  @Autowired
  private DoubleStateRepository doubleStateRepository;

  @GetMapping("/states/doubles")
  public List<DoubleState> index (@NonNull final HttpServletRequest request) {
    return Lists.newArrayList(this.doubleStateRepository.findAll());
  }

  @GetMapping("/states/doubles/{identifier}")
  public DoubleState get (@PathVariable final int identifier) {
    return this.doubleStateRepository.findById(identifier).get();
  }
}
