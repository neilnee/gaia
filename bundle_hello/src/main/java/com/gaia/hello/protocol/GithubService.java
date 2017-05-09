package com.gaia.hello.protocol;

import com.gaia.hello.model.GithubUserInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GithubService {

    @GET("users/{user}")
    Call<GithubUserInfo> queryUserInfo(@Path("user") String user);

}
