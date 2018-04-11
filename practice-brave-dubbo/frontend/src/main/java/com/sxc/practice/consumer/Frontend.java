package com.sxc.practice.consumer;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 *
 * @author: ZMM
 * @version: 1.0
 * Filename:    	frontend
 * Create at:   	2018/3/28
 * <p>
 * Copyright:   	Copyright (c)2018
 * Company:     	songxiaocai
 * <p>
 * Modification History:
 * Date        		      Author          Version      Description
 * ------------------------------------------------------------------
 * 2018/3/28    	          ZMM           1.0          1.0 Version
 */
@RestController
public class Frontend {

    @Reference(version = "1.0.0",
            application = "${dubbo.application.id}",
            url = "dubbo://localhost:12345")
    HelloService helloService;

    @RequestMapping(value = "/test")
   public String test(){
       return helloService.say();
   }

}