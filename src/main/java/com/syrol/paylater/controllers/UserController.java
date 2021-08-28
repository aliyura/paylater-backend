package com.syrol.paylater.controllers;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.enums.AccountType;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.*;
import com.syrol.paylater.services.MessagingService;
import com.syrol.paylater.services.UserService;
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
public class UserController {

    @Autowired
    private final UserService userService;
    private final MessagingService messagingService;

    @PostMapping("/user/signup")
    public APIResponse<User> signUpUser(@RequestBody User user){
        return userService.signUp(user, AccountType.USER);
    }

    @PostMapping("/agent/signup")
    public APIResponse<User> signUpAgent(@RequestBody User user){
        return userService.signUp(user, AccountType.AGENT);
    }

    @PostMapping("/user/signin")
    public APIResponse<User> signIn(@RequestBody UserRequest loginRequest){
        return userService.signIn(loginRequest);
    }


    @PostMapping("/user/generate/otp")
    public APIResponse<User> generateOTP(@RequestBody UserRequest userRequest){
        return messagingService.generateAndSendOTP(userRequest);
    }

    @PostMapping("/user/verification")
    public APIResponse<User> verifyUser(@RequestBody UserVerificationRequest verificationRequest){
        return userService.verifyUser(verificationRequest);
    }

    @PostMapping("/user/initiate_password_change")
    public APIResponse<User> initiatePasswordReset(@RequestBody UserInitiatePasswordChangeRequest userPasswordResetRequest){
        return userService.initiatePasswordReset(userPasswordResetRequest);
    }

    @PostMapping("/user/change_password")
    public APIResponse<User> initiatePasswordReset(@RequestBody PasswordChangeRequest request){
        return userService.resetPassword(request);
    }

    @PostMapping("/user/logout")
    public APIResponse<User> resetPassword(){
        return userService.logout();
    }


    @PostMapping("/user/delete/{userId}")
    public APIResponse<User> deleteUserById(@PathVariable Long userId){
        return userService.deleteUerById(userId);
    }


    @PutMapping("/user/profile/update")
    public APIResponse<User> updateUserProfileById(Principal principal, @RequestBody User newDetails){
        return userService.updateUserProfileById(principal,newDetails);
    }

    @PutMapping("/user/status/update/{userId}")
    public APIResponse<User> updateUserStatusById(@PathVariable Long userId, @RequestParam Status status){
        return userService.updateUserStatusById(userId, status);
    }

    @GetMapping("/user/get_by_uiid/{uiid}")
    public APIResponse<User> getUserByUiid(@PathVariable String uiid){
        return userService.findUserByUiid(uiid);
    }


    @GetMapping("/users/get_by_account_type")
    public APIResponse<Page<User>> getUsersByAccountType(@RequestParam String type, @RequestParam int page, @RequestParam int size){
        return userService.findUsersByAccountType(PageRequest.of(page,size),type);
    }

    @GetMapping("/users/get_by_status")
    public APIResponse<Page<User>> getUsersByStatus(@RequestParam Status status, @RequestParam int page, @RequestParam int size){
        return userService.findUsersByStatus(PageRequest.of(page,size),status);
    }
    @GetMapping("/users/get_by_referee_uuid/{uuid}")
    public APIResponse<Page<User>> getUsersReferred(@PathVariable String uuid, @RequestParam int page, @RequestParam int size){
        return userService.findAllUsersReferred(PageRequest.of(page,size),uuid);
    }

    @GetMapping("/users/get_all")
    public APIResponse<Page<User>> getAll(@PathVariable String uuid, @RequestParam int page, @RequestParam int size){
        return userService.findAllUsersReferred(PageRequest.of(page,size),uuid);
    }
}
