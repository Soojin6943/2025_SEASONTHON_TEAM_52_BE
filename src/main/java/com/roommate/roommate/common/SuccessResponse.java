package com.roommate.roommate.common;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
public class SuccessResponse<T> {

    private int status;
    private String message;
    private T data;

    // 성공 응답 (with data)
    public static <T> ResponseEntity<SuccessResponse<T>> onSuccess(String message, HttpStatus httpStatus, T data){
        return new ResponseEntity<>(new SuccessResponse<>(httpStatus.value(), message, data), httpStatus);
    }

    // 데이터 없는 성공응답
    public static ResponseEntity<SuccessResponse<Void>> ok(String message){
        return new ResponseEntity<>(new SuccessResponse<>(HttpStatus.OK.value(), message, null), HttpStatus.OK);
    }
}
