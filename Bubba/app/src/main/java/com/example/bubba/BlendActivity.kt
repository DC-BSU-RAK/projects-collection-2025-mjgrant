package com.example.bubba

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.example.bubba.databinding.ActivityBlendBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.yuyakaido.android.cardstackview.*

class BlendActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlendBinding
    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: BlendCardAdapter

    private enum class Category { FLAVOR, SIZE, SUGAR, ICE, ADDONS }
    private var currentCategory = Category.FLAVOR
    private var hasChosen = false

    // Store user selections
    private var selectedFlavor: String? = null
    private var selectedSize: String? = null
    private var selectedSugar: String? = null
    private var selectedIce: String? = null
    private val selectedAddOns = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCardStack()
    }

    private fun setupCardStack() {
        manager = CardStackLayoutManager(this, object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {}

            override fun onCardSwiped(direction: Direction?) {
                val position = manager.topPosition - 1
                val card = adapter.getItems().getOrNull(position)

                if (direction == Direction.Right && card != null) {
                    when (currentCategory) {
                        Category.FLAVOR -> {
                            if (!hasChosen) {
                                selectedFlavor = card.title
                                hasChosen = true
                                loadNextCategory(Category.SIZE)
                            }
                        }
                        Category.SIZE -> {
                            if (!hasChosen) {
                                selectedSize = card.title
                                hasChosen = true
                                loadNextCategory(Category.SUGAR)
                            }
                        }
                        Category.SUGAR -> {
                            if (!hasChosen) {
                                selectedSugar = card.title
                                hasChosen = true
                                loadNextCategory(Category.ICE)
                            }
                        }
                        Category.ICE -> {
                            if (!hasChosen) {
                                selectedIce = card.title
                                hasChosen = true
                                loadNextCategory(Category.ADDONS)
                            }
                        }
                        Category.ADDONS -> {
                            if (!selectedAddOns.contains(card.title)) {
                                selectedAddOns.add(card.title)
                            }
                        }
                    }
                }

                // End and show result after last card of ADDONS
                if (currentCategory == Category.ADDONS &&
                    manager.topPosition >= adapter.itemCount
                ) {
                    showFinalResult()
                }
            }

            override fun onCardRewound() {}
            override fun onCardCanceled() {}
            override fun onCardAppeared(view: View?, position: Int) {
                val title = view?.findViewById<TextView>(R.id.item_title)?.text
                Log.d("BlendActivity", "Card appeared: $title")
            }

            override fun onCardDisappeared(view: View?, position: Int) {
                val title = view?.findViewById<TextView>(R.id.item_title)?.text
                Log.d("BlendActivity", "Card disappeared: $title")
            }
        })

        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.FREEDOM)
        manager.setCanScrollHorizontal(true)
        manager.setSwipeableMethod(SwipeableMethod.Manual)
        manager.setOverlayInterpolator(LinearInterpolator())

        adapter = BlendCardAdapter(getFlavorCards())
        binding.cardStackView.layoutManager = manager
        binding.cardStackView.adapter = adapter
        binding.cardStackView.itemAnimator = DefaultItemAnimator()
    }

    private fun loadNextCategory(next: Category) {
        currentCategory = next
        hasChosen = false

        val nextCards = when (next) {
            Category.SIZE -> getSizeCards()
            Category.SUGAR -> getSugarLevelCards()
            Category.ICE -> getIceLevelCards()
            Category.ADDONS -> getAddOnCards()
            else -> emptyList()
        }

        // Start fade-out animation (1 second)
        binding.cardStackContainer.animate()
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                // Update cards after fade-out
                val oldList = adapter.getItems()
                val callback = CardStackCallback(oldList, nextCards)
                val result = DiffUtil.calculateDiff(callback)
                adapter.updateItems(nextCards)
                result.dispatchUpdatesTo(adapter)
                manager.scrollToPosition(0)

                // Start fade-in animation (1 second)
                binding.cardStackContainer.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .start()
            }
            .start()
    }


    private fun showFinalResult() {
        val dialogView = layoutInflater.inflate(R.layout.popup_blend_summary, null)

        val imageView = dialogView.findViewById<ImageView>(R.id.summaryImage)
        val textView = dialogView.findViewById<TextView>(R.id.summaryText)
        val redoButton = dialogView.findViewById<Button>(R.id.redoButton)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)

        // Set the flavor image (or fallback)
        val flavorImageRes = resources.getIdentifier(selectedFlavor?.lowercase() ?: "", "drawable", packageName)
        imageView.setImageResource(if (flavorImageRes != 0) flavorImageRes else R.drawable.pfpimageplaceholder)

        // Set summary text
        val summary = "A ${selectedSize ?: "unknown"} ${selectedFlavor ?: "unknown"} boba tea with " +
                "${selectedSugar ?: "unknown"}, ${selectedIce ?: "unknown"}, and " +
                if (selectedAddOns.isNotEmpty()) selectedAddOns.joinToString(", ") else "no add-ons."
        textView.text = summary

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialog.show()


        // Redo blend
        redoButton.setOnClickListener {
            dialog.dismiss()
            recreate() // Restart activity
        }

        // Confirm blend
        // Confirm blend
        confirmButton.setOnClickListener {
            val blend = BlendData(
                flavor = selectedFlavor ?: "Unknown",
                size = selectedSize ?: "Unknown",
                sugar = selectedSugar ?: "Unknown",
                ice = selectedIce ?: "Unknown",
                addOns = selectedAddOns.ifEmpty { listOf("None") },
                imageResId = R.drawable.pfpimageplaceholder
            )

            val sharedPreferences = getSharedPreferences("BlendPrefs", MODE_PRIVATE)
            val gson = Gson()

            val existingJson = sharedPreferences.getString("blend_history", null)
            val type = object : com.google.gson.reflect.TypeToken<MutableList<BlendData>>() {}.type
            val blendList: MutableList<BlendData> = if (!existingJson.isNullOrEmpty()) {
                gson.fromJson(existingJson, type)
            } else {
                mutableListOf()
            }

            blendList.add(blend)

            val updatedJson = gson.toJson(blendList)
            sharedPreferences.edit().putString("blend_history", updatedJson).apply()

            Log.d("BlendActivity", "Blend saved: $blend")
            dialog.dismiss()
            finish() // Optionally navigate to LastSipsActivity here
        }



    }


    private fun getFlavorCards(): List<BlendCard> {
        val flavorNames = listOf(
            "strawberry", "matcha", "chocolate", "bubblegum", "caramel",
            "wintermelon", "cherry", "mango", "peach", "mint", "okinawa", "taro"
        ).shuffled() // <--- Shuffle here

        return flavorNames.map {
            val resId = resources.getIdentifier(it, "drawable", packageName)
            BlendCard(resId, it.replaceFirstChar { c -> c.uppercase() }, "A delicious $it flavor.")
        }
    }


    private fun getSizeCards(): List<BlendCard> {
        val sizes = listOf("Small", "Medium", "Large")
        return sizes.map {
            val resId = resources.getIdentifier(it.lowercase(), "drawable", packageName)
            BlendCard(resId, it, "Choose $it size.")
        }
    }

    private fun getSugarLevelCards(): List<BlendCard> {
        val sugarLevels = listOf("Extra Sugar", "Regular Sugar", "Less Sugar", "No Sugar")
        return sugarLevels.map {
            val key = it.lowercase().replace(" ", "") // matches e.g., "extrasugar"
            val resId = resources.getIdentifier(key, "drawable", packageName)
            BlendCard(resId, it, "Sweetness: $it.")
        }
    }

    private fun getIceLevelCards(): List<BlendCard> {
        val iceLevels = listOf("Extra Ice", "Regular Ice", "Less Ice", "No Ice")
        return iceLevels.map {
            val key = it.lowercase().replace(" ", "") // matches e.g., "regularice"
            val resId = resources.getIdentifier(key, "drawable", packageName)
            BlendCard(resId, it, "Ice level: $it.")
        }
    }

    private fun getAddOnCards(): List<BlendCard> {
        val addOns = listOf("Extra Pearls", "Coffee Jelly", "Nata de Coco", "Cream Cheese", "Crushed Oreos")
        return addOns.map {
            val key = it.lowercase().replace(" ", "") // matches e.g., "creamcheese"
            val resId = resources.getIdentifier(key, "drawable", packageName)
            BlendCard(resId, it, "Add-on: $it.")
        }
    }

}
