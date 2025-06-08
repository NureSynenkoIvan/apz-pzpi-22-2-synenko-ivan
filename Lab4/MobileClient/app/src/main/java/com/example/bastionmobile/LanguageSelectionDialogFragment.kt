package com.example.bastionmobile


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class LanguageSelectionDialogFragment : DialogFragment() {

    interface OnLanguageSelectedListener {
        fun onLanguageSelected(languageCode: String)
    }

    private var listener: OnLanguageSelectedListener? = null
    private lateinit var appPreferences: AppPreferences

    private val languages = listOf(
        Language("uk", "Українська"),
        Language("en", "English")
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        appPreferences = AppPreferences(requireContext())

        val languageNames = languages.map { it.name }.toTypedArray()
        val currentLanguageCode = appPreferences.getLanguage()
        val checkedItem = languages.indexOfFirst { it.code == currentLanguageCode }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.select_language_title)
            .setSingleChoiceItems(languageNames, checkedItem) { dialog, which ->
                val selectedLanguage = languages[which]
                listener?.onLanguageSelected(selectedLanguage.code)
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLanguageSelectedListener) {
            listener = context
        } else if (parentFragment is OnLanguageSelectedListener) {
            listener = parentFragment as OnLanguageSelectedListener
        } else {
            throw RuntimeException("$context must implement OnLanguageSelectedListener or parentFragment must")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        const val TAG = "LanguageSelectionDialogFragment"

        fun newInstance(): LanguageSelectionDialogFragment {
            return LanguageSelectionDialogFragment()
        }
    }
}

data class Language(
    val code: String, //  "en", "uk"
    val name: String  //  "English", "Українська"
)