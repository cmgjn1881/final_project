package com.example.firebasetest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Msgitem(
    val caller: String,
    val receiver: String,
    val detail: String
)

interface OnMsgClickListener {
    fun onMsgClick(item: Msgitem)
}


// ViewHolder 클래스 정의
class MsgItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.findViewById(R.id.item1)
    val priceTextView: TextView = view.findViewById(R.id.item2)
    val salesStatusTextView: TextView = view.findViewById(R.id.item3)
}

class MsgAdapter(private val msgitems: List<Msgitem>, private val listener: OnMsgClickListener) : RecyclerView.Adapter<MsgItemViewHolder>() {

    // ViewHolder를 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MsgItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return MsgItemViewHolder(view)
    }

    // ViewHolder에 데이터를 바인딩하는 함수
    override fun onBindViewHolder(holder: MsgItemViewHolder, position: Int) {
        val item = msgitems[position]

        if (holder.itemView.context is MsgActivity) {
            // item1의 레이아웃 파라미터 수정
            val layoutParams = holder.titleTextView.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 0f // 가중치 제거
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT // 너비를 내용물에 맞게 조정
            holder.titleTextView.layoutParams = layoutParams
        }

        holder.titleTextView.text = item.caller
        holder.priceTextView.text = item.receiver
        holder.salesStatusTextView.text = item.detail

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            listener.onMsgClick(item)
        }
    }

    // 아이템 개수를 반환하는 함수
    override fun getItemCount(): Int {
        return msgitems.size
    }
}