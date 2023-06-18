package myapp.musicmastery.ui.Recording

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import myapp.musicmastery.data.model.Recording
import myapp.musicmastery.databinding.ItemRecViewBinding
import java.text.SimpleDateFormat
import java.util.*

var isVisible = true

class RecordingAdapter(val onItemClick: (Int, Recording) -> Unit,
                  val onEditClick: (Int, Recording) -> Unit,
                  val onDeleteClick: (Int, Recording) -> Unit)
: RecyclerView.Adapter<RecordingAdapter.ViewHolder>()
{
    private var list: MutableList<Recording> = arrayListOf()
    val date = SimpleDateFormat(" dd MM yyyy")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemRecView = ItemRecViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemRecView)
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
    fun getDayOfWeek(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Create a SimpleDateFormat with the desired output format
        val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())

        // Format the day of the week
        return dateFormat.format(date)
    }
    override fun getItemCount(): Int {
        return list.size
    }
    inner class ViewHolder(val binding: ItemRecViewBinding): RecyclerView.ViewHolder(binding.root)
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
            binding.recordingViewName.text = item.name
            binding.dayOfWeek.text = getDayOfWeek(item.date)
            binding.recordingViewDate.text = date.format(item.date)
            binding.durationTime.text = item.duration
            binding.editButton.setOnClickListener { onEditClick.invoke(bindingAdapterPosition, item) }
            binding.deleteButton.setOnClickListener { onDeleteClick.invoke(bindingAdapterPosition , item) }
            binding.itemRecView.setOnClickListener { onItemClick.invoke(bindingAdapterPosition , item) }
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