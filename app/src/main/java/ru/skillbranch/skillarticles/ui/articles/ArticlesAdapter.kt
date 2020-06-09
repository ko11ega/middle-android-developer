package ru.skillbranch.skillarticles.ui.articles

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.ui.custom.ArticleItemView

class ArticlesAdapter(private val listener: (ArticleItemData)-> Unit) :
    PagedListAdapter<ArticleItemData, ArticleVH>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleVH {
        val view = ArticleItemView(parent.context)
        return ArticleVH(view)
    }

    override fun onBindViewHolder(holder: ArticleVH, position: Int) {
        holder.bind(getItem(position), listener)
    }
}

class ArticleDiffCallback: DiffUtil.ItemCallback<ArticleItemData>(){
    override fun areItemsTheSame(oldItem: ArticleItemData, newItem: ArticleItemData): Boolean = oldItem.id == newItem.id


    override fun areContentsTheSame(oldItem: ArticleItemData, newItem: ArticleItemData): Boolean = oldItem == newItem

}

class ArticleVH(val containerView: View) : RecyclerView.ViewHolder(containerView) {
    fun bind(
        item: ArticleItemData?,
        listener: (ArticleItemData) -> Unit
    ){

        //if use placeholder item may be null
        (containerView as ArticleItemView).bind(item!!)
        itemView.setOnClickListener { listener(item!!)}

    }

}

/*
val posterSize = containerView.context.dpToIntPx(64)
        val cornerRadius = containerView.context.dpToIntPx(8)
        val categorySize = containerView.context.dpToIntPx(40)

        Glide.with(containerView.context)
            .load(item.poster)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(posterSize)
            .into(iv_poster)

        Glide.with(containerView.context)
            .load(item.categoryIcon)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(categorySize)
            .into(iv_category)

        tv_date.text = item.date.format()
        tv_author.text = item.author
        tv_title.text = item.title
        tv_description.text = item.description
        tv_likes_count.text = "${item.likeCount}"
        tv_comments_count.text = "${item.commentCount}"
        tv_read_duration.text ="${item.readDuration} min read"

        itemView.setOnClickListener{ listener(item) }

 */