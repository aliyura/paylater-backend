package com.syrol.paylater.controllers;
import com.syrol.paylater.entities.ServiceRequest;
import com.syrol.paylater.enums.ServiceTenureType;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.services.ServiceRequestService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class ServiceRequestController {

    @Autowired
    private final ServiceRequestService serviceRequestService;

    @PostMapping("/service-request/add")
    public APIResponse<ServiceRequest> addNewService(@RequestBody ServiceRequest serviceRequest, Principal principal) {
        return serviceRequestService.createServiceRequest(principal, serviceRequest);
    }

    @PutMapping("/service-request/update_status/{sruid}")
    public APIResponse<ServiceRequest> updateService(@PathVariable String sruid,  @RequestParam Status status, Principal principal) {
        return serviceRequestService.updateServiceRequestStatus(principal, sruid, status);
    }

    @DeleteMapping("/service-request/delete/{id}")
    public APIResponse<ServiceRequest> updateService(@PathVariable Long id) {
        return serviceRequestService.deleteById(id);
    }

    @GetMapping("/service-request/find_by_id/{id}")
    public APIResponse<ServiceRequest> findService(@PathVariable Long id) {
        return serviceRequestService.findById(id);
    }

    @GetMapping("/service-request/find_by_uuid/{uuid}")
    public APIResponse<List<ServiceRequest>> findByUuid(@PathVariable String uuid) {
        return serviceRequestService.findByUuid(uuid);
    }

    @GetMapping("/service-request/find_by_suid/{suid}")
    public APIResponse<ServiceRequest> findBySuid(@PathVariable String suid) {
        return serviceRequestService.findBySuid(suid);
    }

    @GetMapping("/service-request/find_by_status")
    public APIResponse<Page<ServiceRequest>> findServicesByStatus(@RequestParam Status status, @RequestParam int page, @RequestParam int size) {
        return serviceRequestService.findByStatus(status, PageRequest.of(page,size));
    }
    @GetMapping("/service-request/find_by_tenure")
    public APIResponse<Page<ServiceRequest>> findByTenure(@RequestParam ServiceTenureType tenure, @RequestParam int page, @RequestParam int size) {
        return serviceRequestService.findByTenure(tenure, PageRequest.of(page,size));
    }

    @GetMapping("/service-request/find_all")
    public APIResponse<Page<ServiceRequest>> findServices(@RequestParam int page, @RequestParam int size) {
        return serviceRequestService.findByAll(PageRequest.of(page,size));
    }
}
