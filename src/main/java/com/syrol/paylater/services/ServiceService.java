package com.syrol.paylater.services;
import com.syrol.paylater.entities.PLService;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.*;
import com.syrol.paylater.repositories.ServiceRepository;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.AuthDetails;
import com.syrol.paylater.util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService implements Serializable {

    private final  App app;
    private final Response response;
    private final AuthDetails authDetails;
    private final ServiceRepository serviceRepository;


    public APIResponse createService(Principal principal, PLService plService){

        User user= authDetails.getAuthorizedUser(principal);
        if (plService.getTitle() == null)
            return response.failure("Service Title Required <title>!");
        else if (plService.getDescription() == null)
            return response.failure("Service Description Required <required>!");
        else {

            app.print("Add Service Request:");
            app.print(plService);
            PLService existingService=serviceRepository.findByTitle(plService.getTitle()).orElse(null);
            if(existingService==null){
                plService.setCreatedDate(new Date());
                plService.setLastModifiedDate(new Date());
                plService.setStatus(Status.AC);
                plService.setRatings(0l);
                plService.setCreatedBy(user.getUuid());
                plService.setCreatedByUsername(user.getEmail());
                plService.setSuid(app.generateRandomId());
                return response.success(  serviceRepository.save(plService));
            }else{
                return  response.failure("Service ["+plService.getTitle()+"] already exist");
            }
        }
    }

    public APIResponse updateService(Principal principal, PLService newService) {
        User user = authDetails.getAuthorizedUser(principal);
        PLService service = serviceRepository.findBySuid(newService.getSuid()).orElse(null);
        if (service != null) {

            if (newService.getTitle() != null)
                  service.setTitle(newService.getTitle());
            else if (newService.getDescription() != null)
                  service.setDescription(newService.getDescription());
            else if (newService.getStatus() != null)
                service.setStatus(newService.getStatus());
            else if (newService.getDisplayImageUrl() != null)
                service.setDisplayImageUrl(newService.getDisplayImageUrl());
            else if (newService.getMax() != null)
                service.setMax(newService.getMax());
            else if (newService.getMin() != null)
                service.setMin(newService.getMin());

            service.setLastModifiedBy(user.getUuid());
            service.setLastModifiedDate(new Date());
            return response.success(serviceRepository.save(service));

        } else {
            return response.failure("Invalid Service Id");
        }
    }

    public APIResponse deleteById(Long id) {
        serviceRepository.deleteById(id);
        return response.success("Deleted Successfully");
    }

    public APIResponse deleteBySuid(Long id) {
        try {
            serviceRepository.deleteById(id);
            return response.success("Deleted Successfully");
        } catch (Exception ex) {
            return response.failure("Invalid Service Id");
        }
    }

    public APIResponse findById(Long id) {
        PLService service = serviceRepository.findById(id).orElse(null);
        if (service!=null)
            return response.success(service);
        else
            return response.failure("Service not Found");
    }

    public APIResponse findBySuid(String  suid) {
        PLService service = serviceRepository.findBySuid(suid).orElse(null);
        if (service != null)
            return response.success(service);
        else
            return response.failure("Service not found");
    }

    public APIResponse findByStatus(Status status) {
        List<PLService> service = serviceRepository.findByStatus(status).orElse(null);
        if (!service.isEmpty())
            return response.success(service);
        else
            return response.failure("No Service Found");
    }

    public APIResponse findAll() {
        List<PLService> service = serviceRepository.findAll();
        if (!service.isEmpty())
            return response.success(service);
        else
            return response.failure("No Service Available");
    }


}