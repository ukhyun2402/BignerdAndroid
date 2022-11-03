
import com.wookhyun.photogallery.FlickrResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrAPI {
    @GET("services/rest/?method=flickr.interestingness.getList")
    suspend fun fetchPhotos(@Query("page") page: Int = 1): FlickrResponse

    @GET("services/rest/?method=flickr.photos.search")
    suspend fun searchPhotos(@Query("text") query: String): FlickrResponse
}