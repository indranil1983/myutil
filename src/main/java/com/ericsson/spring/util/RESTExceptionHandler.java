package com.ericsson.spring.util;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.CommunicationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ericsson.spring.model.EbiUtilResponseBean;

/**
 * Any new exception type has to be registered with proper handling method.
 * For existing AGW exceptions, HttpStatus code has to be identified per error case
 * since one type of internal exception (e.g. ActionException) may represent both
 * client and server errors, hence need to be separated by appropriate HttpStatus code. 
 *
 */
@ControllerAdvice
public class RESTExceptionHandler extends ResponseEntityExceptionHandler 
{
    
    
    @ExceptionHandler({ CommunicationException.class })
    public ResponseEntity<?> handleAll(CommunicationException ex, WebRequest request) {
       
    	EbiUtilResponseBean oEbiUtilResponseBean = new EbiUtilResponseBean();
    	oEbiUtilResponseBean.getErrorList().add(ex.getMessage());
		return new ResponseEntity<EbiUtilResponseBean>(oEbiUtilResponseBean,HttpStatus.EXPECTATION_FAILED);
    }
    
    
   /* @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
      NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request)
    {
        String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
     
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }*/
    
   /*@Override
   protected ResponseEntity<Object> handleNoSuchRequestHandlingMethod(NoSuchRequestHandlingMethodException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    	// TODO Auto-generated method stub
    	    	RESTErrorDetails oRESTErrorDetails = new RESTErrorDetails(String.valueOf(status), 
    			ex.getLocalizedMessage());
    	
    	    	return new ResponseEntity<Object>(oRESTErrorDetails, new HttpHeaders(), status);
   }
    
   
   @Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		// TODO Auto-generated method stub
		return super.handleHttpRequestMethodNotSupported(ex, headers, status, request);
	}  
   
*/   

}
