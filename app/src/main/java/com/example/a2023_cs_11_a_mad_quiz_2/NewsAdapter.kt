package com.example.a2023_cs_11_a_mad_quiz_2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.a2023_cs_11_a_mad_quiz_2.databinding.ItemNewsBinding

class NewsAdapter(
    private var articles: List<Article>,
    private val onItemClick: (Article) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    fun updateData(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    inner class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.tvNewsTitle.text = article.title
            binding.tvSourceName.text = article.source.name
            binding.tvPublishedDate.text = article.publishedAt.take(10) // Simple date format

            Glide.with(binding.ivNewsImage.context)
                .load(article.image)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.ivNewsImage)

            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }
    }
}
