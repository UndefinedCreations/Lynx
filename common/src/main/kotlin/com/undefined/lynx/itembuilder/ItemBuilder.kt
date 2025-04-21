package com.undefined.lynx.itembuilder

import com.undefined.lynx.itembuilder.meta.*
import com.undefined.lynx.util.component
import com.undefined.lynx.util.legacyString
import com.undefined.lynx.util.miniMessage
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockDataMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

class ItemBuilder {

    private var itemStack: ItemStack? = null
    private var material: Material? = null

    private var name: Component? = null
    private var lore: MutableList<Component> = mutableListOf()
    private var amount: Int = 1
    private var customModelData = 0
    private var persistentDataContainers: HashMap<NamespacedKey, PDCInfo<*, *>> = hashMapOf()
    private var enchantments: HashMap<Enchantment, Int> = HashMap()
    private var unbreakable: Boolean = false
    private var flags: MutableList<ItemFlag> = mutableListOf()
    private var attributeModifiers: HashMap<Attribute, AttributeModifier> = hashMapOf()
    private var hideToolTips: Boolean = false
    private var maxStackSize = -1
    private var itemRarity: ItemRarity? = null

    private var itemMeta: ItemBuildMeta? = null

    constructor(material: Material) {
        this.material = material
    }

    constructor(itemStack: ItemStack) {
        this.itemStack = itemStack
        this.material = itemStack.type
        val itemMeta = itemStack.itemMeta ?: return
        setPlainName(itemMeta.itemName)
        itemMeta.lore?.let { setRawLore(it) }
        setAmount(itemStack.amount)
        this.itemMeta = getItemMeta()
        this.itemMeta?.let { setItemCache(it, itemMeta) }
    }

    fun setName(name: Component): ItemBuilder = apply {
        this.name = name
    }
    fun setName(name: String): ItemBuilder = apply {
        this.name = "<reset>$name".miniMessage()
    }
    fun setPlainName(name: String): ItemBuilder = apply {
        this.name = name.component()
    }
    fun setLore(lore: List<Component>): ItemBuilder = apply {
        this.lore = lore.toMutableList()
    }
    fun setRawLore(lore: List<String>): ItemBuilder = apply {
        this.lore = lore.map { it.component() }.toMutableList()
    }
    fun setLore(vararg lore: Component): ItemBuilder = apply {
        this.lore = lore.toMutableList()
    }
    fun setLore(vararg lore: String): ItemBuilder = apply {
        this.lore = lore.map { "<reset>$it".miniMessage() }.toMutableList()
    }
    fun addLore(vararg lore: Component): ItemBuilder = apply {
        this.lore.addAll(lore.toList())
    }
    fun addLore(vararg lore: String): ItemBuilder = apply {
        this.lore.addAll(lore.map { "<reset>$it".miniMessage() }.toList())
    }
    fun addPlainLore(vararg lore: String): ItemBuilder = apply {
        this.lore.addAll(lore.map { it.component() }.toList())
    }
    fun setAmount(amount: Int): ItemBuilder = apply {
        this.amount = amount
    }
    fun addAmount(amount: Int): ItemBuilder = apply {
        this.amount += amount
    }
    fun setCustomModelData(customModelData: Int): ItemBuilder = apply {
        this.customModelData = customModelData
    }
    fun <P, C : Any> addPersistentData(key: NamespacedKey, persistentDataType: PersistentDataType<P, C>, value: C): ItemBuilder = apply {
        this.persistentDataContainers[key] = PDCInfo(persistentDataType, value)
    }
    fun addEnchantment(enchantment: Enchantment, level: Int = 1): ItemBuilder = apply {
        this.enchantments[enchantment] = level
    }
    fun setEnchantments(enchantments: HashMap<Enchantment, Int>): ItemBuilder = apply {
        this.enchantments = enchantments
    }
    fun setUnbreakable(unbreakable: Boolean): ItemBuilder = apply {
        this.unbreakable = unbreakable
    }
    fun addFlag(flag: ItemFlag): ItemBuilder = apply {
        this.flags.add(flag)
    }
    fun addFlags(vararg flag: ItemFlag): ItemBuilder = apply {
        this.flags.addAll(flag)
    }
    fun addFlags(flag: List<ItemFlag>): ItemBuilder = apply {
        this.flags.addAll(flag)
    }
    fun setFlags(flags: List<ItemFlag>): ItemBuilder = apply {
        this.flags = flags.toMutableList()
    }
    fun addAttributeModifier(attribute: Attribute, modifier: AttributeModifier): ItemBuilder = apply {
        this.attributeModifiers[attribute] = modifier
    }
    fun addAttributeModifiers(attributeModifiers: HashMap<Attribute, AttributeModifier>): ItemBuilder = apply {
        for ((attribute, modifier) in attributeModifiers) this.attributeModifiers[attribute] = modifier
    }
    fun setAttributeModifiers(attributeModifiers: HashMap<Attribute, AttributeModifier>): ItemBuilder = apply {
        this.attributeModifiers = attributeModifiers
    }
    fun hideTooltips(hideToolTips: Boolean): ItemBuilder = apply {
        this.hideToolTips = hideToolTips
    }
    fun setMaxStackSize(maxStackSize: Int): ItemBuilder {
        this.maxStackSize = maxStackSize
        return this
    }
    fun setItemRarity(itemRarity: ItemRarity): ItemBuilder = apply {
        this.itemRarity = itemRarity
    }


    @Suppress("UNCHECKED_CAST")
    fun <T: ItemBuildMeta> meta(): T =
        itemMeta as? T ?: run {
            itemMeta = getItemMeta()
            itemMeta as T
        }

    @Suppress("UNCHECKED_CAST")
    fun build(): ItemStack {
        val item = itemStack ?: ItemStack(material!!)
        item.addUnsafeEnchantments(enchantments)
        item.amount = this.amount

        val meta = item.itemMeta ?: return item

        meta.setDisplayName(this.name?.legacyString())
        meta.lore = this.lore.map { it.legacyString() }
        meta.setCustomModelData(this.customModelData)
        for ((key, container) in persistentDataContainers)
            meta.persistentDataContainer[key, container.type as PersistentDataType<Any, Any>] = container.value
        meta.isUnbreakable = this.unbreakable
        meta.addItemFlags(*this.flags.toTypedArray())
        for ((attribute, modifier) in attributeModifiers) meta.addAttributeModifier(attribute, modifier)
        meta.isHideTooltip = hideToolTips
        if (maxStackSize > 0) meta.setMaxStackSize(maxStackSize)
        if (itemRarity != null) meta.setRarity(itemRarity)

        item.itemMeta = itemMeta?.let { setMetaFromCache(it, meta) } ?: meta
        return item
    }

    private fun setMetaFromCache(itemBuildMeta: ItemBuildMeta, itemMeta: ItemMeta): ItemMeta {
        val method = itemBuildMeta::class.java.getDeclaredMethod("setMetaFromCache", ItemMeta::class.java)
        method.isAccessible = true
        return method.invoke(itemBuildMeta, itemMeta) as ItemMeta
    }

    private fun setItemCache(itemBuildMeta: ItemBuildMeta, itemMeta: ItemMeta) {
        val method = itemBuildMeta::class.java.getDeclaredMethod("setItemCache", ItemMeta::class.java)
        method.isAccessible = true
        method.invoke(itemBuildMeta, itemMeta)
    }

    private fun getItemMeta(): ItemBuildMeta? = when {
        material!!.name.contains("LEATHER_") -> LeatherArmorMeta()
        material!!.name.contains("BANNER") -> BannerMeta()
        EnchantmentTarget.ARMOR.includes(material!!) -> com.undefined.lynx.itembuilder.meta.ArmorMeta()
        material!! == Material.AXOLOTL_BUCKET -> AxolotlBucketMeta()
        material!! == Material.WRITABLE_BOOK -> WritableBookMeta()
        material!! == Material.WRITTEN_BOOK -> BookMeta()
        material!! == Material.BUNDLE -> BundleMeta()
        else -> null
    }

}