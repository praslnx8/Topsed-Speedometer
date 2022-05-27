package com.bike.race.customs.dynamicAdapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class DynamicAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val contentList = mutableListOf<DynamicContent>()

    fun updateData(contentList: List<DynamicContent>) {
        val result = DiffUtil.calculateDiff(DynamicContentDiffUtil(this.contentList, contentList))
        this.contentList.clear()
        this.contentList.addAll(contentList)
        result.dispatchUpdatesTo(this)
    }

    fun getData(position: Int) = contentList[position]

    override fun getItemCount() = contentList.size

    override fun getItemViewType(position: Int): Int {
        return contentList[position].viewType
    }

    class DynamicContentDiffUtil(
        private val oldList: List<DynamicContent>,
        private val newList: List<DynamicContent>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldContentType = oldList.getOrNull(oldItemPosition)?.viewType
            val newContentType = newList.getOrNull(oldItemPosition)?.viewType

            return oldContentType == newContentType
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldContent = oldList.getOrNull(oldItemPosition)?.content
            val newContent = newList.getOrNull(oldItemPosition)?.content
            return oldContent.hashCode() == newContent.hashCode()
        }
    }
}