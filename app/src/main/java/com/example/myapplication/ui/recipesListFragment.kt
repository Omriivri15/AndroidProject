import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.LoginActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.adapter.RecipeAdapter
import com.example.myapplication.model.Recipe
import com.example.myapplication.ui.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class RecipesListFragment : Fragment() {

    private lateinit var adapter: RecipeAdapter
    private var recipesList: MutableList<Recipe> = mutableListOf()  // Store the fetched recipes

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recipes_list, container, false)

        // Initialize the adapter with an empty list
        adapter = RecipeAdapter(recipesList)

        // Set up RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recipes_list_activity_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Set up Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.toolbar_recipes_list)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "ReciAppes"

        val profileIcon: ImageView = view.findViewById(R.id.profile_icon)
        profileIcon.setOnClickListener {
            showProfileMenu(it)
        }

        // Fetch recipes from Firestore
        fetchRecipes()

        return view
    }

    override fun onStart() {
        super.onStart()
        // Notify adapter when data changes
        adapter.notifyDataSetChanged()
    }

    // Fetch recipes from Firestore
    private fun fetchRecipes() {
        val db = FirebaseFirestore.getInstance()
        val recipesCollection = db.collection("recipes")  // Assuming 'recipes' is the collection name

        recipesCollection.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result
                    recipesList.clear()  // Clear existing list
                    if (documents != null) {
                        for (document in documents) {
                            val recipe = document.toObject(Recipe::class.java)
                            recipesList.add(recipe)  // Add new recipe to list
                        }
                        adapter.notifyDataSetChanged()  // Notify adapter about data change
                    }
                } else {
                    // Handle failure (e.g., show error message)
                }
            }
    }

    // Show Popup Menu for Profile
    private fun showProfileMenu(anchor: View) {
        val popupMenu = PopupMenu(requireContext(), anchor)
        popupMenu.menuInflater.inflate(R.menu.menu_profile, popupMenu.menu)

        // Handle menu item clicks
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_profile -> {
                    // Navigate to ProfileFragment
                    navigateToProfile()
                    true
                }
                R.id.action_logout -> {
                    // Perform Logout
                    logoutUser()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun navigateToProfile() {
        // Use MainActivity's displayFragment to navigate
        (activity as? MainActivity)?.displayFragment(ProfileFragment())
    }

    private fun logoutUser() {
        // Logout via Firebase
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
        startActivity(intent)
        requireActivity().finish()
    }
}
