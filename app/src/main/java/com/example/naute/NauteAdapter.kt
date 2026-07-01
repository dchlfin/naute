package com.example.naute

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class NauteAdapter(private val context: Context, private val nautes: ArrayList<String>): RecyclerView.Adapter<NauteAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val nauteCard = LayoutInflater.from(context).inflate(R.layout.naute_card, viewGroup, false)
        return ViewHolder(nauteCard)
    }

    override fun getItemCount(): Int {
        return nautes.count()
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(nautes[position])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(naute: String){
            itemView.findViewById<TextView>(R.id.nauteTitle).text = naute
            itemView.setOnClickListener {
                Toast.makeText(context, naute, Toast.LENGTH_LONG).show()
            }
        }
    }
}