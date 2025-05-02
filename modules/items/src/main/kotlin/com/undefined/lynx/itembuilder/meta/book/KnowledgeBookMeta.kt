package com.undefined.lynx.itembuilder.meta.book

import com.undefined.lynx.itembuilder.ItemBuildMeta
import org.bukkit.NamespacedKey
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.KnowledgeBookMeta

class KnowledgeBookMeta : ItemBuildMeta() {

    var recipes: MutableList<NamespacedKey> = mutableListOf()

    fun addRecipe(vararg recipe: NamespacedKey) = apply {
        recipes.addAll(recipe)
    }

    fun setRecipe(recipes: List<NamespacedKey>) = apply {
        this.recipes = recipes.toMutableList()
    }

    override fun setItemCache(itemMeta: ItemMeta) {
        val knowledgeBookMeta = itemMeta as KnowledgeBookMeta
        this.recipes = knowledgeBookMeta.recipes
    }

    override fun setMetaFromCache(itemMeta: ItemMeta): ItemMeta {
        val knowledgeBookMeta = itemMeta as? KnowledgeBookMeta ?: return itemMeta
        knowledgeBookMeta.recipes = recipes
        return knowledgeBookMeta
    }
}