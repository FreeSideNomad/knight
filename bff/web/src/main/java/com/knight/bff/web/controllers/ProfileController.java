package com.knight.bff.web.controllers;

import com.knight.contexts.serviceprofiles.management.api.queries.SpmQueries;
import com.knight.platform.sharedkernel.ClientId;
import com.knight.platform.sharedkernel.ServicingProfileId;
import org.springframework.web.bind.annotation.*;

/**
 * BFF controller for profile-related endpoints.
 * Composes data from multiple bounded contexts.
 */
@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final SpmQueries spmQueries;

    public ProfileController(SpmQueries spmQueries) {
        this.spmQueries = spmQueries;
    }

    @GetMapping("/servicing/{clientUrn}/summary")
    public SpmQueries.ServicingProfileSummary getServicingProfileSummary(@PathVariable String clientUrn) {
        ClientId clientId = ClientId.of(clientUrn);
        ServicingProfileId profileId = ServicingProfileId.of(clientId);
        return spmQueries.getServicingProfileSummary(profileId);
    }
}
