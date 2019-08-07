package com.sid.nike.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.sid.nike.R
import com.sid.nike.db.dto.Definition
import com.sid.nike.utils.AppUtils
import com.sid.nike.viewmodel.MainViewModel

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.layout_definition_item.view.*

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    var adapter:DefinitionAdapter? = null
    var searchTerm:String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(definitions_toolbar)

        // Attach view model to the activity
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        var termsHistoryList = viewModel.getSearchHistory()
        if(termsHistoryList.isNotEmpty()){
            definitions_no_data_available.visibility = View.GONE
            termsHistoryList.forEach{
                addToTabLayout(it)
            }
            viewModel.load(termsHistoryList[0])
        } else {
            definitions_no_data_available.visibility = View.VISIBLE
        }

        // Create adapter for the definitions_recycler_view
        adapter = DefinitionAdapter(this, mutableListOf())

        // Todo: Have to add the translate animations to all the recycler view items
        // var controller = AnimationUtils.loadLayoutAnimation(this, R.anim.slide_in)
        // definitions_recycler_view.layoutAnimation = controller

        // Set layout manager, adapter to the recycler view
        definitions_recycler_view.layoutManager = LinearLayoutManager(this)
        definitions_recycler_view.adapter = adapter
        definitions_tab_layout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {}

            override fun onTabUnselected(p0: TabLayout.Tab?) {}

            override fun onTabSelected(selectedTab: TabLayout.Tab?) {
                viewModel.load(selectedTab?.text.toString())
            }
        })

        // Observe for the network live data here
        viewModel.definitionsLiveData.observe(this, Observer {
            definitions_progress_bar.visibility = View.GONE
            if(it.size > 0){
                adapter?.updateDefinitions(it.toMutableList())
                definitions_no_data_available.visibility = View.GONE

                // Add item to the tab layout.
                addToTabLayout(searchTerm!!)
                viewModel.addToSearchHistory(searchTerm!!)
            } else {
//                adapter?.updateDefinitions(mutableListOf())
//                definitions_no_data_available.visibility = View.VISIBLE

                Snackbar.make(definitions_parent,String.format(getString(R.string.cannot_find_term),searchTerm),Snackbar.LENGTH_SHORT).show()
            }
            searchTerm = ""
        })

        // Observe for the local database live data here.
        viewModel.definitionsLocalLiveData.observe(this, Observer {
            definitions_progress_bar.visibility = View.GONE
            if(it.size > 0){
                adapter?.updateDefinitions(it.toMutableList())
                definitions_no_data_available.visibility = View.GONE

                // Add item to the tab layout.
                addToTabLayout(searchTerm!!)
            } else {
//                adapter?.updateDefinitions(mutableListOf())
//                definitions_no_data_available.visibility = View.VISIBLE

                Snackbar.make(definitions_parent,String.format(getString(R.string.cannot_find_term),searchTerm),Snackbar.LENGTH_SHORT).show()
            }
            searchTerm = ""
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        // Get the instance of search view and its respective edittext to get the user entered text.
        var searchItem = menu.findItem(R.id.menu_main_search)
        var searchView = searchItem?.actionView as SearchView

        var searchEditText = searchView.findViewById<EditText>(R.id.search_src_text)
        searchEditText.setHintTextColor(Color.BLACK)
        searchEditText.setTextColor(Color.BLACK)
        searchEditText.setHint(R.string.action_search_hint)

        searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Get user entered data and persist in searchTerm variable to use later.
                searchTerm = query?: ""

                // Request viewmodel to pull user requested Definition
                viewModel.search(searchTerm!!)

                // Close the search view.
                searchItem.collapseActionView()
                definitions_progress_bar.visibility = View.VISIBLE
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        return true
    }

    /**
     * Local Function(s)
     */
    // This function is responsible for creating tab item (with user entered text) and it to tab layout.
    private fun addToTabLayout(searchText:String){
        if(!searchText.isEmpty()) {
            var tabItem = definitions_tab_layout.newTab()
            tabItem.text = searchText
            definitions_tab_layout.addTab(tabItem,0)
            tabItem.select()
        }
    }

    /**
     * Inner classes
     */
    class DefinitionViewHolder(var view:View) : RecyclerView.ViewHolder(view) {
        fun setData(definition: Definition){
            view.definition_item_meaning.text = definition.definition
            view.definition_item_example.text = definition.example
            view.definition_item_thumbs_up.text = definition.thumbsUp.toString()
            view.definition_item_thumbs_down.text = definition.thumbsDown.toString()
            view.definition_item_date.text = AppUtils.convertToGeneralDateFormat(definition.writtenOn)
        }
    }

    class DefinitionAdapter(var context: Context,var definitionsList:MutableList<Definition>): RecyclerView.Adapter<DefinitionViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefinitionViewHolder {
            var rootView = LayoutInflater.from(context).inflate(R.layout.layout_definition_item,parent,false)
            return DefinitionViewHolder(rootView)
        }

        fun updateDefinitions(newDefinitionsList:MutableList<Definition>){
            definitionsList.clear()
            definitionsList.addAll(newDefinitionsList)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = definitionsList.size

        override fun onBindViewHolder(holder: DefinitionViewHolder, position: Int) {
            holder.setData(definitionsList.get(position))
        }
    }
}
