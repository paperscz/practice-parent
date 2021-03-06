package com.sxc.dubbo.user.application.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.sxc.dubbo.api.provider.user.UserQueryProvider;

import java.util.UUID;

/**
 * Description:
 *
 * @author: ZMM
 * @version: 1.0
 * Filename:    	UserQueryProviderImpl
 * Create at:   	2018/3/23
 * <p>
 * Copyright:   	Copyright (c)2018
 * Company:     	songxiaocai
 * <p>
 * Modification History:
 * Date        		      Author          Version      Description
 * ------------------------------------------------------------------
 * 2018/3/23    	          ZMM           1.0          1.0 Version
 */
@Service
public class UserQueryProviderImpl implements UserQueryProvider {

    @Override
    public String queryName(Integer id) {
        return UUID.randomUUID().toString();
    }
}
