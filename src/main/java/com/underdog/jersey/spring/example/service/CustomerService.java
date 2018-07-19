/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.underdog.jersey.spring.example.service;

import com.underdog.jersey.spring.example.domain.Customer;
import java.util.List;

/**
 *
 * @author PaulSamsotha
 */
public interface CustomerService {
    List<Customer> findAll();
    Customer findOne(Long id);
    List<Customer> findOneByFirstName(String fname);
    Customer save(Customer customer);
    Iterable<Customer> saveAll(Iterable<Customer> customerList);
    void update(Customer customer);
    void delete(Customer customer);
    void deleteAll();
    List<Customer> findByFirstAndLastName(String fname, String lname);
}
