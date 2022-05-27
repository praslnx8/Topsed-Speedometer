package com.bike.race.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.bike.race.R


object DialogUtils {

    fun createDialog(
        context: Context,
        title: String? = null,
        message: String? = null,
        positiveAction: String? = null,
        negativeAction: String? = null,
        onSuccessAction: () -> Unit = {},
        onNegativeAction: () -> Unit = {}
    ): Dialog {
        val alertDialogBuilder =
            AlertDialog.Builder(context, R.style.AppDialogTheme)
        if (title != null) alertDialogBuilder.setTitle(title)
        if (message != null) alertDialogBuilder.setMessage(message)
        if (positiveAction != null) alertDialogBuilder.setPositiveButton(
            positiveAction
        ) { _: DialogInterface?, _: Int ->
            onSuccessAction()
        }
        if (negativeAction != null) {
            alertDialogBuilder.setNegativeButton(
                negativeAction
            ) { _: DialogInterface?, _: Int ->
                onNegativeAction()
            }
            alertDialogBuilder.setCancelable(false)
        } else {
            alertDialogBuilder.setCancelable(true)
        }
        return alertDialogBuilder.create()
    }

    fun createInputDialog(
        context: Context,
        title: String? = null,
        message: String? = null,
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        input: String? = null,
        hint: String? = null,
        maxLength: Int? = null,
        positiveAction: String? = null,
        negativeAction: String? = null,
        onSuccessAction: (String) -> Unit = {},
        onNegativeAction: () -> Unit = {}
    ): Dialog {

        val alertDialogBuilder =
            AlertDialog.Builder(context, R.style.AppDialogTheme)

        val inputText = EditText(context)
        inputText.inputType = inputType
        if (input.isNullOrBlank().not()) {
            inputText.setText(input)
        }
        if (hint.isNullOrBlank().not()) {
            inputText.hint = hint
        }
        if (maxLength != null) {
            inputText.maxEms = maxLength
        }
        alertDialogBuilder.setView(inputText)
        if (title != null) alertDialogBuilder.setTitle(title)
        if (message != null) alertDialogBuilder.setMessage(message)
        if (positiveAction != null) alertDialogBuilder.setPositiveButton(
            positiveAction
        ) { _: DialogInterface?, _: Int ->
            onSuccessAction(inputText.text.trim().toString())
        }
        if (negativeAction != null) {
            alertDialogBuilder.setNegativeButton(
                negativeAction
            ) { _: DialogInterface?, _: Int ->
                onNegativeAction()
            }
            alertDialogBuilder.setCancelable(false)
        } else {
            alertDialogBuilder.setCancelable(true)
        }


        return alertDialogBuilder.create()
    }
}