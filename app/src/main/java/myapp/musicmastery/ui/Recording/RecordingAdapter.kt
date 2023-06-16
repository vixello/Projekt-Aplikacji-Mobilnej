package myapp.musicmastery.ui.Recording

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import myapp.musicmastery.data.model.Goal
import myapp.musicmastery.data.model.Recording
import myapp.musicmastery.databinding.ItemViewBinding
import java.text.SimpleDateFormat

var isVisible = true

class RecordingAdapter(val onItemClick: (Int, Recording) -> Unit,
                  val onEditClick: (Int, Recording) -> Unit,
                  val onDeleteClick: (Int, Recording) -> Unit)
: RecyclerView.Adapter<RecordingAdapter.ViewHolder>()
{
    private var list: MutableList<Recording> = arrayListOf()
    val date = SimpleDateFormat(" dd MM yyyy")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]
        holder.bind(item)
    }
    fun updateList(list: MutableList<Recording>)
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
        fun bind(item: Recording)
        {

            if(isVisible){
                binding.editButton.visibility =  View.VISIBLE
                binding.deleteButton.visibility = View.VISIBLE
            }else{
                binding.editButton.visibility =  View.INVISIBLE
                binding.deleteButton.visibility = View.INVISIBLE
            }
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
    fun setVisibility(visibility: Boolean) {
        isVisible = visibility
        this.notifyDataSetChanged()
    }
}