package services;

import com.squareup.okhttp.RequestBody;

import java.util.List;

import domains.services.SyncEntriesByChildren;
import domains.services.EntryChildren;
import domains.services.SyncChildren;
import domains.services.UnlockedCategories;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;

/**
 * Created by juanlabrador on 10/09/15.
 */
public interface UserServices {


    /**
     * get Key for create account
     * @param email email text
     * @return email
     */
    @GET("littledot/link.php")
    Call<Object> getKeyLogin(@Query("email") String email);

    /**
     * Get List categories unlocked from server
     * @param userId
     * @return
     */
    @GET("user/getUnlockedCategories?userId")
    Call<UnlockedCategories> getUnlockedCategories(@Query("userId") String userId);

    /**
     * get response if children was updated or not
     * @param syncChildren
     * @return
     */
    @FormUrlEncoded
    @POST("user/sync_child")
    Call<Object> syncChildren(@Field("data") String syncChildren);

    /**
     * delete children
     * @param childGuids
     * @return 200 ok
     */
    @DELETE("user/sync_child")
    Call<Object> deleteChildren(@Query("childGuids") List<String> childGuids);

    /**
     * Sync with data user, get children more all dots data by child
     * @param userId
     * @param dateModified
     * @return
     */
    @GET("user/sync?userId?dateModified")
    Call<List<EntryChildren>> syncUser(@Query("userId") String userId, @Query("dateModified") String dateModified);


    /**
     * get response if entries was updated or not
     * @param syncEntriesByChildren
     * @return
     */
    @FormUrlEncoded
    @POST("user/sync_entries")
    Call<Object> syncEntries(@Field("data") String syncEntriesByChildren);

    /**
     * delete entries
     * @param entryGuids
     * @return
     */

    @DELETE("user/sync_entries")
    Call<Object> syncDeleteEntries(@Query("entryGuids") List<String> entryGuids);

    /**
     * upload image mode multipart
     * @param id id image
     * @param image image base64
     * @return state 200 or 404.
     */
    @Multipart
    @POST("user/sync/image")
    Call<Object> uploadImage(@Part("id") RequestBody id, @Part("image") RequestBody image);
}
