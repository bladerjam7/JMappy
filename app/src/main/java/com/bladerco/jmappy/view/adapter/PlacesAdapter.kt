package com.bladerco.jmappy.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bladerco.jmappy.R
import com.bladerco.jmappy.model.data.places.PlacesResult
import com.bladerco.jmappy.util.Constants.Companion.IMAGE_URL
import com.bladerco.jmappy.util.Constants.Companion.TAG
import com.bumptech.glide.Glide

class PlacesAdapter (private var placeList: List<PlacesResult>): RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder>() {

    fun updatePlaceList(list: List<PlacesResult>){
        placeList = list
        notifyDataSetChanged()
        Log.d(TAG, "updatePlaceList: Place List has been updated")
    }

    inner class PlacesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imPhoto: ImageView = itemView.findViewById(R.id.im_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_name_place)
        val tvRatingNumber: TextView = itemView.findViewById(R.id.tv_rating_number)
        val ratingBar: RatingBar = itemView.findViewById(R.id.rb_stars)
        val tvReviewNumber: TextView = itemView.findViewById(R.id.tv_review_numbers)
        val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        val btnFavorite: CardView = itemView.findViewById(R.id.cv_btn_favorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        return PlacesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.items_places, parent, false))
    }

    override fun getItemCount(): Int = placeList.size


    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val place = placeList[position]

        holder.apply {

            Glide.with(itemView.context)
                .load(IMAGE_URL + place.photos?.get(0).photo_reference)
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(imPhoto)
            tvName.text = place.name
            tvRatingNumber.text = place.rating.toString()
            ratingBar.rating = place.rating.toFloat()
            tvReviewNumber.text = place.user_ratings_total.toString()

            var s = ""
            for(i in 0..place.types.size-1){
                if(i < place.types.size-1){
                    s += "${place.types[i]}, "
                } else {
                    s += "${place.types[i]}"
                }

            }
            tvCategory.text = s

            btnFavorite.setOnClickListener {
                // To do later
            }


        }
    }
}