package com.example.a2023_cs_11_a_mad_quiz_2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a2023_cs_11_a_mad_quiz_2.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var newsAdapter: NewsAdapter
    private val apiService = ApiService.create()
    private val countryApiService = CountryApiService.create()
    private val apiKey = "4c6beb662841b3580ac53e2aa9c20bfb" 

    private var countriesList: List<Country> = emptyList()
    private var selectedCountryCode: String = "us"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchView()
        setupRefreshButton()
        
        fetchCountries()
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(emptyList()) { article ->
            val intent = Intent(this, NewsDetailActivity::class.java)
            intent.putExtra("article", article)
            startActivity(intent)
        }
        binding.rvNews.layoutManager = LinearLayoutManager(this)
        binding.rvNews.adapter = newsAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    searchNews(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    private fun setupRefreshButton() {
        binding.btnRefresh.setOnClickListener {
            fetchNews(selectedCountryCode)
        }
        binding.btnRetry.setOnClickListener {
            fetchCountries()
        }
    }

    private fun fetchCountries() {
        binding.progressBar.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
        lifecycleScope.launch {
            try {
                countriesList = countryApiService.getCountries().sortedBy { it.name.common }
                val countryNames = countriesList.map { it.name.common }
                val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, countryNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCountry.adapter = adapter

                binding.spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedCountryCode = countriesList[position].cca2.lowercase()
                        fetchNews(selectedCountryCode)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                binding.progressBar.visibility = View.GONE
            } catch (e: Exception) {
                showError("Failed to load countries: ${e.message}")
            }
        }
    }

    private fun fetchNews(countryCode: String) {
        showLoading()
        lifecycleScope.launch {
            try {
                val response = apiService.getTopHeadlines(country = countryCode, apiKey = apiKey)
                newsAdapter.updateData(response.articles)
                showContent()
            } catch (e: Exception) {
                showError("Error fetching news: ${e.message}")
            }
        }
    }

    private fun searchNews(query: String) {
        showLoading()
        lifecycleScope.launch {
            try {
                val response = apiService.searchNews(query = query, country = selectedCountryCode, apiKey = apiKey)
                newsAdapter.updateData(response.articles)
                showContent()
            } catch (e: Exception) {
                showError("Search failed: ${e.message}")
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvNews.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
    }

    private fun showContent() {
        binding.progressBar.visibility = View.GONE
        binding.rvNews.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.rvNews.visibility = View.GONE
        binding.errorLayout.visibility = View.VISIBLE
        binding.tvError.text = message
    }
}
