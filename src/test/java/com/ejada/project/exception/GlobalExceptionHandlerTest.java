package com.ejada.project.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI("/test-url");
    }

    @Test
    void handleNotFound_ShouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(ex, request);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not found", response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("/test-url", response.getBody().getPath());
    }

    @Test
    void handleConflict_ShouldReturn409() {
        ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException("Already exists");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConflict(ex, request);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Already exists", response.getBody().getMessage());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    void handleBadRequest_ShouldReturn400() {
        BadRequestException ex = new BadRequestException("Bad request");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadRequest(ex, request);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleException_ShouldReturn500() {
        Exception ex = new Exception("Some error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(ex, request);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred.", response.getBody().getMessage());
        assertEquals(500, response.getBody().getStatus());
    }

    @Test
    void handleValidationException_ShouldReturn400WithErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("object", "field1", "Message 1")
        ));

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertNotNull(response.getBody().getValidationErrors());
        assertEquals("Message 1", response.getBody().getValidationErrors().get("field1"));
    }

    @Test
    void handleHttpMessageNotReadableException_ShouldReturn400() {
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Invalid JSON", inputMessage);
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleHttpMessageNotReadableException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request body", response.getBody().getMessage());
    }

    @Test
    void handleBadCredentialsException_ShouldReturn401() {
        BadCredentialsException ex = new BadCredentialsException("Bad creds");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentialsException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password.", response.getBody().getMessage());
        assertEquals(401, response.getBody().getStatus());
    }

    @Test
    void handleAuthorizationDeniedException_ShouldReturn403() {
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Denied");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuthorizationDeniedException(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("You do not have permission to perform this action.", response.getBody().getMessage());
        assertEquals(403, response.getBody().getStatus());
    }

    @Test
    void handleAccessDeniedException_ShouldReturn403() {
        AccessDeniedException ex = new AccessDeniedException("Custom denied message");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Custom denied message", response.getBody().getMessage());
        assertEquals(403, response.getBody().getStatus());
    }
}
