package com.sid.nike.db

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.MutableLiveData
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.sid.nike.BuildConfig
import com.sid.nike.db.dao.DefinitionDao
import com.sid.nike.db.dto.Definition
import com.sid.nike.db.dto.Response
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Repository singleton acts as a mediator between all ViewModels and data sources (like DB or Network resource).
 * Note - We can use "object" to create singleton Repository instead of Singleton pattern, but Object won't have constructors.
 */
class Repository {
    private lateinit var _application: Application
    private var _call: Call<Response>? = null
    private var _database:AppDatabase? = null
    private val _defaultList by lazy {
        mutableListOf<Definition>()
    }
    private val _apiService by lazy {
        DictionaryApiService.create()
    }
    val definitionsLiveData: MutableLiveData<List<Definition>> by lazy {
        MutableLiveData<List<Definition>>()
    }
    val definitionsLocalLiveData: MutableLiveData<List<Definition>> by lazy {
        MutableLiveData<List<Definition>>()
    }

    companion object {
        // A singleton instance of Repository
        var INSTANCE: Repository? = null

        /**
         * A function to create a Repository instance
         */
        fun createInstance(application: Application): Repository {
            if (INSTANCE == null) INSTANCE = Repository().apply {
                _application = application
                _database = AppDatabase.getInstance(application)
            }
            return INSTANCE as Repository
        }
    }

    fun downloadTerm(term: String) {
        // Make a network call only if text is not empty
        if (!term.isEmpty()) {

            // Check any request is made previously. If so cancel it.
            if (_call != null && _call?.isExecuted!!) _call?.cancel()

            // create network call
            _call = _apiService.getDefinitions(term, BuildConfig.API_KEY)

            // Enqueue the request using retrofit.
            _call?.enqueue(object : Callback<Response> {
                override fun onFailure(call: Call<Response>, t: Throwable) {
                    // Delete all previous definitions if response for current request is a failure.
                    definitionsLiveData.postValue(_defaultList)
                }

                override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                    if (response.isSuccessful) {
                        var definitions = response.body()?.definitions

                        // Post the results to UI
                        definitionsLiveData.postValue(definitions)

                        // Also store the information in DB for further reference
                        insertTermDefinitions(definitions!!)
                    } else {
                        // Delete all previous definitions if response for current request is a failure.
                        definitionsLiveData.postValue(_defaultList)
                    }
                }
            })
        }
    }

    fun loadTerm(term: String){
        QueryAsyncTask(_database?.DefinitionDao()!!).execute(term)
    }

    fun deleteTerm(term: String){
        DeleteAsyncTask(_database?.DefinitionDao()!!).execute(term)
    }

    private fun insertTermDefinitions(definitions:List<Definition>){
        InsertAsyncTask(_database?.DefinitionDao()!!).execute(*definitions!!.toTypedArray())
    }

    /**
     * Inner Class(s)
     */
    inner class DeleteAsyncTask(var dao: DefinitionDao): AsyncTask<String,Unit, Unit>(){
        override fun doInBackground(vararg params: String?) {
            dao.deleteDefinitionsByTerm(params[0]!!)
        }
    }

    inner class QueryAsyncTask(var dao: DefinitionDao):AsyncTask<String,Unit,MutableList<Definition>>(){
        override fun doInBackground(vararg params: String?) : MutableList<Definition> {
            var currentTerm = params[0]
            return dao.getAllTermDefinitions(currentTerm!!)
        }

        override fun onPostExecute(result: MutableList<Definition>?) {
            super.onPostExecute(result)
            definitionsLocalLiveData.postValue(result)
        }
    }

    inner class InsertAsyncTask(var dao: DefinitionDao): AsyncTask<Definition,Unit, Unit>(){
        override fun doInBackground(vararg definitions: Definition) {
            dao.insertAllDefinitions(*definitions)
        }
    }
}

interface DictionaryApiService {
    companion object {
        fun create(): DictionaryApiService {

            var client = OkHttpClient.Builder().addInterceptor(createHttpLoggingInterceptor()).build()

            var retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl("https://${BuildConfig.API_HOST}")
                .addConverterFactory(JacksonConverterFactory.create(createObjectMapper()))

                .build()
            return retrofit.create(DictionaryApiService::class.java)
        }

        fun createHttpLoggingInterceptor(): HttpLoggingInterceptor {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            return httpLoggingInterceptor
        }

        /**
         * A function to create object Mapper.
         * We are ignoring few params in Definition class. So we need to set few configurations like FAIL_ON_UNKNOWN_PROPERTIES as false
         * All other configurations are general configurations.
         */
        fun createObjectMapper(): ObjectMapper {
            var objectMapper = ObjectMapper()
            objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
            objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, true)
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            objectMapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
            return objectMapper
        }
    }

    @GET("define")
    fun getDefinitions(@Query("term") term: String, @Header("X-RapidAPI-Key") key: String): Call<Response>
}