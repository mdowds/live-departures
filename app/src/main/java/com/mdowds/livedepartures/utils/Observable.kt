package com.mdowds.livedepartures.utils

// TODO look at using callbacks rather than a list of objects
interface Observer<T> {
    fun update(message: T)
}

abstract class Observable<T> {

    private val observers = mutableListOf<Observer<T>>()

    fun addObserver(observer: Observer<T>) {
        observers.add(observer)
    }

    fun removeObserver(observer: Observer<T>) {
        observers.remove(observer)
    }

    fun notifyObservers(message: T) {
        observers.forEach{ it.update(message) }
    }
}