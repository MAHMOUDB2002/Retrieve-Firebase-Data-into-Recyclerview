package com.example.cloudfirebase.view

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cloudfirebase.R
import com.example.cloudfirebase.adapter.UserInfoAdapter
import com.example.cloudfirebase.model.UserInfo
import com.example.cloudfirebase.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_alert_dialog.view.*

class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var swipeRefresh: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        swipeRefresh = findViewById(R.id.swipeRefresh)

        swipeRefresh.setOnRefreshListener {
            getData()
        }
        mRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity
            )
        }
        getData()


        btnAddUserInfo.setOnClickListener {

            val mDialogView =
                LayoutInflater.from(this).inflate(R.layout.custom_alert_dialog, null)

            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("Add user Info")
            val mAlertDialog = mBuilder.show()



            mDialogView.btnDialogSave.setOnClickListener {
                val name = mDialogView.nameDialog.text.toString()
                val number = mDialogView.phoneNumberDialog.text.toString()
                val address = mDialogView.addressDialog.text.toString()

                if (name.isEmpty()) {
                    Toast.makeText(this, "Fill name field !!", Toast.LENGTH_SHORT)
                        .show()
                } else if (number.isEmpty()) {
                    Toast.makeText(this, "Fill number field !!", Toast.LENGTH_SHORT)
                        .show()
                } else if (address.isEmpty()) {
                    Toast.makeText(this, "Fill address field !!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val userInfo = hashMapOf(
                        "uName" to name,
                        "uPhone_Number" to number,
                        "uAddress" to address,
                    )
                    val pDialog = ProgressDialog(this)
                    pDialog.setMessage("Uploading Info.....")
                    pDialog.setCancelable(false)
                    pDialog.show()

                    db.collection(Constants.USERS)
                        .add(userInfo)
                        .addOnSuccessListener {
                            pDialog.dismiss()
                            Toast.makeText(
                                this,
                                "Added SuccessFully...............",
                                Toast.LENGTH_SHORT
                            ).show()
                            mAlertDialog.dismiss()
                            mRecyclerView.adapter!!.notifyDataSetChanged()
                        }

                        .addOnFailureListener {
                            pDialog.dismiss()
                            Toast.makeText(this, "Failed to Add!!!!!!!!!!", Toast.LENGTH_SHORT)
                                .show()
                            mAlertDialog.dismiss()
                        }

                    mRecyclerView.adapter?.notifyDataSetChanged()

//                mAlertDialog.dismiss()
                }
            }

            mDialogView.btnDialogCancel.setOnClickListener {
                mAlertDialog.dismiss()
                //notifyItemChanged(holder.adapterPosition)
            }
        }

    }

    private fun getData() {
        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = false
        }
        val mm = ArrayList<UserInfo>()
        FirebaseFirestore.getInstance().collection(Constants.USERS)
            .orderBy("uName", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (doc in documents) {
                        var id = doc.id
                        var name = doc.get("uName").toString()
                        var number = doc.get("uPhone_Number").toString()
                        var address = doc.get("uAddress").toString()
                        var categ = UserInfo(id, name, number, address)
                        mm.add(categ)
                    }
                    mRecyclerView.adapter = UserInfoAdapter(this, mm)
                    mRecyclerView.adapter!!.notifyDataSetChanged()

                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }
}