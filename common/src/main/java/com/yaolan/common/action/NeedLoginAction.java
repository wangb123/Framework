package com.yaolan.common.action;

import android.content.Context;

import com.spinytech.macore.MaAction;
import com.spinytech.macore.MaActionResult;
import com.spinytech.macore.MaApplication;
import com.spinytech.macore.router.LocalRouter;
import com.spinytech.macore.router.RouterRequest;
import com.spinytech.macore.router.RouterResponse;
import com.yaolan.common.config.Router;

import java.util.HashMap;

/**
 * Created by 王冰 on 2017/8/21.
 */

public abstract class NeedLoginAction extends MaAction {

    public abstract MaActionResult invokeImp(Context context, HashMap<String, String> requestData);

    public MaActionResult invokeImp(Context context, HashMap<String, String> requestData, Object object) {
        return new MaActionResult.Builder().code(MaActionResult.CODE_NOT_IMPLEMENT).msg("This method has not yet been implemented.").build();
    }

    @Override
    public final MaActionResult invoke(Context context, HashMap<String, String> requestData) {

        if (isCurrentLogin(context)) {
            return invokeImp(context, requestData);
        } else {
            return loginInvoke(context, requestData, null);
        }
    }

    @Override
    public final MaActionResult invoke(Context context, HashMap<String, String> requestData, Object object) {

        if (isCurrentLogin(context)) {
            return invokeImp(context, requestData, object);
        } else {
            return loginInvoke(context, requestData, object);
        }
    }

    private boolean isCurrentLogin(Context context) {
        boolean isLogin = false;
        try {
            RouterResponse response = LocalRouter.getInstance(MaApplication.getMaApplication())
                    .route(context, RouterRequest.obtain(context)
                            .provider(Router.provider.User)
                            .action(Router.action.IsLogin));
            isLogin = response != null && response.getCode() == MaActionResult.CODE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isLogin;
    }

    private MaActionResult loginInvoke(Context context, HashMap<String, String> requestData, Object object) {
        try {
            RouterRequest request = RouterRequest.obtain(context)
                    .provider(Router.provider.User)
                    .action(Router.action.Login)
                    .object(object);
            if (requestData != null) {
                request.getData().putAll(requestData);
            }
            RouterResponse response = LocalRouter.getInstance(MaApplication.getMaApplication())
                    .route(context, request);
            return new MaActionResult.Builder()
                    .code(response.getCode())
                    .msg(response.getMessage())
                    .data(response.getData())
                    .object(response.getObject())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return new MaActionResult.Builder()
                    .code(MaActionResult.CODE_ERROR)
                    .msg(e.getMessage())
                    .data(e.getClass().getName())
                    .object(e)
                    .build();
        }
    }

}
