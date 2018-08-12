package com.mdowds.livedepartures

import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MainPagerAdapterTests {

    private lateinit var adapter: MainPagerAdapter

    @Before
    fun setUp() {
        adapter = MainPagerAdapter(mock())
    }

    @Test
    fun `getCount returns count of modes ignoring 'All'`() {
        assertEquals(Mode.values().count() - 1, adapter.count)
    }

    @Test
    fun `getPageTitle returns correct mode, ignoring 'All'`() {
        assertEquals("Bus", adapter.getPageTitle(0))
    }
}