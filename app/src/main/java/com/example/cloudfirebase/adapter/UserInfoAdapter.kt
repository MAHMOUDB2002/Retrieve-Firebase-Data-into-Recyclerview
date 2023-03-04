package com.example.cloudfirebase.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudfirebase.R
import com.example.cloudfirebase.model.UserInfo
import com.example.cloudfirebase.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.add_user_item.view.*

class UserInfoAdapter(
    private val context: Context,
    private val AddToRecycler: ArrayList<UserInfo>
) :RecyclerView.Adapter<AddUserInfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddUserInfoViewHolder {

        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.add_user_item,
                    parent, false
                )
        return AddUserInfoViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: AddUserInfoViewHolder, position: Int) {

        val AddUser = AddToRecycler[position]
        holder.uName.text = AddToRecycler[position].uName.toString()
        holder.uNumber.text = AddToRecycler[position].uNumber.toString()
        holder.uAddress.text = AddToRecycler[position].uAddress.toString()



        holder.itemView.btnDeleteUser.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete Item")
            builder.setMessage("Are you sure uoy want to remove item from wishlist?")

            builder.setPositiveButton("Confirm") { dialog, which ->
                FirebaseFirestore.getInstance().collection(Constants.USERS).document(AddUser.id)
                    .delete().addOnSuccessListener {
                        AddToRecycler.removeAt(position)
                        notifyDataSetChanged()
                    }
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
                .show()
            notifyItemChanged(holder.adapterPosition)
        }



    }

    override fun getItemCount(): Int {
        return AddToRecycler.size
    }

}

class AddUserInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val uName: TextView = itemView.findViewById(R.id.iName)
    val uNumber: TextView = itemView.findViewById(R.id.iNumber)
    val uAddress: TextView = itemView.findViewById(R.id.iAddress)
    val deleteButton: ImageView = itemView.findViewById(R.id.btnDeleteUser)

}