package pt.isel.tests.drag

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

// https://stackoverflow.com/questions/47854598/livedata-remove-observer-after-first-callback
fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}
