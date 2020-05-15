package com.example.doroute.data.domain


interface Repository<T> {
    //Repository: what I want to do with the database, whichever it will be

    fun getAll():List<T>
    fun get(id:String) : T
    fun add(t: T)
    fun remove(t: T)
    fun update(t: T)
}