package me.liuqingwen.kotlinandroidviewbindings

/**
 * Created by Qingwen on 2018-2-12, project: ProjectBasicMVP.
 *
 * @Author: Qingwen
 * @DateTime: 2018-2-12
 * @Package: me.liuqingwen.android.projectbasicmvp.ui in project: ProjectBasicMVP
 *
 * Notice: If you are using this class or file, check it and do some modification.
 *
 * This is the code from the [KotlinAndroidViewBindings](https://github.com/MarcinMoskala/KotlinAndroidViewBindings)
 */

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.support.annotation.IdRes
import android.view.View
import android.widget.EditText
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Bind the click action to the button or other views
 */
fun Activity.bindToClickEvent(@IdRes viewId: Int): ReadWriteProperty<Any?, () -> Unit> = ClickEventActionBinding(
        lazy { this.findViewById<View>(viewId) })
/*fun Fragment.bindToClickEvent(@IdRes viewId: Int): ReadWriteProperty<Any?, () -> Unit> = ClickEventActionBinding(
        lazy { this.view!!.findViewById<View>(viewId) })*/

private class ClickEventActionBinding(viewProvider: Lazy<View>): ReadWriteProperty<Any?, () -> Unit>
{
    companion object
    {
        private val EMPTY_ACTION = {}
    }
    private val view by viewProvider
    private var action:(() -> Unit)? = null
    
    override fun getValue(thisRef: Any?, property: KProperty<*>) = this.action ?: ClickEventActionBinding.EMPTY_ACTION
    
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: () -> Unit)
    {
        if (this.action == null)
        {
            this.view.setOnClickListener {
                this.action?.invoke()
            }
        }
        this.action = value
    }
}

/**
 * bindToEditorActions
 */
fun Activity.bindToEditorActions(@IdRes editTextId: Int, predicate: (Int?, Int?) -> Boolean):ReadWriteProperty<Any?, () -> Unit> = EditorActionBinding(
        lazy { this.findViewById<EditText>(editTextId) }, predicate)

private class EditorActionBinding(editTextViewProvider: Lazy<EditText>, private val predicate: (Int?, Int?) -> Boolean): ReadWriteProperty<Any?, () -> Unit>
{
    companion object
    {
        private val EMPTY_ACTION = {}
    }
    private val editText by editTextViewProvider
    private var action:(() -> Unit)? = null
    
    override fun getValue(thisRef: Any?, property: KProperty<*>): () -> Unit = this.action ?: EditorActionBinding.EMPTY_ACTION
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: () -> Unit)
    {
        if (this.action == null)
        {
            this.editText.setOnEditorActionListener { _, actionId, event ->
                val handleAction = this.predicate(actionId, event.keyCode)
                if (handleAction)
                {
                    this.action?.invoke()
                }
                handleAction
            }
        }
        this.action = value
    }
}

/**
 * Bind the error of the EditText view
 */
fun Activity.bindToErrorId(@IdRes editTextId: Int, context: Context): ReadWriteProperty<Any?, Int?> = EditTextViewErrorBinding(
        lazy { this.findViewById<EditText>(editTextId) }, context)

private class EditTextViewErrorBinding(editTextViewProvider: Lazy<EditText>, private val context: Context): ReadWriteProperty<Any?, Int?>
{
    private val editText by editTextViewProvider
    private var currentErrorId:Int? = null
    
    override fun getValue(thisRef: Any?, property: KProperty<*>) = this.currentErrorId
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int?)
    {
        this.currentErrorId = value
        this.editText.error = value?.let { this.context.getString(it) }
    }
}

/**
 * Bind the view's focus action
 */
fun Activity.bindToRequestFocus(@IdRes viewId: Int): ReadOnlyProperty<Any?, ()->Unit> = RequestFocusBinding(
        lazy { this.findViewById<View>(viewId) })

private class RequestFocusBinding(viewProvider: Lazy<View>): ReadOnlyProperty<Any?, ()->Unit>
{
    private val view by viewProvider
    private val requestFocus by lazy { { this.view.requestFocus(); Unit } }
    
    override fun getValue(thisRef: Any?, property: KProperty<*>) = this.requestFocus
}

/**
 * Bind the text to the EditText view
 */
fun Activity.bindToEditText(@IdRes editTextId: Int): ReadWriteProperty<Any?, String> = EditTextViewBinding(
        lazy { this.findViewById<EditText>(editTextId) })

private class EditTextViewBinding(editTextViewProvider: Lazy<EditText>): ReadWriteProperty<Any?, String>
{
    private val editText by editTextViewProvider
    override fun getValue(thisRef: Any?, property: KProperty<*>) = this.editText.text.toString()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) = this.editText.setText(value)
}

/**
 * Bind the visibility of two views
 */
fun Activity.bindToLoadings(@IdRes progressViewId: Int, @IdRes hiddenViewId: Int):ReadWriteProperty<Any?, Boolean> = ProgressViewLoadingBinding(
        lazy { this.findViewById<View>(progressViewId) }, lazy { this.findViewById<View>(hiddenViewId) })

private class ProgressViewLoadingBinding(progressViewProvider: Lazy<View>, hiddenViewProvider: Lazy<View>): ReadWriteProperty<Any?, Boolean>
{
    private val progressView by progressViewProvider
    private val hiddenView by hiddenViewProvider
    override fun getValue(thisRef: Any?, property: KProperty<*>) = this.progressView.visibility == View.VISIBLE
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean)
    {
        this.progressView.visibility = if (value) View.VISIBLE else View.INVISIBLE
        this.hiddenView.visibility = if (value) View.INVISIBLE else View.VISIBLE
    }
}

/**
 * Just bind the visibility of the view(ProgressBar)
 */
fun Activity.bindToViewVisibility(@IdRes id:Int, isGone: Boolean = false):ReadWriteProperty<Any?, Boolean> = ViewVisibilityBinding(lazy { this.findViewById<View>(id) }, isGone)

private class ViewVisibilityBinding(viewProvider: Lazy<View>, private val isGone: Boolean = false): ReadWriteProperty<Any?, Boolean>
{
    private val view by viewProvider
    override fun getValue(thisRef: Any?, property: KProperty<*>) = this.view.visibility == View.VISIBLE
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean)
    {
        this.view.visibility = if (value) View.VISIBLE else if(this.isGone) View.GONE else View.INVISIBLE
    }
}

/**
 * Bind the [Dialog] to the loading view
 * **This function may lead to memory leaks...**
 */
fun bindToLoadingDialog(progressViewProvider: () -> Dialog): ReadWriteProperty<Any?, Boolean> = ProgressLoadingBinding(lazy(progressViewProvider))

private class ProgressLoadingBinding(progressViewProvider: Lazy<Dialog>) : ReadWriteProperty<Any?, Boolean>
{
    private val progressView by progressViewProvider
    private var isProgressVisible = false
    private var isCancelListenerSet = false
    
    override fun getValue(thisRef: Any?, property: KProperty<*>) = this.isProgressVisible
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean)
    {
        if (value != this.isProgressVisible)
        {
            this.isProgressVisible = value
            if (value) this.progressView.show() else this.progressView.hide()
            if (value && !this.isCancelListenerSet)
            {
                this.isCancelListenerSet = true
                this.progressView.setOnCancelListener { this.isProgressVisible = false }
            }
        }
    }
}

/**
 * Bind the view state(enabled/disabled) to the boolean property
 */
fun Activity.bindToState(@IdRes viewId: Int): ReadWriteProperty<Any?, Boolean> = StateBinding(lazy { this.findViewById<View>(viewId) })

private class StateBinding(viewProvider: Lazy<View>):ReadWriteProperty<Any?, Boolean>
{
    private val view by viewProvider
    override fun getValue(thisRef: Any?, property: KProperty<*>) = view.isEnabled
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean)
    {
        if (view.isEnabled != value)
        {
            view.isEnabled = value
        }
    }
}
