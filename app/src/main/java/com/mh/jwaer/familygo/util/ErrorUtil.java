package com.mh.jwaer.familygo.util;

import android.util.Log;

import com.mh.jwaer.familygo.data.models.ErrorResponse;
import com.mh.jwaer.familygo.data.network.RetrofitClient;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtil {
    private static final String TAG = "ErrorUtil";
    public  static ErrorResponse  parseError(Response<?> response){
        Converter<ResponseBody, ErrorResponse> converter =
                RetrofitClient.retrofit().responseBodyConverter(ErrorResponse.class, new Annotation[0]);
        ErrorResponse error;
        try{
            assert response.errorBody() != null;
            error = converter.convert(response.errorBody());

        }
        catch (IOException e ){
            Log.i(TAG, "parseError: e: "+ e.getMessage());
            return new ErrorResponse();
        }
        return error;
    }
}
