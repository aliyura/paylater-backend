package com.syrol.paylater.services;
import com.syrol.paylater.entities.PLService;
import com.syrol.paylater.entities.ServiceRequest;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.enums.ServiceTenureType;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.repositories.ServiceRepository;
import com.syrol.paylater.repositories.ServiceRequestRepository;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.AuthDetails;
import com.syrol.paylater.util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceRequestService implements Serializable {

    private final  App app;
    private final Response response;
    private final AuthDetails authDetails;
    private final ServiceRequestRepository serviceRequestRepository;
    private  final ServiceRepository serviceRepository;


    public APIResponse createServiceRequest(Principal principal, ServiceRequest serviceRequest){
        User user= authDetails.getAuthorizedUser(principal);
        if (serviceRequest.getAmount() == null)
            return response.failure("Service Request Amount Required <amount>!");
        else if (serviceRequest.getTenure() == null)
            return response.failure("Repayment Tenure Required <tenure>!");
        else if (serviceRequest.getSuid() == null)
            return response.failure("Service Id Required <suid>!");
        else {
            List<ServiceRequest> existingServiceRequest= serviceRequestRepository.findBySuidAndUser(serviceRequest.getSuid(),user.getUuid()).orElse(null);
            if(existingServiceRequest==null){
                PLService plService = serviceRepository.findBySuid(serviceRequest.getSuid()).orElse(null);
                if(plService!=null) {
                    serviceRequest.setCreatedDate(new Date());
                    serviceRequest.setLastModifiedDate(new Date());
                    serviceRequest.setStatus(Status.PC);
                    serviceRequest.setUserEmail(user.getEmail());
                    serviceRequest.setUserName(user.getName());
                    serviceRequest.setUuid(user.getUuid());
                    serviceRequest.setServiceTitle(plService.getTitle());
                    serviceRequest.setSruid(app.generateRandomId());
                    return response.success(serviceRequestRepository.save(serviceRequest));
                }else{
                    return  response.failure("Service not Found!");
                }
            }else{
                return  response.failure("User ["+user.getName()+"] already applied for this service");
            }
        }
    }

    public APIResponse updateServiceRequestStatus(Principal principal, String sruid, Status status) {
        User user = authDetails.getAuthorizedUser(principal);
        ServiceRequest serviceRequest = serviceRequestRepository.findBySruid(sruid).orElse(null);
        if (serviceRequest != null) {
            serviceRequest.setStatus(status);
            serviceRequest.setLastModifiedBy(user.getUuid());
            serviceRequest.setLastModifiedDate(new Date());
            return response.success(serviceRequestRepository.save(serviceRequest));

        } else {
            return response.failure("Invalid Service Request Id");
        }
    }

    public APIResponse deleteById(Long id) {
        try {
            serviceRequestRepository.deleteById(id);
            return response.success("Deleted Successfully");
        }catch (Exception ex){
            return response.failure("Invalid Service Request Id");
        }
    }

    public APIResponse findById(Long id) {
        ServiceRequest serviceRequest = serviceRequestRepository.findById(id).orElse(null);
        if (serviceRequest!=null)
            return response.success(serviceRequest);
        else
            return response.failure("Service Request not Found");
    }

    public APIResponse findByUuid(String  uuid) {
        List<ServiceRequest> serviceRequest = serviceRequestRepository.findByUuid(uuid).orElse(null);
        if (serviceRequest!=null)
            return response.success(serviceRequest);
        else
            return response.failure("No Service Request Found");
    }

    public APIResponse findBySuid(String  suid) {
        List<ServiceRequest> serviceRequest = serviceRequestRepository.findBySuid(suid).orElse(null);
        if (!serviceRequest.isEmpty())
            return response.success(serviceRequest);
        else
            return response.failure("No Service Request Found");
    }

    public APIResponse findByStatus(Status status, Pageable pageable) {
        Page<ServiceRequest> serviceRequest = serviceRequestRepository.findByStatus(status,pageable).orElse(null);
        if (!serviceRequest.isEmpty())
            return response.success(serviceRequest);
        else
            return response.failure("No Service Request Found");
    }

    public APIResponse findByTenure(ServiceTenureType tenure, Pageable pageable) {
        Page<ServiceRequest> serviceRequest = serviceRequestRepository.findByTenure(tenure,pageable).orElse(null);
        if (!serviceRequest.isEmpty())
            return response.success(serviceRequest);
        else
            return response.failure("No Service Request Found");
    }

    public APIResponse findByAll(Pageable pageable) {
        Page<ServiceRequest> serviceRequest = serviceRequestRepository.findAll(pageable);
        if (!serviceRequest.isEmpty())
            return response.success(serviceRequest);
        else
            return response.failure("No Service Request Available");
    }
}