package com.example.demo.repository;

import com.example.demo.model.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



public interface CustomerRepository extends CrudRepository<Customer, String> {}