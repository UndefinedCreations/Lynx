package com.undefined.lynx.server

import com.undefined.lynx.display.BaseDisplay
import com.undefined.lynx.display.implementions.BlockDisplay
import com.undefined.lynx.display.implementions.Interaction
import com.undefined.lynx.display.implementions.TextDisplay
import com.undefined.lynx.logger.sendInfo
import com.undefined.lynx.scheduler.delay
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.UUID


object CustomGUIExample {

    fun create(location: Location) {
        createBackGround(location)
        createTitle(location, "Custom GUI")
        addButton(location.clone().add(0.0, 0.25, 0.0))
        addButton(location.clone().add(0.0, 1.0, 0.0))
        addButton(location.clone().add(0.0, 1.75, 0.0))
    }

    private fun addButton(location: Location) {
        // Visual box
        val blockDisplay = BlockDisplay(location)
        blockDisplay.setBlock(Material.RED_CONCRETE)
        blockDisplay.setScale(0.01, 0.5, 1.5)
        blockDisplay.setTranslation(0.02, 0.0, 0.25)

        // Visual Text
        val textDisplay = TextDisplay(location)
        textDisplay.setText("Click ME :D")
        textDisplay.setBackgroundColor(0)
        textDisplay.setLeftRotation(0.7, 0.0, 0.7, 0.0)
        textDisplay.setTranslation(0.03, 0.1, 1.0)

        //Interaction
        val interactionSpawn = location.add(-0.7, 0.0, 1.0)
        val interaction = Interaction(interactionSpawn)
        interaction.setWidth(1.5f)
        interaction.setHeight(0.5f)

        // Listening to interaction
        interaction.onClick {
            textDisplay.setText("Click : $clickType")
        }
    }

    private fun createTitle(location: Location, title: String) {
        val textDisplay = TextDisplay(location)
        textDisplay.setText(title)
        textDisplay.setBackgroundColor(0)
        textDisplay.setTranslation(0.02, 2.7, 1.0)
        textDisplay.setLeftRotation(0.7, 0.0, 0.7, 0.0)
    }

    private fun createBackGround(location: Location) {
        val blockDisplay = BlockDisplay(location)
        blockDisplay.setBlock(Material.WHITE_CONCRETE)
        blockDisplay.setScale(0.01, 3.0, 2.0)
    }

}