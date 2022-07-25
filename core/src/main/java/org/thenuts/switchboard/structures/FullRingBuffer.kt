package org.thenuts.switchboard.structures

class FullRingBuffer<T: Any>(val size: Int, init: (Int) -> T) {
    private val mlist: MutableList<T> = MutableList(size, init)
    val list: List<T> get() = mlist
    private var head: Int = 0

    fun write(e: T) {
        mlist[head] = e
        head = (head + 1) % size
    }

    fun read(n: Int): T = mlist[(head + n) % size]
}