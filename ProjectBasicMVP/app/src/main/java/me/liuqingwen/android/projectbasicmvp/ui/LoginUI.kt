package me.liuqingwen.android.projectbasicmvp.ui

import android.content.Context
import android.graphics.Typeface
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.text.InputType
import me.liuqingwen.android.projectbasicmvp.R
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint

/**
 * Created by Qingwen on 2018-2-12, project: ProjectBasicMVP.
 *
 * @Author: Qingwen
 * @DateTime: 2018-2-12
 * @Package: me.liuqingwen.android.projectbasicmvp.ui in project: ProjectBasicMVP
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

const val ID_LABEL_LOGIN    = 0x10
const val ID_LABEL_USERNAME = 0x11
const val ID_TEXT_USERNAME  = 0x12
const val ID_LABEL_PASSWORD = 0x13
const val ID_TEXT_PASSWORD  = 0x14
const val ID_BUTTON_LOGIN   = 0x15
const val ID_PROGRESS_BAR   = 0x16

class LoginUI:AnkoComponent<Context>
{
    override fun createView(ui: AnkoContext<Context>) = with(ui) {
        constraintLayout {
            
            textView("Sign In") {
                id = ID_LABEL_LOGIN
                textColorResource = R.color.colorPrimary
                textSize = 18.0f
                typeface = Typeface.DEFAULT_BOLD
            }
            
            textView("Username") {
                id = ID_LABEL_USERNAME
                textSize = 18.0f
                alpha = 0.75f
            }
            
            editText {
                id = ID_TEXT_USERNAME
                inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                ems = 16
            }.lparams(width = matchConstraint)
    
            textView("Password") {
                id = ID_LABEL_PASSWORD
                textSize = 18.0f
                alpha = 0.75f
            }
    
            editText {
                id = ID_TEXT_PASSWORD
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ems = 16
            }.lparams(width = matchConstraint)
            
            button("Login") {
                id = ID_BUTTON_LOGIN
                textColorResource = R.color.colorAccent
                textSize = 18.0f
            }.lparams(width = matchConstraint)
    
            progressBar {
                id = ID_PROGRESS_BAR
                isIndeterminate = true
            }.lparams(width = matchConstraint, height = matchConstraint)
            
            applyConstraintSet {
                connect(
                        START of ID_LABEL_LOGIN to START of PARENT_ID margin dip(16),
                        TOP of ID_LABEL_LOGIN to TOP of PARENT_ID margin dip(32),
                        
                        START of ID_TEXT_USERNAME to START of PARENT_ID margin dip(16),
                        END of ID_TEXT_USERNAME to END of PARENT_ID margin dip(16),
                        TOP of ID_TEXT_USERNAME to BOTTOM of ID_LABEL_LOGIN margin dip(26),

                        START of ID_LABEL_USERNAME to START of ID_TEXT_USERNAME margin dip(4),
                        BOTTOM of ID_LABEL_USERNAME to BOTTOM of ID_TEXT_USERNAME margin dip(10),

                        START of ID_TEXT_PASSWORD to START of PARENT_ID margin dip(16),
                        END of ID_TEXT_PASSWORD to END of PARENT_ID margin dip(16),
                        TOP of ID_TEXT_PASSWORD to BOTTOM of ID_TEXT_USERNAME margin dip(26),

                        START of ID_LABEL_PASSWORD to START of ID_TEXT_PASSWORD margin dip(4),
                        BOTTOM of ID_LABEL_PASSWORD to BOTTOM of ID_TEXT_PASSWORD margin dip(10),

                        START of ID_BUTTON_LOGIN to START of PARENT_ID margin dip(8),
                        END of ID_BUTTON_LOGIN to END of PARENT_ID margin dip(8),
                        TOP of ID_BUTTON_LOGIN to BOTTOM of ID_TEXT_PASSWORD margin dip(16),

                        START of ID_PROGRESS_BAR to START of ID_BUTTON_LOGIN,
                        END of ID_PROGRESS_BAR to END of ID_BUTTON_LOGIN,
                        TOP of ID_PROGRESS_BAR to TOP of ID_BUTTON_LOGIN,
                        BOTTOM of ID_PROGRESS_BAR to BOTTOM of ID_BUTTON_LOGIN
                        )
            }
        }
    }
}