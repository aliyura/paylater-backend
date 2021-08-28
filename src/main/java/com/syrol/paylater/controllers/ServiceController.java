
package com.syrol.paylater.controllers;
import com.syrol.paylater.entities.PLService;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.services.ServiceService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class ServiceController {

    @Autowired
    private final ServiceService serviceService;

    @PostMapping("/service/add")
    public APIResponse<PLService> addNewService(@RequestBody PLService service, Principal principal) {
        return serviceService.createService(principal, service);
    }

    @PutMapping("/service/update")
    public APIResponse<PLService> updateService(@RequestBody PLService service, Principal principal) {
        return serviceService.updateService(principal, service);
    }

    @DeleteMapping("/service/delete/{id}")
    public APIResponse<PLService> updateService(@PathVariable Long id) {
        return serviceService.deleteBySuid(id);
    }

    @GetMapping("/service/get_by_id/{id}")
    public APIResponse<PLService> findServices(@PathVariable Long id) {
        return serviceService.findById(id);
    }

    @GetMapping("/service/get_by_suid/{suid}")
    public APIResponse<List<PLService>> findServicesBySuid(@PathVariable String suid) {
        return serviceService.findBySuid(suid);
    }

    @GetMapping("/services/get_by_status")
    public APIResponse<List<PLService>> findServicesByStatus(@RequestParam Status status) {
        return serviceService.findByStatus(status);
    }

    @GetMapping("/services/get_all")
    public APIResponse<List<PLService>> findServices() {
        return serviceService.findAll();
    }
}
