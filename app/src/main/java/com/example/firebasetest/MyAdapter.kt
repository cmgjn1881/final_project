package com.example.firebasetest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 아이템 데이터 클래스
data class Item(
    val title: String,
    val price: Int,
    val salesStatus: Boolean,
    val itemID: String
)

interface OnItemClickListener {
    fun onItemClick(item: Item)
}


// ViewHolder 클래스 정의
class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.findViewById(R.id.item1)
    val priceTextView: TextView = view.findViewById(R.id.item2)
    val salesStatusTextView: TextView = view.findViewById(R.id.item3)
}

class MyAdapter(private val items: List<Item>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ItemViewHolder>() {

    // ViewHolder를 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ItemViewHolder(view)
    }

    // ViewHolder에 데이터를 바인딩하는 함수
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.titleTextView.text = item.title
        holder.priceTextView.text = item.price.toString()
        holder.salesStatusTextView.text = if (item.salesStatus) "판매 중" else "판매 완료"

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            listener.onItemClick(item)
        }
    }

    // 아이템 개수를 반환하는 함수
    override fun getItemCount(): Int {
        return items.size
    }
}


