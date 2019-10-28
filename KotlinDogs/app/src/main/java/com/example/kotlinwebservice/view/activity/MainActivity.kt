package com.example.kotlinwebservice.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.GridLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
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


    lateinit var listaDogs:List<String>
    private var dogsAdapter: DogsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pesquisar.setOnQueryTextListener(this)

        floatingActionButton.setOnClickListener{
            pupulaLista()
        }

    }

    private fun getRetrofitList(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/breed/image/random")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


     private fun getRetrofitRaca(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/breed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        procuraPorNome(query.toLowerCase())
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }


    private fun procuraPorNome(query: String) {
        doAsync {
            val call = getRetrofitRaca().create(APIService::class.java).getCharecterByName("$query/images").execute()
            val puppies = call.body() as DogsResponse
            uiThread {
                if (puppies.status == "success") {
                    inicializaListaComPesquisa(puppies)
                } else {
                    Toast.makeText(this@MainActivity, "Deu merda na chamada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun pupulaLista() {
        doAsync {
            val call = getRetrofitList().create(APIService::class.java).getRandomList().execute()
            val puppies = call.body() as DogsResponse
            uiThread {
                if (puppies.status == "success") {
                    inicializaLista(puppies)
                } else {
                    Log.i("LOG", "ERROR PUPULA LISTA")
                }
            }
        }
    }

    private fun inicializaListaComPesquisa(cachorros: DogsResponse) {
        if(cachorros.status == "success"){
            listaDogs = cachorros.images
        }
        dogsAdapter = DogsAdapter(listaDogs)
        recyclerMain.setHasFixedSize(true)
        recyclerMain.layoutManager = LinearLayoutManager(this)
        recyclerMain.adapter = dogsAdapter
    }

    private fun inicializaLista(cachorros: DogsResponse){
        if(cachorros.status == "success"){
            listaDogs = cachorros.images
        }
        dogsAdapter = DogsAdapter(listaDogs)
        recyclerMain.setHasFixedSize(true)
        recyclerMain.layoutManager = GridLayoutManager(this, 3)
        recyclerMain.adapter = dogsAdapter
    }
    
}
