package com.penda.listkeeper

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.penda.listkeeper.adapter.ElementListAdapter
import com.penda.listkeeper.datamodel.ListElement
import com.penda.listkeeper.datamodel.MList
import com.penda.listkeeper.repository.ElementRepository
import com.penda.listkeeper.viewmodel.ElementViewModel
import com.penda.listkeeper.viewmodel.VMProviderFactory
import kotlinx.android.synthetic.main.activity_create_list.*
import kotlinx.android.synthetic.main.add_call_notes.view.*
import kotlinx.android.synthetic.main.content_create_list.*

class CreateList : AppCompatActivity() {
    private val REQUEST_SPEECH_INPUT = 101
    private var newList = false
    private lateinit var viewModel: ElementViewModel
    private lateinit var speechToText: EditText
    private var tag: String? = null
    private lateinit var adapter: ElementListAdapter
    private lateinit var mElementRepository: ElementRepository
    private var supressRefresh = false
    private lateinit var barTitle: String
    private lateinit var mSpeechRecognizer : SpeechRecognizer
    private lateinit var mSpeechRecognizerIntent: Intent
    private lateinit var listener: SpeechRecognizerListener
    private lateinit var micButtonBlue: ImageButton
    private lateinit var micButtonRed: ImageButton
    private val context:Context = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_list)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        newList = intent.getBooleanExtra("newList",false)
        if(!newList){
            tag = intent.getStringExtra("tag")
            barTitle = intent.getStringExtra("list")
        } else {
            tag = getTag()
        }
        setUpRecyclerView(this)
        createRecognizer()
    }

    private fun createRecognizer(){
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        mSpeechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.packageName)
        listener = SpeechRecognizerListener(mSpeechRecognizer)
        mSpeechRecognizer.setRecognitionListener(listener)
        listener.mText.observe(this, Observer{ txt ->
            txt?.let {
                micButtonBlue.visibility = View.VISIBLE
                micButtonRed.clearAnimation()
                micButtonRed.visibility = View.GONE
                when(it){
                    "null" ->  Toast.makeText(context, "Missed that.. please try again", Toast.LENGTH_SHORT).show()
                    "error" -> { Toast.makeText(context, "Missed that.. please try again", Toast.LENGTH_SHORT).show()
                                 mSpeechRecognizer.destroy()
                                 createRecognizer()
                    }
                    else -> speechToText.append(" $it")

                }
            }
        })
    }



    private fun setUpRecyclerView(context: Context) {

        viewModel = ViewModelProviders.of(this, VMProviderFactory.viewModelFactory).get(ElementViewModel::class.java)
        val db = ListRoomDatabase.getDatabase(context)
        mElementRepository = ElementRepository(db, tag)
        viewModel.setRepository(mElementRepository, tag)

        element_recycler.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        adapter = ElementListAdapter(viewModel, context)
        element_recycler.adapter = adapter
        adapter.helper.attachToRecyclerView(element_recycler)

        viewModel.elementsList.observe(this, Observer{ mList ->
            mList?.let {
                adapter.setElementList(it)
            }
        })
        adapter.isEdit.observe(this, Observer {isEdit ->
            isEdit?.let {
                tag?.let {mTag ->
                    viewModel.getElementForEdit(isEdit, mTag)
                }
            }
        })
        viewModel.editElement.observe(this, Observer{element ->
            element?.let{
                speechToTextDialog(it.elementValue,false,true,false, it, -1).show()
                adapter.notifyDataSetChanged()
            }
        })
        adapter.isComplete.observe(this, Observer {element ->
            element?.let {
                val mElement = ListElement(it.element.listTag, it.element.elementValue, "inactive", 1)
                viewModel.handleComplete(mElement, it.element)
            }
        })
        adapter.restored.observe(this, Observer {element ->
            element?.let {
                val mElement = ListElement(it.element.listTag, it.element.elementValue, "active", 1)
                viewModel.handleRestore(mElement, it.element)
            }
        })
        adapter.dragComplete.observe(this, Observer {mList ->
            mList?.let {
                   tag?.let {mTag ->
                       viewModel.reorderList(mTag, mList)
                   }
            }
        })
    }

    override fun onResume(){
        super.onResume()
        if(newList) {
            toolbar.title = "Create List"
            speechToTextDialog("", false, false, false, null, 0).show()
            newList = false
        } else { toolbar.title = intent.getStringExtra("list") }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear()
        menuInflater.inflate(R.menu.menu_createlist, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            android.R.id.home -> { onBackPressed()
                true }
            R.id.action_add_list -> {
                speechToTextDialog("", true, false, false, null, 0 ).show()
                true
            }
            R.id.restore_list -> {
                val mTag = tag
                mTag?.let{
                    viewModel.restoreList(mTag)
                }
                true
            }
            R.id.action_share_list -> {
                val title = intent.getStringExtra("list")
                val date = intent.getStringExtra("date")
                val shareList = adapter.getNormalElementList()
                tag?.let {
                    val mList = MList(it, "none", title, date)
                    Utilities.buildShareIntent(mList, shareList, this)
                }
                return true
            }
            R.id.rename_list -> {
                speechToTextDialog(barTitle, false, false, true, null, 0 ).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onPause(){
        super.onPause()
        Log.d("leaving app", "onpause")
        if(!supressRefresh) {
            tag?.let {
                viewModel.reorderList(it, adapter.getNormalElementList())
            }
        }
    }

    private fun getTag(): String {
        val time = System.currentTimeMillis()
        return java.lang.Long.toString(time)
    }

    private fun speechToTextDialog(value: String, showAddElement: Boolean, isEdit: Boolean, isEditTitle: Boolean, element: ListElement?, pos: Int): AlertDialog {

        val builder = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
        val dialogView = layoutInflater.inflate(R.layout.add_call_notes, null)
        builder.setView(dialogView)
        micButtonBlue = dialogView.findViewById(R.id.speech_to_text) as ImageButton
        micButtonRed = dialogView.findViewById(R.id.speech_to_text_red) as ImageButton
        val fisheye = dialogView.findViewById<ImageView>(R.id.fisheye)
        speechToText = dialogView.notes
        val speechEnabled = Utilities.getPrefs("speechenabled", true, context).getBoolean("speechenabled")
        if(!speechEnabled){
            micButtonBlue.visibility = View.GONE
            micButtonRed.visibility = View.GONE
            fisheye.visibility = View.GONE
        }
        dialogView.speech_to_text.setOnClickListener {
            supressRefresh = true
            getSpeechInput()
        }
        speechToText.apply {
            setText(value)
            setSelection(speechToText.text.length)
            requestFocus()
        }

        val addElement = dialogView.add_item_in_dialog
        addElement.setOnClickListener {
            viewModel.handleElementAddition(speechToText.getText().toString(), tag)
            speechToText.setText("")
        }
        var title = "Enter List Name"

        when {
            showAddElement -> {title = "Add Item"
                }
            isEditTitle -> {title = "Edit List Name"
                addElement.visibility = View.GONE
                }
            isEdit -> {title = "Edit Item"
                addElement.visibility = View.GONE
                }
            else -> {
                title = "Enter List Name"
                addElement.visibility = View.GONE
            }
        }

        builder.setPositiveButton("SAVE") { dialog, _ ->
            dialog.dismiss()
            when {
                showAddElement -> {
                    viewModel.handleElementAddition(speechToText.getText().toString(), tag)
                }
                isEditTitle -> {
                    val listTitle = speechToText.getText().toString()
                    tag?.let{
                        viewModel.handleTitleUpdate(listTitle, tag!!)
                    }
                    toolbar.title = listTitle
                }
                isEdit -> {
                    tag?.let {
                        val mElement = ListElement(tag!!, speechToText.text.toString(), "active", 1)
                        element?.let {
                            viewModel.handleElementUpdate(mElement, element)
                        }
                    }
                }
                else -> {
                    barTitle = speechToText.getText().toString()
                    tag?.let {
                        viewModel.handleListAddition(barTitle, tag!!)
                    }
                    toolbar.title = barTitle
                }
            }
        }
                .setNegativeButton("CANCEL") { dialog, _ ->
                    dialog.dismiss()
                    when {
                        showAddElement -> {
                        }
                        isEditTitle -> {
                        }
                        isEdit -> { }
                        else -> {
                            onBackPressed()
                        }
                    }
                }

        builder.setTitle(title)
        val mDialog = builder.create()

        mDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        return mDialog
    }

    private fun getSpeechInput() {
        /*val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        try {
            startActivityForResult(intent, REQUEST_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            toast("Speech to Text not supported.")
        }*/
        micButtonBlue.visibility = View.GONE
        micButtonRed.visibility = View.VISIBLE
        micButtonRed.startAnimation(Utilities.animateMic())
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        supressRefresh = false
        Log.d("wtfwtf", "onactivityresult")
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SPEECH_INPUT) {
                if (null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    speechToText.append(" " + result[0])
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSpeechRecognizer.destroy()
    }
}
