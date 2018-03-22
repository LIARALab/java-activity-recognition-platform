package org.domus.api.data.repository;

import java.lang.Integer;

import org.springframework.data.repository.CrudRepository;

import org.domus.api.data.entity.IntegerState;

public interface IntegerStateRepository
       extends CrudRepository<IntegerState, Integer>
{ }
