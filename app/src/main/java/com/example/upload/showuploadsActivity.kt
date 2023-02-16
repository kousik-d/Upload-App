package com.example.upload

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class showuploadsActivity : AppCompatActivity() {


    private var userlist: MutableList<Users> = mutableListOf()
    private var database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myreferance : DatabaseReference = database.reference.child("myUsers")
    private lateinit var useradapter : UserAdapter
    private lateinit var recycler : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showuploads)
        recycler = findViewById(R.id.recycler)
        reteriveData()
    }

    fun reteriveData(){

        myreferance.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userlist.clear()
                for (eachuser in snapshot.children){
                   val user = eachuser.getValue(Users::class.java)
                    if(user!=null){
                        userlist.add(user)
                    }
                    useradapter = UserAdapter(this@showuploadsActivity,userlist)
                    recycler.layoutManager= LinearLayoutManager(this@showuploadsActivity)
                    recycler.adapter = useradapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}