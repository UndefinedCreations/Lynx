package com.undefined.lynx.itembuilder

import com.undefined.lynx.adventure.toLegacyText
import com.undefined.lynx.util.RunBlock
import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
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

    private var name: String? = null
    private var lore: MutableList<String> = mutableListOf()
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
    private var damage: Int = -1
    private var maxDamage: Int = -1

    private var bukkitMeta: ItemMeta? = null

    @JvmOverloads
    constructor(material: Material, dsl: ItemBuilder.() -> Unit = {}) {
        this.dsl = dsl
        this.material = material
        itemStack = ItemStack(material)
        bukkitMeta = itemStack!!.itemMeta
    }

    @JvmOverloads
    constructor(itemStack: ItemStack, dsl: ItemBuilder.() -> Unit = {}) {
        this.dsl = dsl
        this.itemStack = itemStack
        this.material = itemStack.type
        bukkitMeta = itemStack.itemMeta ?: return
        setName(bukkitMeta!!.itemName)
        bukkitMeta!!.lore?.let { setStringLore(it) }
        setAmount(itemStack.amount)
    }

    fun setName(name: Component): ItemBuilder = apply {
        this.name = name.toLegacyText()
    }
    fun setName(name: String): ItemBuilder = apply {
        this.name = "${ChatColor.RESET}$name"
    }
    fun setLore(lore: List<Component>): ItemBuilder = apply {
        this.lore = lore.map { it.toLegacyText() }.toMutableList()
    }
    fun setLore(vararg lore: Component) = setLore(lore.toList())
    fun setStringLore(vararg lore: String) = setStringLore(lore.toList())
    fun setStringLore(lore: List<String>) = apply {
        this.lore = lore.toMutableList()
    }
    fun addLore(vararg lore: Component): ItemBuilder = apply {
        this.lore.addAll(lore.map { it.toLegacyText() }.toList())
    }
    fun addLore(vararg lore: String): ItemBuilder = apply {
        this.lore.addAll(lore.toList())
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

     fun <T : ItemMeta> meta(consumer: (T) -> Unit) = apply {
         (getItemMeta(bukkitMeta!!) as T).let {
             bukkitMeta = it
             consumer(it)
         }
     }

    fun <T : ItemMeta> meta(clazz: Class<T>, consumer: RunBlock<T>) = apply {
        (getItemMeta(bukkitMeta!!) as T).let {
            bukkitMeta = it
            consumer.run(bukkitMeta!! as T)
        }
    }

    fun build(): ItemStack {
        dsl(this)
        val item = itemStack ?: ItemStack(material!!)
        item.addUnsafeEnchantments(enchantments)
        item.amount = this.amount

        val meta = bukkitMeta ?: item.itemMeta ?: return item

        meta.setDisplayName(name)
        meta.lore = this.lore
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

        return item.apply { itemMeta = meta }
    }

    private fun getItemMeta(meta: ItemMeta): ItemMeta = when {
        material!! == Material.PLAYER_HEAD -> SkullMeta(meta as org.bukkit.inventory.meta.SkullMeta)
        else -> meta
    }

}