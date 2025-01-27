import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class StepsAdapter(private val steps: MutableList<String>) :
    RecyclerView.Adapter<StepsAdapter.StepViewHolder>() {

    inner class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stepInput: EditText = itemView.findViewById(R.id.step_input)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.stepInput.setText(steps[position])

        holder.stepInput.doAfterTextChanged { text ->
            // If the last step is not empty, add a new step
            if (position == steps.size - 1 && !text.isNullOrEmpty()) {
                steps.add("")
                notifyItemInserted(steps.size - 1)
            }

            // Update the step list with the current text
            if (text != null) {
                steps[position] = text.toString()  // Update the current step text
            }
        }
    }

    override fun getItemCount(): Int = steps.size

    fun addStep() {
        steps.add("")
        notifyItemInserted(steps.size - 1)
    }

    // This function returns the list of steps
    fun getItems(): List<String> {
        return steps
    }
}
