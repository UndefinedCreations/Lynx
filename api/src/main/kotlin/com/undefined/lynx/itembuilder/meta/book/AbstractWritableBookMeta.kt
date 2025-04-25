package com.undefined.lynx.itembuilder.meta.book

import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.WritableBookMeta

@Suppress("UNCHECKED_CAST")
open class AbstractWritableBookMeta<T> : com.undefined.lynx.itembuilder.ItemBuildMeta() {

    private val pages: MutableList<String> = mutableListOf()

    open fun addPage(vararg text: String): T = apply {
        pages.addAll(text.toList())
    } as T

    fun setPages(vararg text: String): T = apply {
        setPages(text.toList())
    } as T

    fun setPages(texts: List<String>): T = apply {
        pages.clear()
        addPage(*texts.toTypedArray())
    } as T

    fun setPage(index: Int, text: String): T = apply {
        pages[index] = text
    } as T


    override fun setItemCache(itemMeta: ItemMeta) {
        val writeableMeta = itemMeta as WritableBookMeta
        setPages(writeableMeta.pages)
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val writeableBookMeta = itemMeta as? WritableBookMeta ?: return itemMeta
        writeableBookMeta.pages = pages
        return writeableBookMeta
    }

}