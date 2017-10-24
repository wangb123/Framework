package com.yaolan.common.config;

/**
 * Created by 10213 on 2017/10/24.
 */

public class Router {
    public class provider {
        public static final String User = "user";
    }

    public class action {
        /**
         * @com.yaolan.common.config.Router.provider.User
         */
        public static final String Login = "user_login";
        public static final String Logout = "user_logout";
        public static final String IsLogin = "user_is_logout";
    }
}
