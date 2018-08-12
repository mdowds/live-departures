package com.mdowds.livedepartures.utils

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
