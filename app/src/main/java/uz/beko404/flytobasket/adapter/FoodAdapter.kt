package uz.beko404.flytobasket.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import uz.beko404.flytobasket.databinding.ItemBinding
import uz.beko404.flytobasket.model.Food

class FoodAdapter : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {
    var listener: ((ShapeableImageView) -> Unit)? = null
    private val dif = AsyncListDiffer(this, ITEM_DIFF)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dif.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    inner class ViewHolder(private val binding: ItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) = with(binding) {
            val data = dif.currentList[position]
            name.text = data.name
            description.text = data.description
            price.text = data.price
            image.setImageResource(data.image)
            imageCopy.setImageResource(data.image)
            root.setOnClickListener {
                listener?.invoke(imageCopy)
            }
        }
    }

    fun submitList(food: List<Food>) {
        dif.submitList(food)
    }

    companion object {
        private val ITEM_DIFF = object : DiffUtil.ItemCallback<Food>() {
            override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean =
                oldItem.id == newItem.id && oldItem.name == newItem.name
        }
    }
}