package com.example.demo.controller;

import com.example.demo.model.CustomerProfile;
import com.example.demo.service.CustomerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// REST Controller is an annotation that marks the class as a controller
// that is a component that handles HTTP requests
@RestController
// RequestMapping is an annotation that maps HTTP requests to handler methods
// it is used to map the HTTP requests to the methods in the controller
@RequestMapping("/api/v1/customerprofiles")
public class CustomerProfileRestController {

    // Autowired is an annotation that marks the field as a dependency
    @Autowired
    private CustomerProfileService customerProfileService;

    //----------------------------- CREATE -----------------------------
    @PostMapping("/create")
    public CustomerProfile createCustomerProfile(@RequestBody CustomerProfile customerProfile) {
        CustomerProfile newCustomerProfile = customerProfileService.createCustomerProfile(customerProfile);
        System.out.println("Service createCustomerProfile: " + newCustomerProfile);
        return newCustomerProfile;
    }

    @PostMapping("/createAll")
    public Iterable<CustomerProfile> createAllCustomerProfiles(@RequestBody Iterable<CustomerProfile> customerProfiles) {
        Iterable<CustomerProfile> newCustomerProfiles = customerProfileService.saveAll(customerProfiles);
        System.out.println("Service createAllCustomerProfiles: " + customerProfiles);
        return newCustomerProfiles;
    }

    //----------------------------- UPDATE -----------------------------
    @PutMapping("/update")
    public CustomerProfile updateCustomerProfile(@RequestBody CustomerProfile customerProfile) {
        if (customerProfile.getId() == null || customerProfileService.getCustomerProfileById(customerProfile.getId()) == null) {
            return null;
        }
        CustomerProfile updatedCustomerProfile = customerProfileService.updateCustomerProfile(customerProfile);
        System.out.println("Service updateCustomerProfile: " + updatedCustomerProfile);
        return updatedCustomerProfile;
    }

    //----------------------------- GET -----------------------------
    @GetMapping("/all")
    public Iterable<CustomerProfile> getAllCustomerProfiles() {
        Iterable<CustomerProfile> customerProfiles = customerProfileService.getAllCustomerProfiles();
        System.out.println("Service getAllCustomerProfiles: " + customerProfiles);
        return customerProfiles;
    }

    @GetMapping("/byId")
    public CustomerProfile getCustomerProfileById(@RequestParam String id) {
        CustomerProfile customerProfile = customerProfileService.getCustomerProfileById(id);
        System.out.println("Service getCustomerProfileById: " + customerProfile);
        return customerProfile;
    }

    @GetMapping("/count")
    public long countCustomerProfiles() {
        long count = customerProfileService.countCustomerProfiles();
        System.out.println("Service countCustomerProfiles: " + count);
        return count;
    }

    //----------------------------- DELETE -----------------------------
    @DeleteMapping("/byId")
    public CustomerProfile deleteById(@RequestParam String id) {
        CustomerProfile foundCustomerProfile = customerProfileService.getCustomerProfileById(id);
        if (foundCustomerProfile == null) {
            return null;
        } else {
            customerProfileService.deleteCustomerProfile(id);
            System.out.println("Service deleteCustomerProfile byId: " + id);
            return foundCustomerProfile;
        }
    }

    @DeleteMapping("/all")
    public void deleteAllCustomerProfiles() {
        customerProfileService.deleteAllCustomerProfiles();
        System.out.println("Service deleteAllCustomerProfiles executed");
    }
}
