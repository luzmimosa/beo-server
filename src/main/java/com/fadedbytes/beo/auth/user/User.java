package com.fadedbytes.beo.auth.user;

import com.fadedbytes.beo.auth.UserService;
import com.fadedbytes.beo.auth.exception.UserNotResolvedException;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public sealed class User permits ResolvedUser {

    public final String userId;

    protected User(String userId) {
        this.userId = userId;
    }

    public @Nullable ResolvedUser resolve() throws UserNotResolvedException {
        ResolvedUser user = UserService.getOrCreate().resolve(this);
        if (user == null) {
            throw new UserNotResolvedException(userId);
        }

        return user;
    }

}
