package com.example.a2023_cs_11_a_mad_quiz_2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.a2023_cs_11_a_mad_quiz_2.databinding.ActivityNewsDetailBinding

class NewsDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val article = intent.getParcelableExtra<Article>("article")

        article?.let { item ->
            binding.tvDetailTitle.text = item.title
            binding.tvDetailSource.text = item.source.name
            binding.tvDetailDate.text = item.publishedAt
            binding.tvDetailDescription.text = item.description
            binding.tvDetailContent.text = item.content

            Glide.with(this)
                .load(item.image)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.ivDetailImage)

            binding.btnReadFull.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                startActivity(intent)
            }
        }
    }
}
