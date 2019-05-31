package uk.co.taniakolesnik.nasa.retrofit;

import uk.co.taniakolesnik.nasa.module.Result;

public interface GetResultCallback {
    void onGetData(Result result, int position);
}
