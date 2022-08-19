package com.ash.thetracker.shared

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.ash.thetracker.R

object DialogUtils {

    fun showDialog(
        context: Context,
        title: String? = null,
        message: String? = null,
        positiveButtonText: String = context.string(R.string.ok),
        negativeButtonText: String = context.string(R.string.cancel),
        showOnlyPositiveButton: Boolean = false,
        isCancelable: Boolean = true,
        negativeButtonClickListener: (dialog: DialogInterface) -> Unit = { },
        positiveButtonClickListener: (dialog: DialogInterface) -> Unit = { }
    ) {
        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(isCancelable)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                positiveButtonClickListener.invoke(dialog)
            }
        if (!showOnlyPositiveButton) {
            builder.setNegativeButton(negativeButtonText) { dialog, _ ->
                negativeButtonClickListener.invoke(dialog)
            }
        }
        builder.create().show()
    }
}