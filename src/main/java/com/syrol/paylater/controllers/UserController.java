package com.syrol.paylater.controllers;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.*;
import com.syrol.paylater.services.MessagingService;
import com.syrol.paylater.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    public APIResponse<User> signUp(@RequestBody User user){
        return userService.signUp(user);
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

    @PostMapping("/user/password/reset")
    public APIResponse<User> resetPassword(@RequestBody UserRequest userPasswordResetRequest){
        return userService.resetPassword(userPasswordResetRequest);
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

    @GetMapping("/get_user_by_uiid/{uiid}")
    public APIResponse<List<User>> getUserByUiid(@PathVariable String uiid){
        return userService.findUserByUiid(uiid);
    }


    @GetMapping("/get_users_by_account_type")
    public APIResponse<List<User>> getUsersByAccountType(@RequestParam String type, @RequestParam int page, @RequestParam int size){
        return userService.findUsersByAccountType(PageRequest.of(page,size),type);
    }
}
