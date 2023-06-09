package com.fadedbytes.beo.auth.user;

import java.util.UUID;

public final class ResolvedUser extends User {
    protected final String username;
    protected String displayName;
    public ResolvedUser(String userId, String username, String displayName) {
        super(userId);
        this.username = username;
        this.displayName = displayName;
    }
}
