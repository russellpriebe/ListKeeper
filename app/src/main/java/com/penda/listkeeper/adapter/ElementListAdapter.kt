package com.penda.listkeeper.adapter


import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.penda.listkeeper.R
import com.penda.listkeeper.datamodel.ListElement
import com.penda.listkeeper.datamodel.OrdinalListElement
import com.penda.listkeeper.viewmodel.ElementViewModel
import kotlinx.android.synthetic.main.recycler_list_cardview.view.*

class ElementListAdapter(viewModel: ElementViewModel, val context: Context?) : androidx.recyclerview.widget.RecyclerView.Adapter<ElementListAdapter.ViewHolder>(){

    var elementList = ArrayList<ListElement>()
    private var localViewModel = viewModel
    private val dragAdapter: DragAdapter
    val helper: ItemTouchHelper
    private lateinit var mElementsList: List<ListElement>
    var isEdit = MutableLiveData<String>()
    var isComplete = MutableLiveData<OrdinalListElement>()
    var restored = MutableLiveData<OrdinalListElement>()
    var dragComplete = MutableLiveData<ArrayList<ListElement>>()

    init {
        dragAdapter = DragAdapter(this, ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT), dragComplete)
        val callback = dragAdapter
        helper = ItemTouchHelper(callback)
    }

    override fun getItemCount(): Int {
        return elementList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_list_element, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = elementList[position].elementValue
        holder.card.setOnClickListener {
            val str = holder.name.text.toString()
            Log.d("holder name", str)
            isEdit.postValue ( str )
        }
        holder.checkOffList.setOnClickListener {
            elementList[position].elementState = "inactive"
            setInactive(position, holder)
            isComplete.postValue(OrdinalListElement(elementList[position], position))
        }
        holder.edit.setOnClickListener {
            val str = holder.name.text.toString()
            Log.d("holder name", str)
            isEdit.postValue ( str )
        }
        holder.restore.setOnClickListener {
            elementList[position].elementState = "active"
            setActive(position, holder)
            restored.postValue(OrdinalListElement(elementList[position], position))
        }
        if(elementList[position].elementState==("inactive")){
            context?.let{
                holder.card.setCardBackgroundColor(context.resources.getColor(R.color.light_gray))
                holder.name.paintFlags = ( Paint.STRIKE_THRU_TEXT_FLAG )
            }
        }
        if (elementList[position].elementState==("inactive")){
            setInactive(position, holder)
        } else {
            setActive(position, holder)
        }
    }

    private fun setInactive(position: Int, holder: ViewHolder) {
        context?.let{
            holder.card.setCardBackgroundColor(context.resources.getColor(R.color.light_gray))
            holder.name.paintFlags = ( Paint.STRIKE_THRU_TEXT_FLAG )
            holder.checkOffList.visibility = View.GONE
            holder.edit.visibility = View.GONE
            holder.restore.visibility = View.VISIBLE
        }
    }

    private fun setActive(position: Int, holder: ViewHolder) {
        context?.let{
            holder.card.setCardBackgroundColor(context.resources.getColor(R.color.white))
            holder.name.paintFlags = 0
            holder.checkOffList.visibility = View.VISIBLE
            holder.edit.visibility = View.VISIBLE
            holder.restore.visibility = View.GONE
        }
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view){
        val name = view.element_item_name
        val card = view.element_cardview
        val checkOffList = view.checkoff
        val edit = view.edit_element
        val restore = view.restore_element
    }

    fun onMovedSub(fromPosition: Int, toPosition: Int){
        val cardA = elementList.get(fromPosition)
        val cardB = elementList.get(toPosition)
        elementList.set(fromPosition, cardB)
        elementList.set(toPosition, cardA)
        notifyItemMoved(fromPosition, toPosition)
    }


    fun removeItem(position: Int){
        localViewModel.deleteElement(elementList[position])
        elementList.removeAt(position)

        notifyDataSetChanged()
    }

    fun setElementList(list: List<ListElement>){
        mElementsList = list
        elementList.clear()
        for (element in mElementsList) {
            elementList.add(ListElement(element.listTag, element.elementValue, element.elementState, 1))
        }
        notifyDataSetChanged()
    }

    fun getNormalElementList(): ArrayList<ListElement> {
        return elementList
    }


    class DragAdapter(adapter: ElementListAdapter, dragDirs: Int, swipeDirs: Int, flag: MutableLiveData<ArrayList<ListElement>>) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs)
    {
        private var cardAdapter = adapter
        private var mFlag = flag

        override fun onSwiped(p0: androidx.recyclerview.widget.RecyclerView.ViewHolder, p1: Int) {
            cardAdapter.removeItem(p0.adapterPosition)
        }

        override fun onMove(p0: androidx.recyclerview.widget.RecyclerView, p1: androidx.recyclerview.widget.RecyclerView.ViewHolder, p2: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
            cardAdapter.onMovedSub(p1.adapterPosition, p2.adapterPosition)
            return true
        }

        override fun clearView(recyclerView: androidx.recyclerview.widget.RecyclerView, viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            val localCopy = cardAdapter.elementList
            mFlag.postValue(localCopy)
        }
    }


}
