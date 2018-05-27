package aunmag.shooter.environment

import aunmag.nightingale.Application
import aunmag.nightingale.utilities.OperativeManager
import aunmag.nightingale.utilities.TimeFlow
import aunmag.nightingale.utilities.UtilsGraphics
import aunmag.shooter.environment.actor.Actor
import aunmag.shooter.ai.Ai
import aunmag.shooter.client.App
import aunmag.shooter.data.soundAmbiance
import aunmag.shooter.data.soundAtmosphere
import aunmag.shooter.environment.decorations.Ground
import aunmag.shooter.gui.NotificationLayer
import aunmag.shooter.items.ItemWeapon
import aunmag.shooter.environment.projectile.Projectile
import aunmag.shooter.environment.terrain.Terrain
import org.lwjgl.opengl.GL11

class World {

    val time = TimeFlow()
    private val terrain = Terrain()
    val ground = OperativeManager<Ground>()
    val ais = OperativeManager<Ai>()
    val actors = OperativeManager<Actor>()
    val projectiles = OperativeManager<Projectile>()
    val notifications = NotificationLayer(time)
    val itemsWeapon = OperativeManager<ItemWeapon>()

    fun update() {
        time.add(Application.time.delta, true)
        ais.update()
        actors.update()
        projectiles.update()
        itemsWeapon.update()
        notifications.update()
    }

    // TODO: Optimize draw modes
    fun render() {
        terrain.render()
        ground.render()
        itemsWeapon.render()

        if (App.main.isDebug) {
            UtilsGraphics.drawPrepare()
        }

        actors.render()

        if (!App.main.isDebug) {
            UtilsGraphics.drawPrepare()
        }

        projectiles.render()
        GL11.glLineWidth(1f)
        notifications.render()
    }

    fun playSounds() {
        soundAmbiance.play()
        soundAtmosphere.play()
    }

    fun stopSounds() {
        soundAmbiance.stop()
        soundAtmosphere.stop()
    }

    fun remove() {
        notifications.clear()
        ground.remove()
        ais.remove()
        actors.remove()
        projectiles.remove()
        itemsWeapon.remove()
        stopSounds()
    }

}
