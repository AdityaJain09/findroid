package dev.jdtech.jellyfin.adapters

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R as MaterialR
import dev.jdtech.jellyfin.core.R as CoreR
import dev.jdtech.jellyfin.databinding.EpisodeItemBinding
import dev.jdtech.jellyfin.databinding.SeasonButtonsBinding
import dev.jdtech.jellyfin.databinding.SeasonHeaderBinding
import dev.jdtech.jellyfin.models.EpisodeItem
import dev.jdtech.jellyfin.models.FindroidEpisode
import dev.jdtech.jellyfin.models.isDownloaded
import dev.jdtech.jellyfin.utils.setTintColor
import dev.jdtech.jellyfin.utils.setTintColorAttribute

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_EPISODE = 1
private const val ITEM_VIEW_TYPE_BUTTONS = 2

class EpisodeListAdapter(
    private val onClickListener: OnClickListener,
    private val onPlayClickListener: OnButtonClickListener,
    private val onCheckClickListener: OnButtonClickListener,
    private val onFavoriteClickListener: OnButtonClickListener,
) :
    ListAdapter<EpisodeItem, RecyclerView.ViewHolder>(DiffCallback) {

    class HeaderViewHolder(private var binding: SeasonHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: EpisodeItem.Header) {
            binding.seriesId = header.seriesId
            binding.seasonId = header.seasonId
            binding.seasonName.text = header.seasonName
            binding.seriesName.text = header.seriesName
            binding.executePendingBindings()
        }
    }

    class ButtonsViewHolder(private var binding: SeasonButtonsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: EpisodeItem.Buttons,
            onPlayClickListener: OnButtonClickListener,
            onCheckClickListener: OnButtonClickListener,
            onFavoriteClickListener: OnButtonClickListener,
        ) {
            // Check icon
            when (item.isPlayed) {
                true -> binding.checkButton.setTintColor(CoreR.color.red, binding.root.context.theme)
                false -> binding.checkButton.setTintColorAttribute(
                    MaterialR.attr.colorOnSecondaryContainer,
                    binding.root.context.theme
                )
            }

            // Favorite icon
            val favoriteDrawable = when (item.isFavorite) {
                true -> CoreR.drawable.ic_heart_filled
                false -> CoreR.drawable.ic_heart
            }
            binding.favoriteButton.setImageResource(favoriteDrawable)
            when (item.isFavorite) {
                true -> binding.favoriteButton.setTintColor(CoreR.color.red, binding.root.context.theme)
                false -> binding.favoriteButton.setTintColorAttribute(MaterialR.attr.colorOnSecondaryContainer, binding.root.context.theme)
            }

            binding.playButton.setOnClickListener {
                binding.playButton.setImageResource(android.R.color.transparent)
                binding.progressCircular.isVisible = true
                onPlayClickListener.onClick()
            }
            binding.checkButton.setOnClickListener {
                onCheckClickListener.onClick()
            }
            binding.favoriteButton.setOnClickListener {
                onFavoriteClickListener.onClick()
            }
        }
    }

    class EpisodeViewHolder(private var binding: EpisodeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(episode: FindroidEpisode) {
            binding.episode = episode
            if (episode.playbackPositionTicks > 0) {
                binding.progressBar.layoutParams.width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    (episode.playbackPositionTicks.div(episode.runtimeTicks).times(.84)).toFloat(),
                    binding.progressBar.context.resources.displayMetrics
                ).toInt()
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }

            binding.downloadedIcon.isVisible = episode.isDownloaded()

            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<EpisodeItem>() {
        override fun areItemsTheSame(oldItem: EpisodeItem, newItem: EpisodeItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EpisodeItem, newItem: EpisodeItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> {
                HeaderViewHolder(
                    SeasonHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            ITEM_VIEW_TYPE_EPISODE -> {
                EpisodeViewHolder(
                    EpisodeItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            ITEM_VIEW_TYPE_BUTTONS -> {
                ButtonsViewHolder(
                    SeasonButtonsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ITEM_VIEW_TYPE_HEADER -> {
                val item = getItem(position) as EpisodeItem.Header
                (holder as HeaderViewHolder).bind(item)
            }
            ITEM_VIEW_TYPE_EPISODE -> {
                val item = getItem(position) as EpisodeItem.Episode
                holder.itemView.setOnClickListener {
                    onClickListener.onClick(item.episode)
                }
                (holder as EpisodeViewHolder).bind(item.episode)
            }
            ITEM_VIEW_TYPE_BUTTONS -> {
                val item = getItem(position) as EpisodeItem.Buttons
                (holder as ButtonsViewHolder).bind(
                    item,
                    onPlayClickListener,
                    onCheckClickListener,
                    onFavoriteClickListener,
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is EpisodeItem.Header -> ITEM_VIEW_TYPE_HEADER
            is EpisodeItem.Episode -> ITEM_VIEW_TYPE_EPISODE
            is EpisodeItem.Buttons -> ITEM_VIEW_TYPE_BUTTONS
        }
    }

    class OnClickListener(val clickListener: (item: FindroidEpisode) -> Unit) {
        fun onClick(item: FindroidEpisode) = clickListener(item)
    }

    class OnButtonClickListener(val clickListener: () -> Unit) {
        fun onClick() = clickListener()
    }
}
