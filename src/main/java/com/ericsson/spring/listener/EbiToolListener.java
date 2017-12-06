package com.ericsson.spring.listener;

import java.util.Calendar;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.ericsson.spring.constants.EBIToolConstants;
import com.ericsson.spring.dao.AppresourceDao;
import com.ericsson.spring.model.UserSession;

@Component
public class EbiToolListener implements HttpSessionListener,ApplicationContextAware {
	private static final Logger log = LoggerFactory.getLogger(EbiToolListener.class);
	
	@Autowired
	AppresourceDao oAppresourceDao;

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		log.info(event.toString());

	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		UserSession oUserSession = (UserSession)event.getSession().getAttribute(EBIToolConstants.USERSESSION_CONSTANTS.USER_SESSION_STRING);
		if(oUserSession!=null)
		{
			//oUserSession.setUserId();
			oUserSession.setLogoutTime(Calendar.getInstance().getTimeInMillis());
			oAppresourceDao.updateUserLoginSession(oUserSession);
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (applicationContext instanceof WebApplicationContext) {
            ((WebApplicationContext) applicationContext).getServletContext().addListener(this);
        } else {
            //Either throw an exception or fail gracefully, up to you
            throw new RuntimeException("Must be inside a web application context");
        }
		
	}

}
