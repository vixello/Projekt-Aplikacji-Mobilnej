package myapp.musicmastery.Goal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.databinding.ItemViewBinding

class GoalAdapter(val onItemClick: (Int, Goal) -> Unit, val onEditClick: (Int, Goal) -> Unit, val onDeleteClick: (Int, Goal) -> Unit)
: RecyclerView.Adapter<GoalAdapter.ViewHolder>()
{
    private var list: MutableList<Goal> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }
    fun updateList(list: MutableList<Goal>)
    {
        this.list = list
        notifyDataSetChanged()
    }
    fun deleteItem(position: Int)
    {
        list.removeAt(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    inner class ViewHolder(val binding: ItemViewBinding): RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item: Goal)
        {
            binding.goalId.text = item.id
            binding.goalText.text = item.text
            binding.editButton.setOnClickListener { onEditClick.invoke(adapterPosition, item) }
            binding.deleteButton.setOnClickListener { onDeleteClick.invoke(adapterPosition, item) }
        }
    }
}