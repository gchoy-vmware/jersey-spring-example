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
import org.springframework.util.StringUtils;
import org.json.*;

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
    @Path("/all")
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
     *  1. GET /api/contacts?name="Deadbeef"
     *  
     *  Get by first name only
     */
    @GET
    public Response getContact(@QueryParam("name") String firstName) {
        List<Customer> customerList = customerService.findOneByFirstName(firstName);
        if (customerList == null || customerList.isEmpty())
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        return Response.ok(customerList.get(0)).build();
    }

    /**
     *  2. DELETE /api/contacts/delete?name=value
     */
    @DELETE
    @Path("/delete")
    public Response deleteContact(@QueryParam("firstName") String firstName) {
        List<Customer> customerList = customerService.findOneByFirstName(firstName);
        if (customerList == null || customerList.isEmpty()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        customerService.delete(customerList.get(0));
        return Response.ok().build();
    }
        
    /**
     *  3. PUT /api/contacts
     *  { "oldName": "Geoff", "newName": "Foobar", "phone": "000" }
     *  
     */
    @PUT
    public Response updateContactBody(
    		String body,
            @Context UriInfo uriInfo) {
    	String oldFirstName = null;
    	String newFirstName = null;
    	String newPhoneNumber = null;

    	try {
        	JSONObject contactJson = new JSONObject(body);
        	oldFirstName = contactJson.getString("oldName");
        	newFirstName = contactJson.getString("newName");
        	newPhoneNumber = contactJson.getString("phone");    		
    	} catch (JSONException ex) {
    		throw new WebApplicationException(Response.Status.BAD_REQUEST);
    	}

    	if(StringUtils.isEmpty(oldFirstName))
    		throw new WebApplicationException(Response.Status.NOT_FOUND);
    	
        List<Customer> customerList = customerService.findOneByFirstName(oldFirstName);
        if (customerList == null || customerList.isEmpty()) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        Customer updatingContact = customerList.get(0);
        
        // Update data
        if(!StringUtils.isEmpty(newFirstName))
        	updatingContact.setFirstName(newFirstName);
        if(!StringUtils.isEmpty(newPhoneNumber))
        	updatingContact.setPhoneNumber(newPhoneNumber);
        customerService.update(updatingContact);
        return Response.noContent().build();
    }

    
    /**
     *  4. POST /api/contacts
     *  { "firstName": "Geoff", "lastName": "Choy" }
     *  
     */
    @POST
    public Response createContactBody(
    		Customer newCustomer,
            @Context UriInfo uriInfo) {
        Customer customer = customerService.save(newCustomer);
        URI createdUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(customer.getId())).build();
        return Response.created(createdUri).build();
    }
    
    /**
     *  5. /api/contacts/reset
     *  
     */
    @POST
    @Path("/reset")
    public Response resetContacts() {
    	customerService.deleteAll();
        return Response.ok().build();
    }

    @DELETE
    public Response deleteContacts(String body , @Context UriInfo uriInfo) {
    	String contactToDelete = null;
    	
    	// Check valid payload 
    	try {
        	JSONObject contactJson = new JSONObject(body);
    		contactToDelete = contactJson.getString("firstName");
    	} catch (JSONException ex) {
    		return Response.notModified().build();
    	}
    	
    	// Check either to delete all contacts or single contact
    	if(contactToDelete.equals("*")) {
    		customerService.deleteAll();
    	} else {
          List<Customer> contactList = customerService.findOneByFirstName(contactToDelete);
          if (contactList == null || contactList.isEmpty()) {
              throw new WebApplicationException(Response.Status.NOT_FOUND);
          }
          customerService.delete(contactList.get(0));
    	}
        return Response.ok().build();
    }

    /**
     *  Bulk create contacts
     */
    @POST
    @Path("/createbulk")
    public Response createBulkContacts(List<Customer> customerList, @Context UriInfo uriInfo) {
    	
    	customerService.saveAll(customerList);
        URI createdUri = uriInfo.getAbsolutePathBuilder().path(Long.toString(1)).build();
        return Response.created(createdUri).build();
    }
}
