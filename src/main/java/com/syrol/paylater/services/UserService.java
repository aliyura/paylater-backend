package com.syrol.paylater.services;
import com.syrol.paylater.entities.ActivityLog;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.enums.AccountType;
import com.syrol.paylater.pojos.*;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.zoho.ZohoContact;
import com.syrol.paylater.pojos.zoho.ZohoContactRequest;
import com.syrol.paylater.repositories.UserRepository;
import com.syrol.paylater.util.App;
import com.syrol.paylater.security.JwtUtil;
import com.syrol.paylater.util.AuthDetails;
import com.syrol.paylater.util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService implements Serializable {

    private final  App app;
    private final Response response;
    private final AuthDetails authDetails;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessagingService messagingService;
    private  final ZohoContactService zohoContactService;
    private  final  ActivityLogService logService;
    private final AuthenticationManager auth;
    private final JwtUtil jwtUtil;

    public APIResponse signUp(User user, AccountType accountType) {
        if (user.getName() == null)
            return response.failure("User Fullname Required <name>!");
        else if (user.getEmail() == null)
            return response.failure("Email Address Required <email>!");
        else if (!app.validEmail(user.getEmail()))
            return response.failure("Invalid Email Address <email>!");
        else if (user.getMobile()==null)
            return response.failure("Mobile Number Required <mobile>!");
        else if (!app.validNumber(user.getMobile()))
            return response.failure("Invalid Mobile Number <mobile>!");
        else if (user.getBvn()==null)
            return response.failure("BVN Required <bvn>!");
        else if (!app.validateBVN(user.getBvn()))
            return response.failure("Invalid BVN <bvn>!");
        else if (user.getDob()==null)
            return response.failure("Date of Birth Required <dob>!");
        else if (user.getGender()==null)
            return response.failure("Gender Required <gender>!");
        else if (user.getPassword() == null)
            return response.failure("User Password Required <password>!");

        else {

            User userByEmail = userRepository.findByEmail(user.getEmail()).orElse(null);
            User userByMobile = userRepository.findByMobile(user.getMobile()).orElse(null);
            User userByBvn = userRepository.findByBvn(user.getBvn()).orElse(null);

            if (userByEmail != null)
                return response.failure("Account already exist with the Email address provided!");
            else if (userByMobile != null)
                return response.failure("Account already exist with the Mobile number provided!");
            else if (userByBvn != null)
                return response.failure("Account already exist with the BVN provided!");
            else {

                if(user.getMobile().startsWith("0")){
                   user.setMobile(user.getMobile().replaceFirst("0","+234"));
                }
                user.setStatus(Status.PV);
                user.setAccountType(accountType);
                user.setCreatedDate(new Date());
                user.setLastLoginDate(new Date());
                user.setUuid(app.generateRandomId());
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setAccountNumber("0456789");
                user.setReferred(0l);

                if(user.getReferralCode()!=null) {
                    User referee = userRepository.findByUuid(user.getReferralCode()).orElse(null);
                    if(referee!=null) {
                        referee.setReferred(referee.getReferred() + 1);
                        referee.setLastModifiedDate(new Date());
                        userRepository.save(referee);
                    }else{
                        return  response.failure("Invalid Referral Code");
                    }
                }


                ZohoContactRequest contact = new ZohoContactRequest();
                contact.setContact_name(user.getName());
                contact.setPhone(user.getMobile());
                contact.setEmail(user.getEmail());
                contact.setCompany_name("Paylater");
                app.print("Creating new Zoho Contact ");
                app.print(contact);
                APIResponse<ZohoContact> contactResponse=zohoContactService.createContact(contact);

                if(contactResponse.isSuccess()) {
                    app.print("User created on Zoho");
                    app.print(contactResponse.getPayload());
                    user.setContactId(contactResponse.getPayload().getContact_id());
                    User savedUser = userRepository.save(user);
                    if (savedUser != null) {
                        UserRequest request = new UserRequest();
                        request.setUsername(user.getEmail() != null ? user.getEmail() : user.getMobile());



                        app.print("OTP Sending...");
                        APIResponse SMSResponse = messagingService.generateAndSendOTP(request);
                        app.print("OTP response...");
                        app.print(SMSResponse);
                        return response.success(savedUser);
                    } else {
                        return response.failure("Unable to create Account!");
                    }
                }else{
                    return response.failure(contactResponse.getMessage());
                }
            }
        }
    }

    public APIResponse signIn(UserCredRequest loginRequest) {

        app.print(loginRequest);
        try {
            if(loginRequest.getUsername().startsWith("0") && app.validNumber(loginRequest.getUsername())){
                loginRequest.setUsername(loginRequest.getUsername().replaceFirst("0","+234"));
            }
            Authentication authentication = auth.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            app.log(authentication.toString());
            if (authentication.isAuthenticated()) {
                User user = userRepository.findByEmail(loginRequest.getUsername()).orElse(
                        userRepository.findByMobile(loginRequest.getUsername()).orElse(null)
                );
                if (user != null) {
                    if (user.getStatus() == Status.AC) {
                        user.setLastLoginDate(new Date());
                        userRepository.save(user);
                        LoginResponse loginResponse =new LoginResponse();
                        user.setPassword("*****************");
                        loginResponse.setUser(user);
                        loginResponse.setAccessToken(
                        "Bearer " + jwtUtil.generateToken(new org.springframework.security.core.userdetails.User(
                                loginRequest.getUsername(), loginRequest.getPassword(), new ArrayList<>())
                        ));
                        return response.success(loginResponse);

                    } else {
                        if (user.getStatus() == Status.PV)
                            return response.failure("Your Account needs to be Activated");
                        else if (user.getStatus() == Status.IA)
                            return response.failure("Your Account is InActive or Not Approved Yet");
                        else
                            return response.failure("Your Account is not Active");
                    }
                } else {
                    return response.failure("Invalid Login Credentials");
                }
            } else {
                return response.failure("Invalid Username or Password");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return response.failure("Invalid Username or Password");
        }
    }
    public APIResponse logout(){
       return  response.success("success");
    }

    public APIResponse verifyUser(UserVerificationRequest request) {
        if(request.getUsername().startsWith("0") && app.validNumber(request.getUsername())){
            request.setUsername(request.getUsername().replaceFirst("0","+234"));
        }
        User user = userRepository.findByMobile(request.getUsername()).orElse(
                userRepository.findByEmail(request.getUsername()).orElse(null)
        );
        if (user != null) {
            app.print(user);
            app.print(user.getCode());
            app.print(request);
            if (user.getCode().equals(request.getOtp())) {
                user.setLastModifiedDate(new Date());
                user.setCountry(null);
                user.setStatus(Status.AC);
                messagingService.sendSMS(user.getMobile(),"Welcome onboard "+user.getName()+"");
                return response.success(userRepository.save(user));
            } else {
                return response.failure("Invalid OTP");
            }
        } else {
            return response.failure("Invalid Username or Password");
        }
    }

    public APIResponse initiatePasswordReset(UserRequest request) {

        if(request.getUsername().startsWith("0") && app.validNumber(request.getUsername())){
            request.setUsername(request.getUsername().replaceFirst("0","+234"));
        }
            User user = userRepository.findByEmail(request.getUsername()).orElse(
                    userRepository.findByMobile(request.getUsername()).orElse(null)
            );
            if (user != null) {
                app.print("OTP Sending...");
                UserRequest userRequest=new UserRequest();
                userRequest.setUsername(request.getUsername());
                APIResponse SMSResponse = messagingService.generateAndSendOTP(userRequest);
                app.print("OTP response...");
                app.print(SMSResponse);
                return response.success("Password change OTP sent Successfully");

            } else {
                return response.failure("Invalid Username or Password");
            }
    }


    public APIResponse resetPassword(PasswordChangeRequest request) {
        if(request.getUsername().startsWith("0") && app.validNumber(request.getUsername())){
            request.setUsername(request.getUsername().replaceFirst("0","+234"));
        }
        User user = userRepository.findByEmail(request.getUsername()).orElse(
                userRepository.findByMobile(request.getUsername()).orElse(null)
        );
        if (user != null) {
            app.print(user.getCode());
            app.print(Long.valueOf(request.getOtp()));
            if(user.getCode().equals(request.getOtp())) {
                user.setLastModifiedDate(new Date());
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                return response.success(userRepository.save(user));
            }else{
                return response.failure("Invalid OTP");
            }
        } else {
            return response.failure("Invalid Username or Password");
        }
    }

    public APIResponse updateUserProfileById(Principal principal, User newDetails) {
        User user=authDetails.getAuthorizedUser(principal);
        if (user != null) {
            if (newDetails.getName() != null)
                user.setName(newDetails.getName());
            if (newDetails.getCountry() != null)
                user.setCountry(newDetails.getCountry());
            if (newDetails.getCity() != null)
                user.setCity(newDetails.getCity());
            if (newDetails.getAddress() != null)
                user.setAddress(newDetails.getAddress());
            if (newDetails.getBankAccountName() != null)
                user.setBankAccountName(newDetails.getBankAccountName());
            if (newDetails.getBankAccountNumber() != null)
                user.setBankAccountNumber(newDetails.getBankAccountNumber());
            if (newDetails.getBankName() != null)
                user.setBankName(newDetails.getBankName());
            if (newDetails.getMaritalStatus() != null)
                user.setMaritalStatus(newDetails.getMaritalStatus());
            if (newDetails.getSourceOfIncome()!= null)
                user.setSourceOfIncome(newDetails.getSourceOfIncome());
            if (newDetails.getMonthlyIncome()!= null)
                user.setMonthlyIncome(newDetails.getMonthlyIncome());
            if (newDetails.getEmployerName()!= null)
                user.setEmployerName(newDetails.getEmployerName());
            if (newDetails.getEmployerAddress()!= null)
                user.setEmployerAddress(newDetails.getEmployerAddress());
            if (newDetails.getEmployerTelephone()!= null)
                user.setEmployerTelephone(newDetails.getEmployerTelephone());


            if (newDetails.getMobile() != null) {
                if(app.validNumber(newDetails.getMobile())) {
                    User appUser=userRepository.findByMobile(newDetails.getMobile()).orElse(null);
                    if(appUser==null || appUser.getId()==user.getId()) {
                        user.setMobile(newDetails.getMobile());
                    }
                }
            }
            if (newDetails.getEmail() != null) {
                if(app.validEmail(newDetails.getEmail())) {
                    User appUser=userRepository.findByEmail(newDetails.getEmail()).orElse(null);
                    if(appUser==null || appUser.getId()==user.getId()) {
                        user.setEmail(newDetails.getEmail());
                    }
                }
            }
            User savedDetails=  userRepository.save(user);
            try {
                //activity logging
                ActivityLog activityLog = new ActivityLog();
                activityLog.setDescription("Profile Update");
                activityLog.setRequestObject(app.getMapper().writeValueAsString(newDetails));
                activityLog.setResponseObject(app.getMapper().writeValueAsString(savedDetails));
                logService.log(principal,activityLog);
            }catch ( Exception ex){
                ex.printStackTrace();
            }
           return response.success(savedDetails);
        } else {
          return  response.failure("Account not found");
        }
    }

    public APIResponse updateUserStatusById(Long userId, Status status) {
        User user=userRepository.findById(userId).orElse(null);
        if (user != null) {
                user.setStatus(status);
                user.setLastModifiedDate(new Date());
            return   response.success(userRepository.save(user));
        }else{
          return  response.failure("Account not found");
        }
    }


    public APIResponse findUsersByAccountType(Pageable pageable,String  type){
        AccountType requestedType=null;
        try {
            requestedType=AccountType.valueOf(type.toUpperCase());
        }catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }
        if(requestedType!=null) {
            Page<User> userList = userRepository.findAllByAccountType(pageable, requestedType);
            if (userList!=null)
                return response.success(userList);
            else
                return response.failure("No  User found");
        }else{
            return  response.failure("No ("+type+") found as an Account type");
        }
    }
    public APIResponse findUsersByStatus(Pageable pageable,Status status) {
        Page<User> userList = userRepository.findByStatus(pageable, status);
        if (userList != null)
            return response.success(userList);
        else
            return response.failure("No  User found");
    }

    public APIResponse findUserByUiid(String  uiid) {
        User user = userRepository.findByUuid(uiid).orElse(null);
        if (user != null)
            return response.success(user);
        else
            return response.failure("User not found");
    }

    public APIResponse deleteUerById(Long userId){
        User user = userRepository.findById(userId).orElse(null);
        if(user!=null)
          userRepository.deleteById(userId);
        return response.success(user);
    }


    public APIResponse findAllUsersReferred(Pageable page,String uuid) {
        Page<User> users =userRepository.findByReferralCode(page, uuid);
        if (users != null) {
            return   response.success(users);
        }else{
            return  response.failure("No User Referred");
        }
    }

    public APIResponse findAll(Pageable page) {
        Page<User> users =userRepository.findAll(page);
        if (users != null) {
            return   response.success(users);
        }else{
            return  response.failure("No User Available");
        }
    }
}