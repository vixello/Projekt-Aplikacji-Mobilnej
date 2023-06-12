package myapp.musicmastery.ui.Goal

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.databinding.ItemViewBinding
import java.text.SimpleDateFormat

class GoalAdapter(val onItemClick: (Int, Goal) -> Unit,
                  val onEditClick: (Int, Goal) -> Unit,
                  val onDeleteClick: (Int, Goal) -> Unit)
: RecyclerView.Adapter<GoalAdapter.ViewHolder>()
{
    private var list: MutableList<Goal> = arrayListOf()
    val date = SimpleDateFormat(" dd MM yyyy")

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
        @SuppressLint("SetTextI18n")
        fun bind(item: Goal)
        {
            binding.goalName.text = item.name
            binding.goalDate.text = date.format(item.date)
            binding.goalText.apply {
                if(item.text.length > 120){
                    text = "${item.text.substring(0,120)}..."
                }
                else{
                    text = item.text
                }
            }
            binding.editButton.setOnClickListener { onEditClick.invoke(bindingAdapterPosition, item) }
            binding.deleteButton.setOnClickListener { onDeleteClick.invoke(bindingAdapterPosition , item) }
            binding.goalText.setOnClickListener { onItemClick.invoke(bindingAdapterPosition , item) }
        }
//        fun bind(item: Goal)
//        {
//            binding.goalId.text = item.id
//            binding.goalName.text = item.name
//            binding.goalText.text = item.text
//            binding.editButton.setOnClickListener { onEditClick.invoke(bindingAdapterPosition, item) }
//            binding.deleteButton.setOnClickListener { onDeleteClick.invoke(bindingAdapterPosition , item) }
//        }
    }
}