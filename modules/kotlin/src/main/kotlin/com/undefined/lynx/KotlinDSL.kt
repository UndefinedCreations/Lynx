package com.undefined.lynx

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * Converts this string to a [Component] with [MiniMessage].
 */
operator fun String.not(): Component = (LynxConfig.miniMessage ?: MiniMessage.miniMessage()).deserialize(this)

/**
 * Appends a component to this component.
 */
operator fun Component.plus(component: Component): Component = this.append(component)