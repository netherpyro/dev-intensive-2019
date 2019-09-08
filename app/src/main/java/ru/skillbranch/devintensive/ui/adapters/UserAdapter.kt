package ru.skillbranch.devintensive.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_user_list.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.UserItem

/**
 * @author mmikhailov on 2019-09-05.
 */
class UserAdapter(val listener: (UserItem) -> Unit) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    var items: List<UserItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val convertView = inflater.inflate(R.layout.item_user_list, parent, false)

        return UserViewHolder(convertView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    fun updateData(data: List<UserItem>) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                items[oldItemPosition].id == data[newItemPosition].id

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                items[oldItemPosition].hashCode() == data[newItemPosition].hashCode()

            override fun getOldListSize() = items.size
            override fun getNewListSize() = data.size
        }).apply {
            items = data
            dispatchUpdatesTo(this@UserAdapter)
        }
    }

    inner class UserViewHolder(convertView: View) : RecyclerView.ViewHolder(convertView),
        LayoutContainer {
        override val containerView: View?
            get() = itemView

        fun bind(user: UserItem, listener: (UserItem) -> Unit) {
            if (user.avatar != null) {
                Glide.with(itemView)
                    .load(user.avatar)
                    .into(iv_avatar_user)
            } else {
                Glide.with(itemView)
                    .clear(iv_avatar_user)
                iv_avatar_user.setInitialsAvatar(user.initials ?: "??")
            }

            sv_indicator.visibility = if (user.isOnline) View.VISIBLE else View.GONE
            tv_user_name.text = user.fullName
            tv_last_activity.text = user.lastActivity
            iv_selected.visibility = if (user.isSelected) View.VISIBLE else View.GONE
            itemView.setOnClickListener { listener(user) }
        }
    }
}