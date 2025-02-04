package ray.mintcat.make

import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
import taboolib.library.xseries.XMaterial
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import taboolib.module.kether.printKetherErrorMessage
import taboolib.module.ui.type.PageableChest
import taboolib.platform.util.buildItem
import java.util.concurrent.CompletableFuture

class LoadTask(string: String)

fun createLoad(max: Double, now: Double, maxS: String, nowS: String): String {
    val a = (now / max)
    val string = StringBuilder()
    (1..10).forEach {
        if (a > it / 10.0) {
            string.append(maxS.reversed())
        } else {
            string.append(nowS.reversed())
        }
    }
    return string.reversed().toString()
}

fun <T> PageableChest<T>.inits() {
    this.setNextPage(51) { _, hasNextPage ->
        if (hasNextPage) {
            buildItem(XMaterial.SPECTRAL_ARROW) {
                name = "§f下一页"
            }
        } else {
            buildItem(XMaterial.ARROW) {
                name = "§7下一页"
            }
        }
    }
    this.setPreviousPage(47) { _, hasPreviousPage ->
        if (hasPreviousPage) {
            buildItem(XMaterial.SPECTRAL_ARROW) {
                name = "§f上一页"
            }
        } else {
            buildItem(XMaterial.ARROW) {
                name = "§7上一页"
            }
        }
    }
}

fun List<String>.eval(player: Player) {
    try {
        KetherShell.eval(this, ScriptOptions(sender = adaptPlayer(player)))
    } catch (e: Throwable) {
        e.printKetherErrorMessage()
    }
}

fun showBoolean(boolean: Boolean): String {
    return if (boolean) {
        "&a√"
    } else {
        "&c×"
    }
}

fun List<String>.check(player: Player): CompletableFuture<Boolean> {
    return if (this.isEmpty()) {
        CompletableFuture.completedFuture(true)
    } else {
        try {
            KetherShell.eval(this, ScriptOptions(sender = adaptPlayer(player))).thenApply {
                Coerce.toBoolean(it)
            }
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
            CompletableFuture.completedFuture(false)
        }
    }
}