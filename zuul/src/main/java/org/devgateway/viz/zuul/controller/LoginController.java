package org.devgateway.viz.zuul.controller;

import org.devgateway.viz.zuul.security.bean.AuthResponse;
import org.devgateway.viz.zuul.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/gateway")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @CrossOrigin("*")
    @PostMapping("/signin")
    @ResponseBody
    public ResponseEntity<AuthResponse> signIn(String username, String password) {
        String token = loginService.login(username, password);
        HttpHeaders headers = new HttpHeaders();

        List<String> headerList = new ArrayList<>();
        headerList.add("Content-Type");
        headerList.add("Accept");
        headerList.add("X-Requested-With");
        headerList.add("Authorization");

        List<String> exposeList = new ArrayList<>();
        exposeList.add("Authorization");

        headers.setAccessControlAllowHeaders(headerList);
        headers.setAccessControlExposeHeaders(exposeList);
        headers.set("Authorization", token);

        return new ResponseEntity<>(new AuthResponse(token), headers, HttpStatus.CREATED);
    }

    @CrossOrigin("*")
    @PostMapping("/signout")
    @ResponseBody
    public ResponseEntity<AuthResponse> signOut(@RequestHeader(value = "Authorization") String token) {
        HttpHeaders headers = new HttpHeaders();

        if (loginService.logout(token)) {
            headers.remove("Authorization");
            return new ResponseEntity<>(new AuthResponse("Logged out"), headers, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(new AuthResponse("Logout Failed"), headers, HttpStatus.NOT_MODIFIED);
    }

    /**
     * @param token
     * @return boolean.
     * if request reach here it means it is a valid token.
     */
    @PostMapping("/valid/token")
    @ResponseBody
    public Boolean isValidToken(@RequestHeader(value = "Authorization") String token) {
        return loginService.isValidToken(token);
    }

    @GetMapping("/test")
    public String test(){
        return "HELLO THIS IS TEST";
    }

}
