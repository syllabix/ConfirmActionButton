package com.crushonly.confirmactionbutton

/**
 * Confirm Action Button
 *
 * The confirm action button is a Floating Action Button that facilitates
 * handling "touch twice" to confirm ux.
 *
 * It supports providing a confirm action label, and the animation
 * to expand the button to reveal confirm text
 *
 * The button also fully supports databinding
 *
 * @author Tom Stoepker
 * @github https://github.com/syllabix
 */


@BindingMethods(
        BindingMethod(
                type = ConfirmActionButton::class, attribute = "confirmActionModeAttrChanged", method = "setConfirmActionModeListener"
        ),
        BindingMethod(
                type = ConfirmActionButton::class, attribute = "confirmTextAttrChanged", method = "setConfirmTextListener"
        )
)
class ConfirmActionButton: LinearLayout, View.OnClickListener {

    // clickHandlers handle the initial click that move the button into a confirm state
    private var clickHandlers: ArrayList<ConfirmActionButton.ConfirmActionButtonClickHandler> = ArrayList()
    //confirmClickHandlers handle the confirmation click - intended to execute an action, make data updates, etc
    private var confirmClickHandlers: ArrayList<ConfirmActionButton.ConfirmActionButtonClickHandler> = ArrayList()
    //Inverse Binding listener for 2 way binding updates on the mode of the button
    private var modeListener: InverseBindingListener? = null
    //Inverse Binding listener for 2 way binding updates on the confirm text
    private var confirmTextListener: InverseBindingListener? = null


    // Mode/State the confirm button is in
    private var mode: Mode = Mode.READY
        set(value) {
            field = value
            modeListener?.onChange()
            animateButton(value == Mode.CONFIRMING)
        }

    private var confirmText: String
        set(value) {
            textView.text = value
            textView.visibility = if (value.isNullOrBlank()) View.GONE else View.VISIBLE
            confirmTextListener?.onChange()
            if(mode == Mode.CONFIRMING) {
                animateButton(true)
            }
        }
        get() {
            return textView.text.toString()
        }


    //Display attributes/values
    private var tint: Int = 0
    private var confirmTint: Int = 0
    private var backgroundTint: Int = 0
    private var confirmBackgroundTint: Int = 0
    private var drawableRight: Int = 0
    private var confirmDrawableRight: Int = 0
    private var allCaps = false

    //Child Views
    var textView: TextView = TextView(context)
    var textContainer: FrameLayout = FrameLayout(context)
    var rightIcon: ImageView = ImageView(context)

    /**
     * The ConfirmActionButton companion object provides methods to expose binding adapters
     */
    companion object {

        @JvmStatic
        @BindingAdapter(value = ["onClick"])
        fun setOnClickHandler(view: ConfirmActionButton, oldHandler: ConfirmActionButton.ConfirmActionButtonClickHandler?, newHandler: ConfirmActionButton.ConfirmActionButtonClickHandler?) {
            oldHandler?.let {
                view.removeOnClickListener(it)
            }
            newHandler?.let {
                view.addOnClickListener(it)
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["onConfirmClick"])
        fun setOnConfirmClickHandler(view: ConfirmActionButton, oldHandler: ConfirmActionButton.ConfirmActionButtonClickHandler?, newHandler: ConfirmActionButton.ConfirmActionButtonClickHandler?) {
            oldHandler?.let {
                view.removeOnConfirmClickListener(it)
            }
            newHandler?.let {
                view.addOnConfirmClickListener(it)
            }
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "confirmActionMode")
        fun getMode(view: ConfirmActionButton) = view.mode

        @JvmStatic
        @BindingAdapter("confirmActionMode")
        fun setMode(view: ConfirmActionButton, mode: ConfirmActionButton.Mode?) {
            mode?.let {
                if(it != view.mode) {
                    view.mode = it
                }
            }
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "confirmText")
        fun getConfirmText(view: ConfirmActionButton) = view.confirmText

        @JvmStatic
        @BindingAdapter("confirmText")
        fun setConfirmText(view: ConfirmActionButton, confirmText: String?) {
            confirmText?.let {
                if(it != view.confirmText) {
                    view.confirmText = it
                }
            }
        }
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val standardSet = arrayOf(android.R.attr.textSize, android.R.attr.tint).toIntArray()
        val stdTypedArray = context.obtainStyledAttributes(attrs, standardSet)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ConfirmActionButton)

        isClickable = true
        isFocusable = true
        allCaps = typedArray.getBoolean(R.styleable.ConfirmActionButton_allCaps, false)

        backgroundTint = stdTypedArray.getColor(R.styleable.ConfirmActionButton_android_backgroundTint, resources.colorFromResId(R.color.colorAccent))
        confirmBackgroundTint = stdTypedArray.getColor(R.styleable.ConfirmActionButton_confirmBackgroundTint, resources.colorFromResId(R.color.colorSuccess))

        layoutTextView(
                allCaps = typedArray.getBoolean(R.styleable.ConfirmActionButton_allCaps, true),
                textSize = stdTypedArray.getDimension(0, 16F),
                textColor = stdTypedArray.getColor(R.styleable.ConfirmActionButton_confirmTextColor, resources.colorFromResId(android.R.color.white))
        )

        drawableRight = typedArray.getResourceId(R.styleable.ConfirmActionButton_drawableRight, R.drawable.icn_morph)
        confirmDrawableRight = typedArray.getResourceId(R.styleable.ConfirmActionButton_confirmDrawableRight, R.drawable.icn_morph_reverse)
        tint = typedArray.getColor(R.styleable.ConfirmActionButton_android_tint, resources.colorFromResId(android.R.color.white))
        layoutRightIcon(tint = tint)

        typedArray.recycle()
        stdTypedArray.recycle()

        setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(mode) {
            Mode.READY -> notifyHandlers(clickHandlers)
            Mode.CONFIRMING -> notifyHandlers(confirmClickHandlers)
        }
    }

    /**
     * Add a click event listener
     *
     * @param handler A handler instance to call when the user clicks on the button
     */
    fun addOnClickListener(handler: ConfirmActionButton.ConfirmActionButtonClickHandler) {
        clickHandlers.add(handler)
    }

    /**
     * Remove a click event listener
     *
     * @param handler A handler to remove
     */
    fun removeOnClickListener(handler: ConfirmActionButton.ConfirmActionButtonClickHandler) {
        clickHandlers.remove(handler)
    }

    /**
     * Add a confirm click event listener
     *
     * @param handler A handler instance to call when the user clicks on the button in confirm mode only
     */
    fun addOnConfirmClickListener(handler: ConfirmActionButton.ConfirmActionButtonClickHandler) {
        confirmClickHandlers.add(handler)
    }

    /**
     * Remove a confirm click event listener
     *
     * @param handler A handler to remove
     */
    fun removeOnConfirmClickListener(handler: ConfirmActionButton.ConfirmActionButtonClickHandler) {
        confirmClickHandlers.remove(handler)
    }

    /**
     * Set the inverse binding listener to enable 2 way binding for the button mode
     *
     * @param[bindingListener] An instance of InverseBindingListener
     */
    fun setConfirmActionModeListener(bindingListener: InverseBindingListener) {
        modeListener = bindingListener
    }


    /**
     * Set the inverse binding listener to enable 2 way binding for the confirm text
     *
     * @param[bindingListener] An instance of InverseBindingListener
     */
    fun setConfirmTextListener(bindingListener: InverseBindingListener) {
        confirmTextListener = bindingListener
    }

    private fun layoutRightIcon(tint: Int) {
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER or Gravity.RIGHT
        layoutParams.width = resources.dpToPx(18F)
        layoutParams.height = resources.dpToPx(18F)
        layoutParams.setMargins(
                resources.dpToPx(18F),
                resources.dpToPx(18F),
                resources.dpToPx(18F),
                resources.dpToPx(18F)
        )
        rightIcon.layoutParams = layoutParams
        rightIcon.scaleType = ImageView.ScaleType.FIT_CENTER


        rightIcon.setImageResource(drawableRight)
        rightIcon.visibility = View.VISIBLE
        rightIcon.imageTintList = ColorStateList.valueOf(tint)
        addView(rightIcon)
    }

    private fun layoutTextView(allCaps: Boolean, textSize: Float, textColor: Int) {
        textContainer.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        }
        addView(textContainer)

        textView.visibility = View.VISIBLE
        textView.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
        textView.setPadding(
                resources.dpToPx(24F),
                resources.dpToPx(0F),
                resources.dpToPx(0F),
                resources.dpToPx(0F)
        )
        textView.textSize = textSize
        textView.setAllCaps(allCaps)
        textView.maxLines = 1
        textView.setSingleLine(true)
        textView.setTextColor(textColor)

        textContainer.addView(textView)
    }

    private fun animateButton(open: Boolean) {
        animateRightIcon(open)
        // Don't animate text open if there is no value
        if (!open || !textView.text.isNullOrBlank()) animateText(open)
        animateColor(open)
    }

    private fun animateText(open: Boolean) {

        // Configure "From" Size
        val from = if (open) 0 else textContainer.width

        // Configure "To" Size
        textContainer.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val newTextWidth = textContainer.measuredWidth
        val to: Int = if (open) newTextWidth else 0

        // Animate it!
        val animator = ValueAnimator.ofInt(from, to)
        animator.apply {
            duration = 400
            setEvaluator(FloatEvaluator())
            addUpdateListener {
                val value = it.animatedValue as Int

                textContainer.layoutParams = textContainer.layoutParams.apply {
                    width = value
                }

                if (open) {
                    textView.alpha = it.animatedFraction
                } else if (!open) {
                    textView.alpha = 1F - it.animatedFraction
                }
            }
        }.start()
    }

    private fun animateRightIcon(open: Boolean) {
        val drawableResId = if (open) drawableRight else confirmDrawableRight
        rightIcon.setImageResource(drawableResId)
        (rightIcon.drawable as? Animatable)?.start()
    }

    private fun animateColor(open: Boolean) {
        val from = if (open) backgroundTint else confirmBackgroundTint
        val to = if (open) confirmBackgroundTint else backgroundTint
        val animator = ValueAnimator.ofInt(from, to)
        animator.apply {
            duration = 300
            setEvaluator(ArgbEvaluator())
            addUpdateListener {
                backgroundTintList = ColorStateList.valueOf(it.animatedValue as Int)
            }
        }.start()
    }

    private fun notifyHandlers(handlers: ArrayList<ConfirmActionButton.ConfirmActionButtonClickHandler>) {
        if (handlers.isNotEmpty()) {
            for(handler in handlers) {
                handler.onClick(this)
            }
        }
    }


    interface ConfirmActionButtonClickHandler {
        fun onClick(button: ConfirmActionButton)
    }

    /**
     *  Mode
     *
     *  The mode enumeration is used to manage the current state of the confirm button
     */
    enum class Mode {
        READY,
        CONFIRMING
    }
}