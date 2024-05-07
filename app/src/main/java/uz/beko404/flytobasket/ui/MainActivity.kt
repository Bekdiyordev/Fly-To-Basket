package uz.beko404.flytobasket.ui

import android.animation.Animator
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import uz.beko404.flytobasket.R
import uz.beko404.flytobasket.adapter.FoodAdapter
import uz.beko404.flytobasket.databinding.ActivityMainBinding
import uz.beko404.flytobasket.model.Food
import uz.beko404.flytobasket.utill.CircleAnimationUtil
import uz.beko404.flytobasket.utill.viewBinding

class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val adapter by lazy { FoodAdapter() }
    private val dataList = mutableListOf<Food>()
    private var itemCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loadData()
        setupUI()
    }

    private fun loadData() {
        dataList.clear()
        dataList.add(Food(1, "Big Burger", "Mol go'shtli", "33 000 so'm", R.drawable.img))
        dataList.add(Food(2, "Lavash", "Pishloqli", "30 000 so'm", R.drawable.img_1))
        dataList.add(Food(3, "Shourma", "Katta", "28 000 so'm", R.drawable.img_2))
        dataList.add(Food(4, "Pitsa", "Assorti", "50 000 so'm", R.drawable.img_3))
        dataList.add(Food(5, "KFC", "Standart", "25 000 so'm", R.drawable.img_5))
        dataList.add(Food(6, "Hot Dog", "Katta(2x)", "24 000 so'm", R.drawable.img_4))
        dataList.add(Food(7, "Sharbat", "Bliss", "15 000 so'm", R.drawable.img_6))
        dataList.add(Food(8, "Maxito", "Bankali", "12 000 so'm", R.drawable.img_7))
        adapter.submitList(dataList)
    }

    private fun setupUI() = with(binding) {
        foodRecycler.adapter = adapter
        adapter.listener = {
            makeFlyAnimation(it)
        }
    }

    private fun addItemToCart() {
        binding.count.text = (++itemCounter).toString()
    }


    private fun makeFlyAnimation(targetView: ImageView) {
        CircleAnimationUtil().attachActivity(this).setTargetView(targetView).setMoveDuration(700)
            .setDestView(binding.destination).setAnimationListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    addItemToCart()
//                    Toast.makeText(this@MainActivity, "Continue Shopping...", Toast.LENGTH_SHORT).show()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).startAnimation()
    }
}