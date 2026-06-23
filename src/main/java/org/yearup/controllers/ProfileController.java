package org.yearup.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.exception.NotFoundException;
import org.yearup.models.Profile;
import org.yearup.models.User;
import org.yearup.service.ProfileService;
import org.yearup.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final UserService userService;

    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public Profile getProfile(Principal principal) {
        User user = userService.getLoggedInUser(principal);
        return profileService.getProfileByUserId(user.getId())
                .orElseThrow(NotFoundException::new);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public Profile updateProfile(Principal principal, @RequestBody Profile data) {
        User user = userService.getLoggedInUser(principal);
        return profileService.updateProfileByUserId(user.getId(), data);
    }
}
