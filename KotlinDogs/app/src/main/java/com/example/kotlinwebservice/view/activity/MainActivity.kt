package com.example.kotlinwebservice.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinwebservice.R
import com.example.kotlinwebservice.model.network.APIService
import com.example.kotlinwebservice.model.pojo.DogsResponse
import com.example.kotlinwebservice.view.adapter.DogsAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(),
    androidx.appcompat.widget.SearchView.OnQueryTextListener {


    lateinit var imagesPuppies:List<String>
    private var dogsAdapter: DogsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pesquisar.setOnQueryTextListener(this)
    }


     private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/breed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        searchByName(query.toLowerCase())
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }


    private fun searchByName(query: String) {
        doAsync {
            val call = getRetrofit().create(APIService::class.java).getCharecterByName("$query/images").execute()
            val puppies = call.body() as DogsResponse
            uiThread {
                if (puppies.status == "success") {
                    inicializaLista(puppies)
                } else {
                    Toast.makeText(this@MainActivity, "Deu merda na chamada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun inicializaLista(cachorros: DogsResponse) {
        if(cachorros.status == "success"){
            imagesPuppies = cachorros.images
        }
        dogsAdapter = DogsAdapter(imagesPuppies)
        recyclerMain.setHasFixedSize(true)
        recyclerMain.layoutManager = LinearLayoutManager(this)
        recyclerMain.adapter = dogsAdapter
    }
    
}
