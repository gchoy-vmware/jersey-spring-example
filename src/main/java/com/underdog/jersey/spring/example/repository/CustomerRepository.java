/**
 *
 * @author Paul Samsotha
 */

package com.underdog.jersey.spring.example.repository;

import com.underdog.jersey.spring.example.domain.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * The JpaRepository<Customer, Long> === <T, ID>
 * @author gchoy
 *
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	List<Customer> findByFirstNameAndLastName(String firstName, String lastName);
	Customer findOneByFirstName(String firstName);
}
