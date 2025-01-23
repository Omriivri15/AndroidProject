package com.example.myapplication.ui

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
            // אם מדובר בשדה האחרון והטקסט לא ריק, נוסיף שדה חדש
            if (position == steps.size - 1 && text?.isNotEmpty() == true) {
                steps.add("") // הוספת שדה ריק
                notifyItemInserted(steps.size - 1)
            }
        }
    }

    override fun getItemCount(): Int = steps.size

    fun addStep() {
        steps.add("") // הוספת שדה ריק כשלב חדש
        notifyItemInserted(steps.size - 1)
    }
}
