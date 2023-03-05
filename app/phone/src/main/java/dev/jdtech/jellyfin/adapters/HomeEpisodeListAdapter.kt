package dev.jdtech.jellyfin.adapters

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.jdtech.jellyfin.R
import dev.jdtech.jellyfin.databinding.HomeEpisodeItemBinding
import dev.jdtech.jellyfin.models.FindroidEpisode
import dev.jdtech.jellyfin.models.FindroidItem
import dev.jdtech.jellyfin.models.FindroidMovie
import dev.jdtech.jellyfin.models.isDownloaded

class HomeEpisodeListAdapter(private val onClickListener: OnClickListener) : ListAdapter<FindroidItem, HomeEpisodeListAdapter.EpisodeViewHolder>(DiffCallback) {
    class EpisodeViewHolder(
        private var binding: HomeEpisodeItemBinding,
        private val parent: ViewGroup
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FindroidItem) {
            binding.item = item
            if (item.playedPercentage != null) {
                binding.progressBar.layoutParams.width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    (item.playedPercentage?.times(2.24))!!.toFloat(), binding.progressBar.context.resources.displayMetrics
                ).toInt()
                binding.progressBar.visibility = View.VISIBLE
            }

            binding.downloadedIcon.isVisible = item.isDownloaded()

            when (item) {
                is FindroidMovie -> {
                    binding.primaryName.text = item.name
                    binding.secondaryName.visibility = View.GONE
                }
                is FindroidEpisode -> {
                    binding.primaryName.text = item.seriesName
                    binding.secondaryName.text = parent.resources.getString(R.string.episode_name_extended, item.parentIndexNumber, item.indexNumber, item.name)
                }
            }

            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<FindroidItem>() {
        override fun areItemsTheSame(oldItem: FindroidItem, newItem: FindroidItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FindroidItem, newItem: FindroidItem): Boolean {
            return oldItem.name == newItem.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        return EpisodeViewHolder(
            HomeEpisodeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            parent
        )
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(item)
        }
        holder.bind(item)
    }

    class OnClickListener(val clickListener: (item: FindroidItem) -> Unit) {
        fun onClick(item: FindroidItem) = clickListener(item)
    }
}
