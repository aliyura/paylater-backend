package com.syrol.paylater.services;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.enums.AccountType;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.LoginResponse;
import com.syrol.paylater.pojos.UserRequest;
import com.syrol.paylater.pojos.UserVerificationRequest;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements Serializable {

    private final  App app;
    private final Response response;
    private final AuthDetails authDetails;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessagingService messagingService;
    private final AuthenticationManager auth;
    private final JwtUtil jwtUtil;


    public APIResponse signUp(User user) {
        User userByEmail = userRepository.findByEmail(user.getEmail()).orElse(null);
        User userByMobile = userRepository.findByMobile(user.getMobile()).orElse(null);

        if (user.getName() == null)
            return response.failure("User Fullname Required");
        else if (user.getEmail() == null)
            return response.failure("Email Address Required");
        else if (user.getPassword() == null)
            return response.failure("User Password Required");
        else if (userByEmail != null)
            return response.failure("Account already exist!");
        else if (userByMobile != null)
            return response.failure("Account already exist!");
        else if (!app.validEmail(user.getEmail()))
            return response.failure("Invalid Email Address!");
        else if (!app.validNumber(user.getMobile()))
            return response.failure("Invalid Mobile Number!");
        else {
            app.print(user);

            user.setStatus(Status.PV);
            user.setCreatedDate(new Date());
            user.setLastLoginDate(new Date());
            user.setUiid(app.makeUIID());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            if (savedUser != null) {
                UserRequest request = new UserRequest();
                request.setUsername(user.getMobile()!=null?user.getMobile():user.getEmail());
                app.print("OTP Sending...");
                APIResponse SMSResponse= messagingService.generateAndSendOTP(request);
                app.print("OTP response...");
                app.print(SMSResponse);
                return response.success(savedUser);
            }
            else{
                return response.failure("Unable to create Account!");
        }
        }
    }

    public APIResponse signIn(UserRequest loginRequest) {

        app.print(loginRequest);
        try {
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
                        loginResponse.setBearer(
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
        User user = userRepository.findByMobile(request.getUsername()).orElse(
                userRepository.findByEmail(request.getUsername()).orElse(null)
        );
        if (user != null) {
            app.print(user.getCode());
            app.print(request);
            app.print(user.getCode().equals(request.getOtp()));
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
            return response.failure("Account not found");
        }
    }

    public APIResponse resetPassword(UserRequest request) {
        User user = userRepository.findByEmail(request.getUsername()).orElse(
                userRepository.findByMobile(request.getUsername()).orElse(null)
        );
        if (user != null) {
            user.setLastModifiedDate(new Date());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            return response.success(userRepository.save(user));

        } else {
            return response.failure("Account not found");
        }
    }

    public APIResponse updateUserProfileById(Principal principal, User newDetails) {
        User user=authDetails.getAuthorizedUser(principal);
        if (user != null) {
            if (newDetails.getName() != null)
                user.setName(newDetails.getName());
            if (newDetails.getAccountType() != null)
                user.setAccountType(newDetails.getAccountType());
            if (newDetails.getCountry() != null)
                user.setCountry(newDetails.getCountry());
            if (newDetails.getCity() != null)
                user.setCity(newDetails.getCity());
            if (newDetails.getAddress() != null)
                user.setAddress(newDetails.getAddress());

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
           return response.success(userRepository.save(user));
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
            Page<List<User>> userList = userRepository.findAllByAccountType(pageable, requestedType);
            if (!userList.isEmpty())
                return response.success(userList);
            else
                return response.failure("No  Account found from " + type.toLowerCase() + "");
        }else{
            return  response.failure("No ("+type+") found as an Account type");
        }
    }

    public APIResponse findUserByUiid(String  uiid) {
        User user = userRepository.findByUiid(uiid).orElse(null);
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
}