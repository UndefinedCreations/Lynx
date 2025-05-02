package com.undefined.lynx.itembuilder.meta.book

import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.BookMeta.Generation
import org.bukkit.inventory.meta.ItemMeta

class BookMeta : com.undefined.lynx.itembuilder.meta.book.AbstractWritableBookMeta<BookMeta>() {

    var author: String? = null
    var generation: Generation? = null
    var title: String? = null

    fun setAuthor(author: String?) = apply {
        this.author = author
    }

    fun setGeneration(generation: Generation) = apply {
        this.generation = generation
    }

    fun setTitle(title: String?) = apply {
        this.title = title
    }

    override fun setItemCache(itemMeta: ItemMeta) {
        super.setItemCache(itemMeta)
        val bookMeta = itemMeta as? BookMeta ?: return
        this.author = bookMeta.author
        this.title = bookMeta.title
        this.generation = bookMeta.generation
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val bookMeta = itemMeta as? BookMeta ?: return super.setMetaFromCache(itemMeta)
        bookMeta.author = this.author
        bookMeta.title = this.title
        bookMeta.generation = this.generation
        return super.setMetaFromCache(bookMeta)
    }
}