package mover.backend.security.auth.ajax;

import com.fasterxml.jackson.databind.ObjectMapper;
import mover.backend.security.exceptions.AuthMethodNotSupportedException;
import mover.backend.security.exceptions.JwtExpiredTokenException;
import mover.backend.web.rest.errors.ErrorConstants;
import mover.backend.web.rest.errors.ErrorVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AjaxAwareAuthenticationFailureHandler class is our
 * custom implementation of {@link AuthenticationFailureHandler} interface.
 * Responsibility of this class is make a response with {@link ErrorVM}.
 */
@Component
public class AjaxAwareAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper mapper;
    
    @Autowired
    public AjaxAwareAuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }	
    
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException e) throws IOException, ServletException {
		
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		
		if (e instanceof BadCredentialsException) {
			mapper.writeValue(response.getWriter(), new ErrorVM(ErrorConstants.ERR_INVALID_CREDENTIALS));
		} else if (e instanceof JwtExpiredTokenException) {
			mapper.writeValue(response.getWriter(), new ErrorVM(ErrorConstants.ERR_TOKEN_EXPIRED));
		} else if (e instanceof AuthMethodNotSupportedException) {
		    mapper.writeValue(response.getWriter(), new ErrorVM(ErrorConstants.ERR_METHOD_NOT_SUPPORTED));
		}

		mapper.writeValue(response.getWriter(), new ErrorVM(ErrorConstants.ERR_VALIDATION_FAILED));
	}
}
