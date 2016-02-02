package com.meizi.wrh.mymeizi.constans;

import com.meizi.wrh.mymeizi.model.GithubTest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by wrh on 16/2/1.
 */
public interface GitHubService {
    @GET("users/{user}")
    Call<GithubTest> listRepos(@Path("user") String user);

    @GET("users/{user}")
    Observable<GithubTest> listRepos2(@Path("user") String user);
}
