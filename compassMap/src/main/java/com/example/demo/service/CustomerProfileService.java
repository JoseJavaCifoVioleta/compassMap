package com.example.demo.service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.model.CustomerProfile;
import com.example.demo.model.RoadMap;
import com.example.demo.repository.CustomerProfileRepository;
import com.example.demo.repository.RoadMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerProfileService {

    // we are using here DEPENDENCE INJECTION
    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private RoadMapRepository roadMapRepository;

    public Iterable<CustomerProfile> getAllCustomerProfiles() {
        return customerProfileRepository.findAll();
    }

    public CustomerProfile createCustomerProfile(CustomerProfile customerProfile) {
        // SET ID customerProfile to UUID (id is not sent from client)
        customerProfile.setId(UUID.randomUUID().toString());

        // call repository to use SAVE operation to save data to h2 db
        CustomerProfile createdCustomerProfile = customerProfileRepository.save(customerProfile);
        System.out.println("Service created customerProfile: " + createdCustomerProfile);
        return createdCustomerProfile;
    }

    public void deleteCustomerProfile(String id) {
        customerProfileRepository.deleteById(id);
    }

    public CustomerProfile getCustomerProfileById(String id) {

        Optional<CustomerProfile> foundCustomerProfile = customerProfileRepository.findById(id);

        return foundCustomerProfile.orElse(null);
    }

    public CustomerProfile getCustomerProfileByCustomerId(String customerId) {
        return customerProfileRepository.findByCustomerId(customerId).orElse(null);
    }

    public CustomerProfile saveCustomerProfile(CustomerProfile customerProfile) {
        return customerProfileRepository.save(customerProfile);
    }

    public Iterable<CustomerProfile> saveAll(Iterable<CustomerProfile> customerProfiles) {
        for (CustomerProfile customerProfile : customerProfiles) {
            // SET ID customerProfile to UUID (id is not sent from client)
            customerProfile.setId(UUID.randomUUID().toString());
        }
        return customerProfileRepository.saveAll(customerProfiles);
    }

    public void deleteAllCustomerProfiles() {
        customerProfileRepository.deleteAll();
    }

    public long countCustomerProfiles() {
        return customerProfileRepository.count();
    }

    public CustomerProfile updateCustomerProfile(CustomerProfile incoming) {
        if (incoming.getId() == null) {
            return null;
        }
        CustomerProfile existing = getCustomerProfileById(incoming.getId());
        if (existing == null) {
            return null;
        }
        // merge only non-null fields from incoming (partial update semantics)
        if (incoming.getCodeProfile() != null) {
            existing.setCodeProfile(incoming.getCodeProfile());
        }
        if (incoming.getName() != null) {
            existing.setName(incoming.getName());
        }
        if (incoming.getDescription() != null) {
            existing.setDescription(incoming.getDescription());
        }
        if (incoming.getGoal() != null) {
            existing.setGoal(incoming.getGoal());
        }
        if (incoming.getProfileAnswers() != null) {
            existing.setProfileAnswers(incoming.getProfileAnswers());
        }
        if (incoming.getRecommendedRoadmaps() != null) {
            // roadmaps are shared associations, not created here: resolve each one by id
            List<RoadMap> resolvedRoadMaps = new ArrayList<>();
            for (RoadMap roadMap : incoming.getRecommendedRoadmaps()) {
                if (roadMap == null || roadMap.getId() == null) {
                    throw new IllegalArgumentException("Recommended roadmap requires an existing roadmap id");
                }
                resolvedRoadMaps.add(roadMapRepository.findById(roadMap.getId())
                        .orElseThrow(() -> new NotFoundException("RoadMap not found: " + roadMap.getId())));
            }
            existing.setRecommendedRoadmaps(resolvedRoadMaps);
        }
        return customerProfileRepository.save(existing);
    }
}
