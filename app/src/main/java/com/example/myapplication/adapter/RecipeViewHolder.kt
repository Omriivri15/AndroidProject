import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
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
    private val ingredientsTextView: TextView = itemView.findViewById(R.id.recipe_ingredients_text_view)
    private val imageView: ImageView = itemView.findViewById(R.id.recipe_image_view)

    fun bind(recipe: Recipe) {
        nameTextView.text = recipe.name
        ingredientsTextView.text = recipe.ingredients?.joinToString(", ") ?: "No ingredients"
        descriptionTextView.text = recipe.description

        if (recipe.imageUrl.isNotBlank()) {
            val storageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(recipe.imageUrl)

            storageReference.downloadUrl.addOnSuccessListener { uri ->
                Log.d("RecipeViewHolder", "Image URL: $uri")
                Glide.with(itemView.context)
                    .load(uri)
                    .placeholder(R.drawable.man)
                    .into(imageView)
            }.addOnFailureListener { exception ->
                Log.e("RecipeViewHolder", "Failed to load image: ${exception.message}")
                imageView.setImageResource(R.drawable.man)
            }
        } else {
            imageView.setImageResource(R.drawable.man)
        }
    }
}
