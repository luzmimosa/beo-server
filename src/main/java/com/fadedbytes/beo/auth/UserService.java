package com.fadedbytes.beo.auth;

import com.fadedbytes.beo.auth.user.ResolvedUser;
import com.fadedbytes.beo.auth.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Set;

public class UserService {

    private static UserService instance = null;
    public static UserService getOrCreate() {
        if (instance == null) instance = new UserService();
        return instance;
    }

    private final @NotNull ArrayList<ResolvedUser> CACHED_USERS;

    private UserService() {
        this.CACHED_USERS = new ArrayList<>();
    }

    private boolean cacheContains(User user) {
        return getFromCache(user) != null;
    }

    private @Nullable ResolvedUser getFromCache(User user) {
        return CACHED_USERS.stream().filter(u -> u.userId.equals(user.userId)).findFirst().orElse(null);
    }

    public @Nullable ResolvedUser resolve(User user) {
        if (cacheContains(user)) return getFromCache(user);
        ResolvedUser resolvedUser = new ResolvedUser(user.userId, "alpaca19", "Alpaca 💛");
        CACHED_USERS.add(resolvedUser);
        return resolvedUser;
    }

}
