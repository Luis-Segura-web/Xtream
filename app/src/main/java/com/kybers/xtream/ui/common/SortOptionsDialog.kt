package com.kybers.xtream.ui.common

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.kybers.xtream.databinding.DialogSortOptionsBinding

enum class SortOption {
    DEFAULT, A_Z, Z_A
}

data class SortSettings(
    val categoriesSort: SortOption = SortOption.DEFAULT,
    val itemsSort: SortOption = SortOption.DEFAULT
)

class SortOptionsDialog(
    private val context: Context,
    private val contentType: String, // "Canales", "Películas", "Series"
    private val currentSettings: SortSettings,
    private val onApply: (SortSettings) -> Unit
) {
    
    private lateinit var binding: DialogSortOptionsBinding
    private var dialog: AlertDialog? = null
    
    fun show() {
        binding = DialogSortOptionsBinding.inflate(LayoutInflater.from(context))
        
        // Set content type label
        binding.tvItemsLabel.text = contentType
        
        // Set current selections
        when (currentSettings.categoriesSort) {
            SortOption.DEFAULT -> binding.rbCategoryDefault.isChecked = true
            SortOption.A_Z -> binding.rbCategoryAz.isChecked = true
            SortOption.Z_A -> binding.rbCategoryZa.isChecked = true
        }
        
        when (currentSettings.itemsSort) {
            SortOption.DEFAULT -> binding.rbItemsDefault.isChecked = true
            SortOption.A_Z -> binding.rbItemsAz.isChecked = true
            SortOption.Z_A -> binding.rbItemsZa.isChecked = true
        }
        
        dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(true)
            .create()
        
        // Button listeners
        binding.btnCancel.setOnClickListener {
            dialog?.dismiss()
        }
        
        binding.btnApply.setOnClickListener {
            val categorySort = when {
                binding.rbCategoryAz.isChecked -> SortOption.A_Z
                binding.rbCategoryZa.isChecked -> SortOption.Z_A
                else -> SortOption.DEFAULT
            }
            
            val itemSort = when {
                binding.rbItemsAz.isChecked -> SortOption.A_Z
                binding.rbItemsZa.isChecked -> SortOption.Z_A
                else -> SortOption.DEFAULT
            }
            
            onApply(SortSettings(categorySort, itemSort))
            dialog?.dismiss()
        }
        
        dialog?.show()
    }
}