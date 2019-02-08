package com.penda.listkeeper

import android.Manifest
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.penda.listkeeper.adapter.CardListAdapter
import com.penda.listkeeper.repository.ListRepository
import com.penda.listkeeper.viewmodel.ListViewModel
import com.penda.listkeeper.viewmodel.VMProviderFactory
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.content_main.*
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


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
        MobileAds.initialize(this, resources.getString(R.string.admobid))
        adView.loadAd(AdRequest.Builder().build())
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

        viewModel = ViewModelProviders.of(this, VMProviderFactory.viewModelFactory).get(ListViewModel::class.java)
        val db = ListRoomDatabase.getDatabase(context)
        mListRepository = ListRepository(db)
        viewModel.setRepository(mListRepository)

        list_recycler.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(context, RecyclerView.VERTICAL, false)
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
