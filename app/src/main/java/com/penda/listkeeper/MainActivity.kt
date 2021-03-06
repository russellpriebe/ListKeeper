package com.penda.listkeeper

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.penda.listkeeper.adapter.CardListAdapter
import com.penda.listkeeper.repository.ListRepository
import com.penda.listkeeper.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ListViewModel
    private lateinit var mListRepository: ListRepository
    private lateinit var adapter: CardListAdapter
    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101
    private val context: Context = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.title = resources.getString(R.string.title)

        setUpRecyclerView(context)
        checkPermissions()
    }


    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), MY_PERMISSIONS_REQUEST_RECORD_AUDIO)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_RECORD_AUDIO -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Utilities.putPrefs(true, "speechenabled", context)
                } else {
                    Toast.makeText(this, "Speech Input will be disabled without this permission", Toast.LENGTH_LONG).show()
                    Utilities.putPrefs(false, "speechenabled", context)
                }
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }
    private fun setUpRecyclerView(context: Context) {
        val db = ListRoomDatabase.getDatabase(context)
        mListRepository = ListRepository(db)
        viewModel = ViewModelProviders
            .of(this, ListViewModel.FACTORY(mListRepository))
            .get(ListViewModel::class.java)

        list_recycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        adapter = CardListAdapter(viewModel, context)
        list_recycler.adapter = adapter
        adapter.helper.attachToRecyclerView(list_recycler)
        viewModel.getListForAdapter()
        viewModel.cardsList.observe(this, Observer {
            it?.let { mList ->
                adapter.setCardList(mList)
            }
        })
        viewModel.shareElements.observe(this, Observer {
            it?.let{ shareBundle ->
                Utilities.buildShareIntent(shareBundle.list, shareBundle.elements, context)
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
