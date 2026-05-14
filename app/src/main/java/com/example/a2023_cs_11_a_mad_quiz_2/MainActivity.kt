package com.example.a2023_cs_11_a_mad_quiz_2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a2023_cs_11_a_mad_quiz_2.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var newsAdapter: NewsAdapter
    private val apiService = ApiService.create()
    private val apiKey = "4c6beb662841b3580ac53e2aa9c20bfb" // Replace with actual API key

    private val countries = mapOf(
        "United States" to "us",
        "Pakistan" to "pk",
        "United Kingdom" to "gb",
        "India" to "in",
        "Saudi Arabia" to "sa",
        "UAE" to "ae"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSpinner()

        binding.btnRefresh.setOnClickListener {
            val selectedCountry = binding.spinnerCountry.selectedItem.toString()
            fetchNews(countries[selectedCountry] ?: "us")
        }
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

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries.keys.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCountry.adapter = adapter

        binding.spinnerCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val countryName = countries.keys.toList()[position]
                fetchNews(countries[countryName] ?: "us")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun fetchNews(countryCode: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvNews.visibility = View.GONE
        binding.tvError.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = apiService.getTopHeadlines(country = countryCode, apiKey = apiKey)
                newsAdapter.updateData(response.articles)
                binding.progressBar.visibility = View.GONE
                binding.rvNews.visibility = View.VISIBLE
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = "Error: ${e.message}"
            }
        }
    }
}
