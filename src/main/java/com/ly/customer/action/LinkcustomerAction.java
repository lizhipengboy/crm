package com.ly.customer.action;

import com.ly.base.service.LinktypeService;
import com.ly.comm.Bjui;
import com.ly.comm.Page;
import com.ly.comm.ParseObj;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CheckSession;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheManager;


import com.ly.customer.vo.Linkcustomer;
import com.ly.customer.service.LinkcustomerService;


@IocBean
@At("/linkcustomer")
@Fail("json")
@Filters(@By(type=CheckSession.class, args={"username", "/WEB-INF/login.html"}))
public class LinkcustomerAction {

	private static final Log log = Logs.getLog(LinkcustomerAction.class);
	
	@Inject
	private LinkcustomerService linkcustomerService;

    @Inject
    private LinktypeService linktypeService;

    @At("/")
    @Ok("beetl:/WEB-INF/customer/linkcustomer_list.html")
    public void index(@Param("..")Page p,
                      @Param("..")Linkcustomer linkcustomer,
                      @Param("customerid")long customerid,
                      HttpServletRequest request){

        Cnd c = new ParseObj(linkcustomer).getCnd();
        if (c == null || c.equals(""))
        {
            p.setRecordCount(linkcustomerService.listCount(c));
            request.setAttribute("list_obj", linkcustomerService.queryCache(c,p));
        }else{
            p.setRecordCount(linkcustomerService.count(c));
            request.setAttribute("list_obj", linkcustomerService.query(c,p));
        }

        request.setAttribute("page", p);
        request.setAttribute("linkcustomer", linkcustomer);
        request.setAttribute("linktypeList",linktypeService.queryCache(null,new Page()));
        request.setAttribute("customerid",customerid);

    }

    @At
    @Ok("beetl:/WEB-INF/customer/linkcustomer.html")
    public void edit(@Param("id")Long id,
                     @Param("action")int action,
                     @Param("customerid")long customerid,
                      HttpServletRequest request){
        if(id == null || id == 0){
            request.setAttribute("linkcustomer", null);
        }else{
            Linkcustomer linkcustomer = linkcustomerService.fetch(id);
            if (action == 3)
            {
                linkcustomer.setId(null);
            }

            request.setAttribute("linkcustomer", linkcustomer);
        }
        request.setAttribute("linktypeList",linktypeService.queryCache(null,new Page()));
        request.setAttribute("customerid",customerid);

    }

    @At
    @Ok("json")
    public Map<String,String> save( @Param("..")Linkcustomer linkcustomer){
        Object rtnObject;
        if (linkcustomer.getId() == null || linkcustomer.getId() == 0) {
            linkcustomer.setAdddate(new Date());
            rtnObject = linkcustomerService.dao().insert(linkcustomer);
        }else{
            rtnObject = linkcustomerService.dao().updateIgnoreNull(linkcustomer);
        }
        CacheManager.getInstance().getCache(LinkcustomerService.CACHE_NAME).removeAll();
        return Bjui.rtnMap((rtnObject == null) ? false : true, "tab_linkcustomer", true);

    }

    @At
    @Ok("json")
    public Map<String,String> del(@Param("id")Long id)
    {
        int num =  linkcustomerService.delete(id);
        CacheManager.getInstance().getCache(LinkcustomerService.CACHE_NAME).removeAll();
        return Bjui.rtnMap((num > 0) ? true : false , "tab_linkcustomer",false);
    }

}
