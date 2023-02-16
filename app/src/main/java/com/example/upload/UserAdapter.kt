package com.example.upload

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class UserAdapter(var context : Context, var userlist : MutableList<Users>):RecyclerView.Adapter<UserAdapter.userview>(){

    inner class userview(itemview : View): RecyclerView.ViewHolder(itemview){
        val img : ImageView = itemview.findViewById(R.id.itemimage)
        val progress : ProgressBar = itemview.findViewById(R.id.progressBar2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userview {
        val item=LayoutInflater.from(context).inflate(R.layout.item_activity,parent,false)
        return userview(item)
    }

    override fun getItemCount() = userlist.size

    override fun onBindViewHolder(holder: userview, position: Int) {
        val curr = userlist[position].url
        Picasso.get().load(curr).into(holder.img,object: Callback{
            override fun onSuccess() {
                holder.progress.visibility = View.INVISIBLE
            }

            override fun onError(e: Exception?) {
                Toast.makeText(context,e?.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        })
    }
}