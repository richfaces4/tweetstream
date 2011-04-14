package org.richfaces.examples.tweetstream.ui.useragent;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 * @author jbalunas@redhat.com
 */
@Named("userAgent")
@RequestScoped
public class UserAgentProcessor {
    private String userAgentStr;
    private String httpAccept;
    private UAgentInfo uAgentTest;

    public UserAgentProcessor() {
    }

    @PostConstruct
    public void init(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request=(HttpServletRequest)context.getExternalContext().getRequest();
        userAgentStr =  request.getHeader("user-agent");
        httpAccept = request.getHeader("Accept");
        uAgentTest = new UAgentInfo(userAgentStr, httpAccept);
    }

    public boolean isPhone(){
        return uAgentTest.detectIphone();
    }

    public boolean isTablet(){
        return uAgentTest.detectIpad();
    }


}
