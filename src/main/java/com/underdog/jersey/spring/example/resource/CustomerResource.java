package com.underdog.jersey.spring.example.resource;

import com.underdog.jersey.spring.example.domain.Customer;
import com.underdog.jersey.spring.example.service.CustomerService;
import java.net.URI;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Paul Samsotha
 */
@Path("contacts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Autowired
    private CustomerService customerService;

    /**
     * /api/contacts/
     */
    @GET
    public Response getAllContacts(
    		@QueryParam("firstName") String firstName,
            @QueryParam("lastName") String lastName) {

        List<Customer> customers;
        if (firstName != null && lastName != null) {
            customers = customerService.findByFirstAndLastName(firstName, lastName);
        } else {
            customers = customerService.findAll();
        }
        return Response.ok(new GenericEntity<List<Customer>>(customers) {
        }).build();
    }

    /**
     *  1. /api/contacts/get?name=value
     *  
     *  Delete by first name only
     */
    @GET
    @Path("/get")
    public Response getContact(@QueryParam("name") String firstName) {
        Customer customer = customerService.findOneByFirstName(firstName);
        if (customer == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return Response.ok(customer).build();
    }

    /**
     *  2. /api/contacts/delete?name=value
     */
    @DELETE
    @Path("/delete")
    public Response deleteContact(@QueryParam("name") String firstName) {
        Customer inDb = customerService.findOneByFirstName(firstName);
        if (inDb == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        customerService.delete(inDb);
        return Response.ok().build();
    }
    
    /**
     *  3. /api/contacts/put?name=value&phone=value
     *  
     *  Last name updating is optional
     */
    @PUT
    @Path("/put")
    public Response updateContact(
    		@QueryParam("name") String firstName,
    		@QueryParam("lname") String lastName,
            @QueryParam("phone") String phoneNumber,
            @Context UriInfo uriInfo) {
        Customer inDb = customerService.findOneByFirstName(firstName);
        if (inDb == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        // Update data
        inDb.setLastName(lastName);
        inDb.setPhoneNumber(phoneNumber);
        customerService.update(inDb);
        return Response.noContent().build();
    }

    /**
     *  4. /api/contacts/post?name=value&phone=value
     *  
     */
    @POST
    @Path("/post")
    public Response createContact(
    		@QueryParam("name") String firstName,
    		@QueryParam("lname") String lastName,
            @QueryParam("phone") String phoneNumber,
            @Context UriInfo uriInfo) {
    	
    	// Create the customer instance
    	Customer customer = new Customer();
    	customer.setFirstName(firstName);
    	customer.setLastName(lastName);
    	customer.setPhoneNumber(phoneNumber);
    	
        customer = customerService.save(customer);
        long id = customer.getId();

        URI createdUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build();
        return Response.created(createdUri).build();
    }
    
    /**
     *  5. /api/contacts/reset
     *  
     */
    @POST
    @Path("/reset")
    public Response deleteAllContacts() {
    	customerService.deleteAll();
        return Response.ok().build();
    }
}
