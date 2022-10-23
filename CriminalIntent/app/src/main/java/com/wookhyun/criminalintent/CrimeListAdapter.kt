package com.wookhyun.criminalintent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.wookhyun.criminalintent.databinding.ListItemCrimeBinding
import com.wookhyun.criminalintent.databinding.ListItemCrucialCrimeBinding
import kotlinx.coroutines.NonDisposableHandle.parent
import java.util.*

class CrimeListAdapter(
    private val crimes: List<Crime>,
    private val onCrimeClicked: (crimeId: UUID) -> Unit,
    private val onDelete: (crime: Crime) -> Unit,
    ) : RecyclerView.Adapter<ViewHolder>() {
    enum class ViewTypeEnum(val num: Int) {
        CRUCIAL(1),
        NORMAL(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder: ViewHolder = when(viewType){
            0 -> {
                val binding = ListItemCrimeBinding.inflate(inflater, parent, false)
                CrimeHolder(binding)
            }
            else -> {
                val binding = ListItemCrucialCrimeBinding.inflate(inflater, parent, false)
                CrucialCrimeHolder(binding)
            }
        }
        return viewHolder

    }

    override fun getItemCount() = crimes.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val crime = crimes[position]
        if(holder is CrucialCrimeHolder){
            holder.bind(crime)
        }
        else if(holder is CrimeHolder){
            holder.bind(crime, onCrimeClicked, onDelete)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (crimes[position].isSolved) ViewTypeEnum.CRUCIAL.num else ViewTypeEnum.NORMAL.num
    }
}