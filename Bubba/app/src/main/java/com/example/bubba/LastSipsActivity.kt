package com.example.bubba

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.graphics.drawable.Drawable
import android.content.res.Resources
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LastSipsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BlendAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val blendList = mutableListOf<BlendData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last_sips)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Resize navigation icon
        val navIcon: Drawable? = toolbar.navigationIcon
        navIcon?.setBounds(0, 0, 48.dpToPx(), 48.dpToPx())
        toolbar.navigationIcon = navIcon

        // RecyclerView setup
        recyclerView = findViewById(R.id.blendRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Load data
        sharedPreferences = getSharedPreferences("BlendPrefs", MODE_PRIVATE)
        loadBlendsFromPrefs()

        // Adapter with favorite callback
        adapter = BlendAdapter(blendList) { favoriteBlend ->
            saveFavoriteBlend(favoriteBlend)
        }
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.last_sips_menu, menu)
        for (i in 0 until menu.size()) {
            val icon = menu.getItem(i).icon
            icon?.setBounds(0, 0, 48.dpToPx(), 48.dpToPx())
            menu.getItem(i).icon = icon
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_history -> {
                clearHistory()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun clearHistory() {
        sharedPreferences.edit().remove("blend_history").apply()
        blendList.clear()
        adapter.notifyDataSetChanged()
    }

    private fun loadBlendsFromPrefs() {
        val blendJson = sharedPreferences.getString("blend_history", null)

        if (!blendJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<BlendData>>() {}.type
            val loadedBlends: List<BlendData> = Gson().fromJson(blendJson, type)

            // Ensure imageResId is resolved again (IDs aren't serialized properly)
            val updatedBlends = loadedBlends.map {
                val imageResId = resources.getIdentifier(it.flavor.lowercase(), "drawable", packageName)
                    .takeIf { id -> id != 0 } ?: R.drawable.pfpimageplaceholder
                it.copy(imageResId = imageResId)
            }

            blendList.addAll(updatedBlends)
        }
    }

    private fun saveFavoriteBlend(blend: BlendData) {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        with(prefs.edit()) {
            putString("favorite_flavor", blend.flavor)
            putString("favorite_size", blend.size)
            putString("favorite_sugar", blend.sugar)
            putString("favorite_ice", blend.ice)
            putStringSet("favorite_addons", blend.addOns.toSet())
            putInt("favorite_image", blend.imageResId)
            apply()
        }
    }

    // Extension function to convert dp to px
    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}
