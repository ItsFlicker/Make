package ray.mintcat.make

import org.bukkit.entity.Player
import ray.mintcat.make.ui.MakeCreateUI
import ray.mintcat.make.ui.MakeQueueUI
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper

@CommandHeader(name = "make", aliases = ["mk"], permission = "make.command.use")
object MakeCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(optional = true)
    val open = subCommand {
        execute<Player> { sender, context, argument ->
            MakeQueueUI.open(sender)
        }
    }

    @CommandBody(optional = true)
    val create = subCommand {
        dynamic("配方名") {
            execute<Player> { sender, _, argument ->
                val data = MakeManager.getStack(argument)
                MakeCreateUI.open(sender, data)
            }
        }
    }

}