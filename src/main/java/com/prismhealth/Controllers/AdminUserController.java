package com.prismhealth.Controllers;

import java.security.Principal;
import java.util.List;

import com.prismhealth.Models.Users;
import com.prismhealth.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@Api(tags = "Admin APIs")
@RestController
@RequestMapping("admin/user")
public class AdminUserController {
    @Autowired
    private UserService userService;
    @ApiOperation(value = "Retrieves users using phone number")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @GetMapping("/find/{phone}")
    public Users getUserById(@PathVariable String phone) {
        return userService.getUserById(phone);
    }

    @ApiOperation(value = "Retrieves users that are blocked")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "null") })
    @GetMapping("/blocked")
    public List<Users> getBlockedUser() {
        return userService.getBlockedUsers();
    }

    @ApiOperation(value = "Retrieves users pending delete")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "Users not found") })
    @GetMapping("/pendingdelete")
    public List<Users> getPendingDelete() {
        return userService.getDeleteUser();
    }

    @ApiOperation(value = "Retrieves users pending verification")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "Users not found") })
    @GetMapping("/pendingverification")
    public List<Users> getpendingVerifications() {
        return userService.getPendingVerifications();
    }

    @ApiOperation(value = "Approves users using phone number")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("/verify/{phone}")
    public ResponseEntity<?> approveUserAccount(@PathVariable("phone") String phone, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.verifyUser(phone, principal));
    }

    @ApiOperation(value = "Unblocks users using phone number")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("/unblock/{phone}")
    public ResponseEntity<?> unblockUser(@PathVariable("phone") String phone, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.unBlockUser(phone, principal));
    }

    @ApiOperation(value = "Blocks a user using phone number")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("/block/{phone}")
    public ResponseEntity<?> blockeUser(@PathVariable("phone") String phone, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.blockUser(phone, principal));
    }

    @ApiOperation(value = "Approve deletion of users using phone number")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("approvedelete/{phone}")
    public ResponseEntity<?> approveUserDeletion(@PathVariable("phone") String phone, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.approveDeleteUser(phone, principal));
    }

    @ApiOperation(value = "Delete users using phone number")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "ok"), @ApiResponse(code = SC_BAD_REQUEST, message = "User not found") })
    @PostMapping("delete/{phone}")
    public ResponseEntity<?> deleteUser(@PathVariable("phone") String phone, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.deleteUser(phone, principal));
    }

}
