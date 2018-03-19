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

import org.domus.api.data.repository.IntegerStateRepository;
import org.domus.api.data.entity.IntegerState;

@RestController
public final class IntegerStateCollectionController {
  @Autowired
  private IntegerStateRepository integerStateRepository;

  @GetMapping("/states/integers")
  public List<IntegerState> index (@NonNull final HttpServletRequest request) {
    return Lists.newArrayList(this.integerStateRepository.findAll());
  }

  @GetMapping("/states/integers/{identifier}")
  public IntegerState get (@PathVariable final int identifier) {
    return this.integerStateRepository.findById(identifier).get();
  }
}
