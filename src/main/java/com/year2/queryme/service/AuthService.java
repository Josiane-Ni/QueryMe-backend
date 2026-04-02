package com.year2.queryme.service;

import com.year2.queryme.model.User;
import com.year2.queryme.model.dto.LoginRequest;
import com.year2.queryme.model.dto.LoginResponse;
import com.year2.queryme.model.dto.SignupRequest;
import com.year2.queryme.repository.UserRepository;
import com.year2.queryme.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private GuestService guestService;

    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        
        // 1. Authenticate using AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 2. Load User Details and generate JWT
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername()));
        
        String jwt = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // 3. Construct LoginResponse
        LoginResponse response = new LoginResponse(
                jwt,
                user.getRole(),
                user.getEmail(),
                user.getId(),
                "Login successful!"
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
        
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        String role = signUpRequest.getRole().toUpperCase();
        
        switch (role) {
            case "STUDENT":
                if (signUpRequest.getCourseId() == null) {
                   return ResponseEntity.badRequest().body("Error: Course ID is required for Students");
                }
                studentService.registerStudent(
                        signUpRequest.getEmail(),
                        signUpRequest.getPassword(),
                        signUpRequest.getFullName(),
                        signUpRequest.getCourseId(),
                        signUpRequest.getClassGroupId()
                );
                break;
                
            case "TEACHER":
                teacherService.registerTeacher(
                        signUpRequest.getEmail(),
                        signUpRequest.getPassword(),
                        signUpRequest.getFullName()
                );
                break;

            case "ADMIN":
                adminService.registerAdmin(
                        signUpRequest.getEmail(),
                        signUpRequest.getPassword(),
                        signUpRequest.getFullName()
                );
                break;

            case "GUEST":
                guestService.registerGuest(
                        signUpRequest.getEmail(),
                        signUpRequest.getPassword(),
                        signUpRequest.getFullName()
                );
                break;

            default:
                return ResponseEntity.badRequest().body("Error: Invalid role provided!");
        }

        return ResponseEntity.ok("User registered successfully as " + role);
    }
}
