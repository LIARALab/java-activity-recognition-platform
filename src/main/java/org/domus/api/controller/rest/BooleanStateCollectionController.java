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

import org.domus.api.data.repository.BooleanStateRepository;
import org.domus.api.data.entity.BooleanState;

@RestController
public final class BooleanStateCollectionController {
  @Autowired
  private BooleanStateRepository booleanStateRepository;

  @GetMapping("/states/booleans")
  public List<BooleanState> index (@NonNull final HttpServletRequest request) {
    return Lists.newArrayList(this.booleanStateRepository.findAll());
  }

  @GetMapping("/states/booleans/{identifier}")
  public BooleanState get (@PathVariable final int identifier) {
    return this.booleanStateRepository.findById(identifier).get();
  }
}
