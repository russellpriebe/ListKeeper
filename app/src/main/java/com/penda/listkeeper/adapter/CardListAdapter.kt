package com.penda.listkeeper.adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.penda.listkeeper.CreateList
import com.penda.listkeeper.viewmodel.ListViewModel
import com.penda.listkeeper.R
import com.penda.listkeeper.datamodel.MList
import kotlinx.android.synthetic.main.recycler_list_row.view.*

class CardListAdapter(val viewModel: ListViewModel, val context: Context?) : androidx.recyclerview.widget.RecyclerView.Adapter<CardListAdapter.ViewHolder>(){

    var cardList = ArrayList<MList>()
    lateinit var mList : List<MList>
    val dragAdapter: DragAdapter
    val helper: ItemTouchHelper

    init {
        dragAdapter = DragAdapter(this, ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT))
        val callback = dragAdapter
        helper = ItemTouchHelper(callback)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dueDate.text = cardList[position].listDate
        holder.name.text = cardList[position].listName
        holder.card.setOnClickListener { val intent = Intent(context, CreateList::class.java)
            intent.putExtra("list", cardList[position].listName)
            intent.putExtra("tag", cardList[position].listTag)
            intent.putExtra("date", cardList[position].listDate)
            context?.startActivity( intent)
        }
        holder.share.setOnClickListener {
            context?.let {
                viewModel.share(cardList[position])
            }
        }
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view){
        val dueDate = view.date_added
        val name = view.item_name
        val card = view.cardview
        val share = view.share
    }

    fun onMovedSub(fromPosition: Int, toPosition: Int){
        val cardA = cardList.get(fromPosition)
        val cardB = cardList.get(toPosition)
        cardList.set(fromPosition, cardB)
        cardList.set(toPosition, cardA)
        notifyItemMoved(fromPosition, toPosition)
    }


    fun removeItem(position: Int){
        cardList.removeAt(position)
        viewModel.deleteList(mList[position])
    }

    fun setCardList(list: List<MList>){
        mList = list
        val len = mList.size
        cardList.clear()
        for (list in mList) {
            cardList.add(MList(list.listTag, list.listType, list.listName, list.listDate))
        }
        notifyDataSetChanged()
    }

    fun getNormalCardList(): ArrayList<MList>{
        return cardList
    }

    class DragAdapter(adapter: CardListAdapter, dragDirs: Int, swipeDirs: Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs)
    {
        var cardAdapter = adapter

        override fun onSwiped(p0: androidx.recyclerview.widget.RecyclerView.ViewHolder, p1: Int) {
            cardAdapter.removeItem(p0.adapterPosition)
        }

        override fun onMove(p0: androidx.recyclerview.widget.RecyclerView, p1: androidx.recyclerview.widget.RecyclerView.ViewHolder, p2: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
            cardAdapter.onMovedSub(p1.adapterPosition, p2.adapterPosition)
            return true
        }

    }

}
