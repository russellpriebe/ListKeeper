package com.penda.mylistkeeper

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.penda.mylistkeeper.adapter.CardListAdapter
import com.penda.mylistkeeper.repository.ListRepository
import com.penda.mylistkeeper.viewmodel.ListViewModel
import com.penda.mylistkeeper.viewmodel.VMProviderFactory

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ListViewModel
    private lateinit var mListRepository: ListRepository
    private lateinit var adapter: CardListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.title = resources.getString(R.string.title)
        val context: Context = this

        setUpRecyclerView(context)

    }

    private fun setUpRecyclerView(context: Context) {

        viewModel = ViewModelProviders.of(this, VMProviderFactory.viewModelFactory).get(ListViewModel::class.java)
        val db = ListRoomDatabase.getDatabase(context)
        mListRepository = ListRepository(db)
        viewModel.setRepository(mListRepository)

        list_recycler.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        adapter = CardListAdapter(viewModel, context)
        list_recycler.adapter = adapter
        adapter.helper.attachToRecyclerView(list_recycler)

        viewModel.cardsList.observe(this, Observer {
                            it?.let {
                                adapter.setCardList(it)
                            }
        })
        viewModel.shareElements.observe(this, Observer {
            it?.let{
                Utilities.buildShareIntent(it.list, it.elements, context)
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_add_list -> {intent = Intent(this, CreateList::class.java)
                    intent.putExtra("newList",true)
                    startActivity(intent)
                    true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause(){
        super.onPause()
        viewModel.reorderList(adapter.getNormalCardList())
    }
}
