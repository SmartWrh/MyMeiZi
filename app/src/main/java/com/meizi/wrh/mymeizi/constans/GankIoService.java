package com.meizi.wrh.mymeizi.constans;

import com.meizi.wrh.mymeizi.model.GankIoModel;
import com.meizi.wrh.mymeizi.model.GithubTest;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by wrh on 16/2/2.
 */
public interface GankIoService {

    @GET("{type}/10/{count}")
    Observable<GankIoModel> homeResult(@Path("type") String type, @Path("count") int count);
}
