package com.example.beside.common.Exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.beside.common.Exception.ExceptionDetail.AdjustScheduleException;
import com.example.beside.common.Exception.ExceptionDetail.CurrentPasswordEqualNewPassword;
import com.example.beside.common.Exception.ExceptionDetail.EmailValidateException;
import com.example.beside.common.Exception.ExceptionDetail.InviteMyMoimException;
import com.example.beside.common.Exception.ExceptionDetail.MoimMakeException;
import com.example.beside.common.Exception.ExceptionDetail.MoimParticipateException;
import com.example.beside.common.Exception.ExceptionDetail.NoResultListException;
import com.example.beside.common.Exception.ExceptionDetail.PasswordException;
import com.example.beside.common.Exception.ExceptionDetail.PasswordNotCorrectException;
import com.example.beside.common.Exception.ExceptionDetail.SocialLoginException;
import com.example.beside.common.Exception.ExceptionDetail.UserAlreadyExistException;
import com.example.beside.common.Exception.ExceptionDetail.UserNotExistException;
import com.example.beside.common.Exception.ExceptionDetail.UserValidateNickName;

import jakarta.mail.MessagingException;

// 전역 Exception Handler

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<?> handlePsswordException(PasswordException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Password Validation failed", errors);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(PasswordNotCorrectException.class)
    public ResponseEntity<?> handlePasswordNotCorrectException(PasswordNotCorrectException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Password not correct", errors);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(UserNotExistException.class)
    public ResponseEntity<?> handleUserNotExistException(UserNotExistException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "User Not Exist", errors);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<?> handleMessagingException(MessagingException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Email send error", errors);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(EmailValidateException.class)
    public ResponseEntity<?> handleEmailValidateException(EmailValidateException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Email Validation error", errors);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        List<String> errors = new ArrayList<>();
        ApiError apiError = new ApiError(null, null, errors);
        errors.add(ex.getMessage());

        if (ex.getClass().getSimpleName().equals("JwtException"))
            apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Invalid Jwt", errors);
        else
            apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Runtime Error", errors);
            
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<?> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "User already Exist", errors);

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(MoimParticipateException.class)
    public ResponseEntity<?> handleMoimParticipateException(MoimParticipateException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Can't participate moim", errors);

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(InviteMyMoimException.class)
    public ResponseEntity<?> handleInviteMyMoimException(InviteMyMoimException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "Can't invite moim", errors);

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(MoimMakeException.class)
    public ResponseEntity<?> handleMoimMakeException(MoimMakeException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Can't make moim", errors);

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(UserValidateNickName.class)
    public ResponseEntity<?> handleUserValidateNickName(UserValidateNickName ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Nickname validation failed", errors);

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(AdjustScheduleException.class)
    public ResponseEntity<?> handleAdjustScheduleException(AdjustScheduleException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "adjust moim schedule failed", errors);

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(SocialLoginException.class)
    public ResponseEntity<?> handleSocialLoginException(SocialLoginException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "소셜 로그인이 실패했습니다", errors);

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(CurrentPasswordEqualNewPassword.class)
    public ResponseEntity<?> handleCurrentPasswordEqualNewPassword(CurrentPasswordEqualNewPassword ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
                "The current password and the new password are the same", errors);

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(NoResultListException.class)
    public ResponseEntity<?> handleNoResultList(NoResultListException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "No results were found", errors);

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
