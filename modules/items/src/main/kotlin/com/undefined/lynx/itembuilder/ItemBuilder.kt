package com.undefined.lynx.itembuilder

import com.undefined.lynx.itembuilder.meta.*
import com.undefined.lynx.itembuilder.meta.armor.ArmorMeta
import com.undefined.lynx.itembuilder.meta.armor.LeatherArmorMeta
import com.undefined.lynx.itembuilder.meta.banner.BannerMeta
import com.undefined.lynx.itembuilder.meta.banner.ShieldMeta
import com.undefined.lynx.itembuilder.meta.book.BookMeta
import com.undefined.lynx.itembuilder.meta.book.KnowledgeBookMeta
import com.undefined.lynx.itembuilder.meta.book.WriteableBookMeta
import com.undefined.lynx.itembuilder.meta.firework.FireworkEffectMeta
import com.undefined.lynx.itembuilder.meta.firework.FireworkMeta
import com.undefined.lynx.util.component
import com.undefined.lynx.util.legacySectionString
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
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

@Suppress("UNCHECKED_CAST")
class ItemBuilder {

    private var dsl: ItemBuilder.() -> Unit

    private var itemStack: ItemStack? = null
    private var material: Material? = null

    var name: Component? = null
    var lore: MutableList<Component> = mutableListOf()
    var amount: Int = 1
    var customModelData = 0
    var persistentDataContainers: HashMap<NamespacedKey, PDCInfo<*, *>> = hashMapOf()
    var enchantments: HashMap<Enchantment, Int> = HashMap()
    var unbreakable: Boolean = false
    var flags: MutableList<ItemFlag> = mutableListOf()
    var attributeModifiers: HashMap<Attribute, AttributeModifier> = hashMapOf()
    var hideToolTips: Boolean = false
    var maxStackSize = -1
    var itemRarity: ItemRarity? = null
    var damage: Int = -1
    var maxDamage: Int = -1

    private var itemMeta: ItemBuildMeta? = null

    constructor(material: Material, dsl: ItemBuilder.() -> Unit = {}) {
        this.dsl = dsl
        this.material = material
    }

    constructor(itemStack: ItemStack, dsl: ItemBuilder.() -> Unit = {}) {
        this.dsl = dsl
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
    fun setDamage(damage: Int) = apply {
        this.damage = damage
    }
    fun setMaxDamage(maxDamage: Int) = apply {
        this.maxDamage = maxDamage
    }

    fun <T: ItemBuildMeta> meta(consumer: T.() -> Unit): ItemBuilder {
        val meta = itemMeta as? T ?: run {
            itemMeta = getItemMeta()
            itemMeta as T
        }
        consumer(meta)
        return this
    }

    fun build(): ItemStack {
        dsl(this)
        val item = itemStack ?: ItemStack(material!!)
        item.addUnsafeEnchantments(enchantments)
        item.amount = this.amount

        var meta = item.itemMeta ?: return item

        meta.setDisplayName(this.name?.legacySectionString())
        meta.lore = this.lore.map { it.legacySectionString() }
        meta.setCustomModelData(this.customModelData)
        for ((key, container) in persistentDataContainers)
            meta.persistentDataContainer[key, container.type as PersistentDataType<Any, Any>] = container.value
        meta.isUnbreakable = this.unbreakable
        meta.addItemFlags(*this.flags.toTypedArray())
        for ((attribute, modifier) in attributeModifiers) meta.addAttributeModifier(attribute, modifier)
        meta.isHideTooltip = hideToolTips
        if (maxStackSize > 0) meta.setMaxStackSize(maxStackSize)
        if (itemRarity != null) meta.setRarity(itemRarity)

        val damageable = meta as? Damageable
        if (damage >= 0) damageable?.damage = damage
        if (maxDamage >= 0) damageable?.setMaxDamage(maxDamage)

        meta = damageable ?: meta

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
        material!!.name.contains("SPAWN_EGG") -> SpawnEggMeta()
        EnchantmentTarget.ARMOR.includes(material!!) -> ArmorMeta()
        material!! == Material.AXOLOTL_BUCKET -> AxolotlBucketMeta()
        material!! == Material.WRITABLE_BOOK -> WriteableBookMeta()
        material!! == Material.WRITTEN_BOOK -> BookMeta()
        material!! == Material.BUNDLE -> BundleMeta()
        material!! == Material.COMPASS -> CompassMeta()
        material!! == Material.CROSSBOW -> CrossbowMeta()
        material!! == Material.FIREWORK_STAR -> FireworkEffectMeta()
        material!! == Material.FIREWORK_ROCKET -> FireworkMeta()
        material!! == Material.KNOWLEDGE_BOOK -> KnowledgeBookMeta()
        material!! == Material.MAP -> MapMeta()
        material!! == Material.GOAT_HORN -> MusicInstrumentMeta()
        material!! == Material.OMINOUS_BOTTLE -> OminousBottleMeta()
        material!! == Material.POTION -> PotionMeta()
        material!! == Material.SHIELD -> ShieldMeta()
        material!! == Material.SUSPICIOUS_STEW -> SuspiciousStewMeta()
        material!! == Material.TROPICAL_FISH_BUCKET -> TropicalFishBucketMeta()
        material!! == Material.PLAYER_HEAD -> SkullMeta()
        else -> null
    }

}