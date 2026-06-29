package com.interswitch.verveguardfraudservice.model;

import java.util.Collections;
import java.util.Set;

public record ClientProfile(
        String clientId,
        ClientTier tier,
        Set<RuleCategory> activeCategories

) {
    // Compact constructor to protect against null being passed directly
    public ClientProfile {
        activeCategories = (activeCategories == null)
                ? Collections.emptySet()
                : Set.copyOf(activeCategories); // Makes the set immutable
    }

    // Overloaded constructor omitting the optional field
    public ClientProfile(String clientId, ClientTier tier) {
        this(clientId, tier, Collections.emptySet());
    }
}
