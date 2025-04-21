package com.undefined.lynx.itembuilder.meta

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.WritableBookMeta

open class WritableBookMeta : ItemBuildMeta() {

    private val pages: MutableList<String> = mutableListOf()

    open fun addPage(vararg text: String) = apply {
        pages.addAll(text.toList())
    }

    fun setPages(vararg text: String) = apply {
        setPages(text.toList())
    }

    fun setPages(texts: List<String>) = apply {
        pages.clear()
        addPage(*texts.toTypedArray())
    }

    fun setPage(index: Int, text: String) = apply {
        pages[index] = text
    }


    protected open fun setItemCache(itemMeta: ItemMeta) {
        val writeableMeta = itemMeta as WritableBookMeta
        setPages(writeableMeta.pages)
    }

    protected open fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val writeableBookMeta = itemMeta as? WritableBookMeta ?: return itemMeta
        writeableBookMeta.pages = pages
        return writeableBookMeta
    }

}