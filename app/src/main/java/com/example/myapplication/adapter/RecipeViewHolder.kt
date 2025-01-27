import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.Recipe
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class RecipeViewHolder(
    itemView: View,
    private val listener: AdapterView.OnItemClickListener?
) : RecyclerView.ViewHolder(itemView) {

    private val nameTextView: TextView = itemView.findViewById(R.id.recipe_name_text_view)
    private val descriptionTextView: TextView = itemView.findViewById(R.id.recipe_description_text_view)
    private val ratingBar: RatingBar = itemView.findViewById(R.id.recipe_rating_bar)
    private val imageView: ImageView = itemView.findViewById(R.id.recipe_image_view)

    init {

    }

    fun bind(recipe: Recipe) {
        nameTextView.text = recipe.name
        descriptionTextView.text = recipe.description
        ratingBar.rating = recipe.rating

        // Check if imageUrl is valid (could be null or empty)
        if (recipe.imageUrl.isNotBlank()) {
            // Firebase Storage reference
            val storageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(recipe.imageUrl)

            // Get the download URL from Firebase Storage
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                // Log the URI to check if it's correct
                Log.d("RecipeViewHolder", "Image URL: $uri")

                // Use Glide to load the image from the URI
                Glide.with(itemView.context)
                    .load(uri) // Load image from Firebase Storage using URI
                    .placeholder(R.drawable.man) // Placeholder while loading
                    .into(imageView)
            }.addOnFailureListener { exception ->
                // Log error and use the placeholder if image fetch fails
                Log.e("RecipeViewHolder", "Failed to load image: ${exception.message}")
                imageView.setImageResource(R.drawable.man)
            }
        } else {
            // If no image URL is found, set a default placeholder
            imageView.setImageResource(R.drawable.man)
        }
    }

}
