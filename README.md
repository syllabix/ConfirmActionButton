# ConfirmActionButton

| Confirm Action Button | About |
|:-------------------:|:------|
| ![confirm-button-demo](https://user-images.githubusercontent.com/5419021/34921044-b2449924-f94a-11e7-82b2-64e314e78589.gif) | An early stage implementation of a data bindable floating action button for android that supports a tap twice to confirm workflow.

---


## Install

Gradle:

```implementation 'com.crushonly.confirmactionbutton:confirmactionbutton:0.0.2'```

As the project is in it's early stages, it is not on jCenter yet - you will need to add the bintray repo to your gradle file

```
repositories{
    maven {
        url  "http://dl.bintray.com/syllabix/maven"
    }
}
```

## Usage
This project is in very early stages of development, and while it does provide a functional component - levels of customization are limited. 

Data Binding Example: 

```
<com.crushonly.confirmactionbutton.ConfirmActionButton
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginBottom="14dp"
            android:layout_marginEnd="14dp"
            android:background="@drawable/confirm_btn_background"
            android:layout_gravity="bottom|right"
            android:elevation="4dp"
            app:drawableRight="@drawable/icn_morph"
            app:onClick="@{ () -> activity.handleClick() }"
            app:onConfirmClick="@{ () -> activity.handleConfirmClick() }"
            app:confirmActionMode="@={ activity.confirmActionMode }"
            app:confirmText="@={ activity.confirmButtonMessage }"/>
            />
```


### Notes and Todos

1. Improve Test Coverage
2. Expand widget styleable attributes
3. Improve usage guide in Readme
