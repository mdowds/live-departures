package com.mdowds.livedepartures.utils

import java.lang.Exception

private typealias ObserverFunction<T> = (T) -> Unit

abstract class Observable<T> {

    private val observers = mutableListOf<ObserverFunction<T>>()

    fun addObserver(observer: ObserverFunction<T>) {
        observers.add(observer)
    }

    fun removeObserver(observer: ObserverFunction<T>) {
        observers.remove(observer)
    }

    fun notifyObservers(message: T) = observers.forEach{ it(message) }
}

abstract class ErrorObservable<T>: Observable<T>() {
    private val errorObservers = mutableListOf<ObserverFunction<Exception>>()

    fun addObserver(observer: ObserverFunction<T>, errorObserver: ObserverFunction<Exception>) {
        addObserver(observer)
        errorObservers.add(errorObserver)
    }

    fun removeObserver(observer: ObserverFunction<T>, errorObserver: ObserverFunction<Exception>) {
        removeObserver(observer)
        errorObservers.remove(errorObserver)
    }

    fun notifyObserversOfError(error: Exception) = errorObservers.forEach{ it(error) }
}
