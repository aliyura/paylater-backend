package com.syrol.paylater.services;
import com.syrol.paylater.entities.ActivityLog;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.repositories.ActivityLogRepository;
import com.syrol.paylater.repositories.UserRepository;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ActivityLogService{

    private final com.syrol.paylater.util.Response apiResponse;
    private final ActivityLogRepository activityLogRepository;
    private final AuthDetails authDetails;

    public APIResponse log(Principal principal, ActivityLog log) {
        try {
            User user = authDetails.getAuthorizedUser(principal);
            log.setCreatedAt(new Date());
            log.setContactId(user.getContactId());
            log.setUserName(user.getName());
            log.setUuid(user.getUuid());
            log.setIpAddress("Not Detected");
            log.setDevice("Not Detected");
            return apiResponse.success(activityLogRepository.save(log));
        }catch ( Exception ex){
            ex.printStackTrace();
            return apiResponse.failure("Unable to save activity log");
        }
    }
}
