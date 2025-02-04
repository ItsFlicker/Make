package ray.mintcat.make.ui

import github.saukiya.sxitem.SXItem
import ink.ptms.um.Item
import ink.ptms.um.Mythic
import ink.ptms.zaphkiel.Zaphkiel
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import ray.mintcat.make.*
import ray.mintcat.make.data.MakeMaterial
import ray.mintcat.make.data.MakeMaterialType
import ray.mintcat.make.data.MakeStack
import ray.mintcat.make.data.Time
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XMaterial
import taboolib.module.nms.getName
import taboolib.module.nms.inputSign
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Chest
import taboolib.module.ui.type.PageableChest
import taboolib.platform.util.*

object MakeCreateUI {

    fun open(player: Player, builder: MakeStack) {
        player.openMenu<Chest>("创建 ${builder.name}") {
            map(
                "####A##J#",
                "#########",
                "#B#C#D#E#",
                "#F#G#H#I#",
                "#########",
            )
            set('A', builder.getShowItem(player).clone().modifyLore {
                add("")
                add("&f是否给予: ${showBoolean(builder.give)}".color())
                add("&7左键 替换物品".color())
                add("&8Shift+左键 选择源物品".color())
                add("&7右键 切换给予".color())
            }) {
                if (clickEvent().isLeftClick && clickEvent().isShiftClick) {
                    player.closeInventory()
                    submit(delay = 2) {
                        player.openMenu<Chest>("编辑产物") {
                            map("#A#B#C#D#")
                            set('#', buildItem(XMaterial.BLACK_STAINED_GLASS_PANE) {
                                name = " "
                                colored()
                            }) {
                                isCancelled = true
                            }
                            set('A', buildItem(XMaterial.GRASS_BLOCK) {
                                name = "&f设置来源: &a原版"
                                colored()
                            }) {
                                player.closeInventory()
                                submit(delay = 1) {
                                    addMC(player, builder, Type.ITEM)
                                }
                            }
                            set('B', buildItem(XMaterial.SOUL_SAND) {
                                name = "&f设置来源: &aMythicMobs"
                                colored()
                            }) {
                                player.closeInventory()
                                submit(delay = 1) {
                                    addMM(player, builder, Type.ITEM)
                                }
                            }
                            set('C', buildItem(XMaterial.OAK_LOG) {
                                name = "&f设置来源: &aSX-Item"
                                colored()
                            }) {
                                player.closeInventory()
                                submit(delay = 1) {
                                    addSX(player, builder, Type.ITEM)
                                }
                            }
                            set('D', buildItem(XMaterial.GLASS) {
                                name = "&f设置来源: &aZaphkiel"
                                colored()
                            }) {
                                player.closeInventory()
                                submit(delay = 1) {
                                    addZap(player, builder, Type.ITEM)
                                }
                            }
                        }
                    }
                    return@set
                }
                if (clickEvent().isLeftClick) {
                    player.closeInventory()
                    submit(delay = 2) {
                        player.openMenu<Chest>("请放入物品") {
                            map("####@####")
                            handLocked(false)
                            set('#', buildItem(XMaterial.BLACK_STAINED_GLASS_PANE) {
                                name = " "
                                colored()
                            }) {
                                isCancelled = true
                            }
                            onClick(lock = false)
                            onClose {
                                val item = it.inventory.getItem(4) ?: return@onClose kotlin.run {
                                    player.error("修改失败")
                                }
                                builder.show = item
                                player.info("成功修改为! ${item.getName()}")
                                submit(delay = 5) {
                                    open(player, builder)
                                }
                            }
                        }
                    }
                } else {
                    builder.give = !builder.give
                }
                refresh(player, builder)
            }

            set('B', buildItem(XMaterial.NETHER_STAR) {
                name = "&d继承其他配方"
                colored()
            }) {
                player.closeInventory()
                submit(delay = 2) {
                    player.openMenu<PageableChest<MakeStack>>("继承其他配方") {
                        rows(6)
                        inits()
                        slots(Slots.CENTER)
                        elements { MakeManager.stacks }
                        onGenerate { player, element, index, slot ->
                            element.getShowItem(player).modifyLore {
                                add("")
                                add("&f继承这个配方".color())
                            }
                        }
                        onClick { event, element ->
                            builder.type = element.type
                            builder.time = element.time
                            builder.replace.addAll(element.replace)
                            builder.show = element.show.clone()
                            builder.give = element.give
                            builder.action.addAll(element.action)
                            builder.check.addAll(element.check)
                            builder.info.addAll(element.info)
                            builder.source = element.source
                            player.closeInventory()
                        }
                        onClose {
                            submit(delay = 5) {
                                open(player, builder)
                            }
                        }
                    }
                }
            }
            set('C', buildItem(XMaterial.ITEM_FRAME) {
                name = "&f类型: ${builder.type}"
                lore.add(" ")
                lore.add("&7点击修改")
                colored()
            }) {
                player.closeInventory()
                player.inputSign(arrayOf("", "", "第一行输入类型")) { len ->
                    if (len[0].isNotEmpty()) {
                        builder.type = len[0]
                        player.info("修改完成！")
                    }
                    refresh(player, builder)
                }
            }
            set('D', buildItem(XMaterial.CLOCK) {
                name = "&f制作时间: ${Time(builder.time)}"
                lore.add(" ")
                lore.add("&7点击修改")
                colored()
            }) {
                player.closeInventory()
                player.inputSign(arrayOf("${builder.time / 1000}", "", "第一行输入时间(秒)")) { len ->
                    if (len[0].isNotEmpty()) {
                        builder.time = len[0].toLong() * 1000
                        player.info("修改完成！")
                    }
                    refresh(player, builder)
                }
            }
            set('E', buildItem(XMaterial.CHEST) {
                name = "&e配方管理"
                builder.replace.forEach { element ->
                    lore.add("&7${element.type.display} => ${element.id} X ${element.amount}".color())
                }
                colored()
            }) {
                isCancelled = true
                player.closeInventory()
                submit(delay = 1) {
                    materialType(player, builder)
                }
            }
            set('F', buildItem(XMaterial.REDSTONE_LAMP) {
                name = "&f编辑动作"
                lore.addAll(builder.action)
                lore.add("&f最后一行输入 clear 则清空")
                colored()
            }) {
                player.closeInventory()
                player.inputBook("动作编辑", true, builder.action) { len ->
                    if (len.last() == "clear") {
                        builder.action = mutableListOf()
                    } else {
                        builder.action = len.toMutableList()
                    }
                    refresh(player, builder)
                }
            }
            set('G', buildItem(XMaterial.DROPPER) {
                name = "&f编辑条件"
                lore.addAll(builder.check)
                lore.add("&f最后一行输入 clear 则清空")
                colored()
            }) {
                player.closeInventory()
                player.inputBook("条件编辑", true, builder.check) { len ->
                    if (len.last() == "clear") {
                        builder.check = mutableListOf()
                    } else {
                        builder.check = len.toMutableList()
                    }
                    refresh(player, builder)
                }
            }
            set('H', buildItem(XMaterial.OAK_SIGN) {
                name = "&f介绍"
                lore.addAll(builder.info)
                lore.add("&f最后一行输入 clear 则清空")
                colored()
            }) {
                player.closeInventory()
                player.inputBook("介绍编辑", true, builder.info) { len ->
                    if (len.last() == "clear") {
                        builder.info = mutableListOf()
                    } else {
                        builder.info = len.toMutableList()
                    }
                    refresh(player, builder)
                }
            }
            set('J', buildItem(XMaterial.TORCH) {
                name = "&f编辑模式: ${showBoolean(builder.builder)}"
                colored()
            }) {
                builder.builder = !builder.builder
                refresh(player, builder)
            }

            set('I', buildItem(XMaterial.LAVA_BUCKET) {
                name = "&c删除"
                colored()
            }) {
                MakeManager.stacks.remove(builder)
                player.closeInventory()
                player.info("删除成功")
            }

            onClose {
                MakeManager.stacksSave()
            }
        }
    }

    //true 是add false是take
    class MaterialTask(val type: Boolean, val material: MakeMaterial) {
        fun log(): String {
            return if (type) {
                "&a+ 添加 &7${material.type.display} => ${material.id} X ${material.amount}".color()
            } else {
                "&c- 删除 &7${material.type.display} => ${material.id} X ${material.amount}".color()
            }
        }
    }

    val list = HashMap<MakeStack, MutableList<MaterialTask>>()

    fun materialType(player: Player, builder: MakeStack) {
        player.openMenu<PageableChest<MakeMaterial>>("操作配方") {
            inits()
            rows(6)
            slots(Slots.CENTER)
            elements {
                builder.replace
            }
            onGenerate { player, element, index, slot ->
                return@onGenerate element.showItem(player).clone().modifyLore {
                    add(" ")
                    add("&7${element.type.display} => ${element.id} X ${element.amount}".color())
                    add("&f点击删除".color())
                    if (element.type == MakeMaterialType.SXITEM) {
                        add("&e右键 添加额外参数".color())
                        add("&e     最后一行输入 clear 则清空")
                    }
                }
            }
            onClick { event, element ->
                if (element.type == MakeMaterialType.SXITEM && event.clickEvent().isRightClick) {
                    player.closeInventory()
                    player.inputBook("编辑额外参数", true, element.excess) { len ->
                        if (len.last() == "clear") {
                            element.excess = mutableListOf()
                        } else {
                            element.excess = len.toMutableList()
                        }
                        submit(delay = 1) {
                            materialType(player, builder)
                        }
                    }
                    return@onClick
                }
                list.getOrPut(builder) { mutableListOf() }.add(MaterialTask(false, element))
                builder.replace.remove(element)
                player.closeInventory()
                submit(delay = 1) {
                    materialType(player, builder)
                }
            }
            val log = list.getOrDefault(builder, mutableListOf()).toList().reversed()
            set(9, buildItem(XMaterial.PAPER) {
                name = "&e撤销"
                log.forEachIndexed { index, materialTask ->
                    lore.add("&f${log.size - index}. ${materialTask.log()}")
                }
                colored()
            }) {
                val task = log.getOrNull(0) ?: return@set
                if (task.type) {
                    builder.replace.remove(task.material)
                } else {
                    builder.replace.add(task.material)
                }
                list.getOrDefault(builder, mutableListOf()).removeLastOrNull()
                player.closeInventory()
                submit(delay = 1) {
                    materialType(player, builder)
                }
            }
            set(17, buildItem(XMaterial.GRASS_BLOCK) {
                name = "&f添加材料: &a原版"
                colored()
            }) {
                addMC(player, builder)
            }
            set(26, buildItem(XMaterial.SOUL_SAND) {
                name = "&f添加材料: &aMythicMobs"
                colored()
            }) {
                addMM(player, builder)
            }
            set(35, buildItem(XMaterial.OAK_LOG) {
                name = "&f添加材料: &aSX-Item"
                colored()
            }) {
                addSX(player, builder)
            }
            set(44, buildItem(XMaterial.GLASS) {
                name = "&f添加材料: &aZaphkiel"
                colored()
            }) {
                addZap(player, builder)
            }

        }
    }

    enum class Type {
        REPLACE,
        ITEM
    }

    fun addMC(player: Player, builder: MakeStack, type: Type = Type.REPLACE) {
        player.openMenu<PageableChest<Material>>("添加材料: MC") {
            rows(6)
            inits()
            slots(Slots.CENTER)
            elements {
                val list = mutableListOf<Material>()
                list.addAll(Material.entries.filter { it.isNotAir() && it.canUse() })
                list.sortBy { it.name }
                list
            }
            onGenerate { player, element, index, slot ->
                return@onGenerate buildItem(element) {
                    lore.add(" ")
                    if (type == Type.ITEM) {
                        lore.add("&f点击设置产物")
                    } else {
                        lore.add("&f点击添加")
                    }
                    colored()
                }
            }
            onClick { event, element ->
                player.closeInventory()
                submit(delay = 1) {
                    player.inputSign(arrayOf("", "", "第一行输入数量")) { len ->
                        val amount = len[0].toInt()
                        if (amount == 0) {
                            player.closeInventory()
                            submit(delay = 2) {
                                if (type == Type.REPLACE) {
                                    materialType(player, builder)
                                } else {
                                    open(player, builder)
                                }
                            }
                            return@inputSign
                        }
                        val task = MakeMaterial(element.name, MakeMaterialType.MINECRAFT, amount)
                        if (type == Type.REPLACE) {
                            list.getOrPut(builder) { mutableListOf() }.add(MaterialTask(true, task))
                            builder.replace.add(task)
                            player.closeInventory()
                            submit(delay = 1) {
                                materialType(player, builder)
                            }
                        } else {
                            builder.source = task
                            player.closeInventory()
                            submit(delay = 1) {
                                open(player, builder)
                            }
                        }
                    }
                }
            }
        }
    }

    fun addMM(player: Player, builder: MakeStack, type: Type = Type.REPLACE) {
        player.openMenu<PageableChest<Item>>("添加材料: MM") {
            rows(6)
            inits()
            slots(Slots.CENTER)
            elements {
                Mythic.API.getItemList()
            }
            onGenerate { player, element, index, slot ->
                return@onGenerate element.generateItemStack(1).apply {
                    modifyLore {
                        add("")
                        if (type == Type.ITEM) {
                            add("&f点击设置产物".color())
                        } else {
                            add("&f点击添加".color())
                        }
                    }
                    modifyMeta<ItemMeta> {
                        setDisplayName("$displayName &7(${element.internalName})".color())
                    }
                }
            }
            onClick { event, element ->
                player.closeInventory()
                submit(delay = 1) {
                    player.inputSign(arrayOf("", "", "第一行输入数量")) { len ->
                        val amount = len[0].toInt()
                        if (amount == 0) {
                            player.closeInventory()
                            submit(delay = 2) {
                                if (type == Type.REPLACE) {
                                    materialType(player, builder)
                                } else {
                                    open(player, builder)
                                }
                            }
                            return@inputSign
                        }
                        val task = MakeMaterial(element.internalName, MakeMaterialType.MYTHIC, amount)
                        if (type == Type.REPLACE) {
                            list.getOrPut(builder) { mutableListOf() }.add(MaterialTask(true, task))
                            builder.replace.add(task)
                            player.closeInventory()
                            submit(delay = 1) {
                                materialType(player, builder)
                            }
                        } else {
                            builder.source = task
                            player.closeInventory()
                            submit(delay = 1) {
                                open(player, builder)
                            }
                        }
                    }
                }
            }
        }
    }

    fun addSX(player: Player, builder: MakeStack, type: Type = Type.REPLACE) {
        player.openMenu<PageableChest<String>>("添加材料: SX") {
            rows(6)
            inits()
            slots(Slots.CENTER)
            elements {
                val list = mutableListOf<String>()
                list.addAll(SXItem.getItemManager().itemList.filter {
                    SXItem.getItemManager().getItem(it, player).isNotAir()
                })
                list.sortBy { it }
                list
            }
            onGenerate { player, element, index, slot ->
                return@onGenerate SXItem.getItemManager().getItem(element, player).clone().apply {
                    modifyLore {
                        add("")
                        if (type == Type.ITEM) {
                            add("&f点击设置产物".color())
                        } else {
                            add("&f点击添加".color())
                        }
                    }
                    modifyMeta<ItemMeta> {
                        setDisplayName("$displayName &7(${element})".color())
                    }
                }
            }
            onClick { event, element ->
                player.closeInventory()
                submit(delay = 1) {
                    player.inputSign(arrayOf("", "", "第一行输入数量")) { len ->
                        val amount = len[0].toInt()
                        if (amount == 0) {
                            player.closeInventory()
                            submit(delay = 2) {
                                if (type == Type.REPLACE) {
                                    materialType(player, builder)
                                } else {
                                    open(player, builder)
                                }
                            }
                            return@inputSign
                        }
                        val task = MakeMaterial(element, MakeMaterialType.SXITEM, amount)
                        if (type == Type.REPLACE) {
                            list.getOrPut(builder) { mutableListOf() }.add(MaterialTask(true, task))
                            builder.replace.add(task)
                            player.closeInventory()
                            submit(delay = 1) {
                                materialType(player, builder)
                            }
                        } else {
                            builder.source = task
                            player.closeInventory()
                            submit(delay = 1) {
                                open(player, builder)
                            }
                        }
                    }
                }
            }
        }
    }

    fun addZap(player: Player, builder: MakeStack, type: Type = Type.REPLACE) {
        player.openMenu<PageableChest<ink.ptms.zaphkiel.api.Item>>("添加材料: Zap") {
            rows(6)
            inits()
            slots(Slots.CENTER)
            elements {
                Zaphkiel.api().getItemManager().getItemMap().values.toList()
            }
            onGenerate { player, element, index, slot ->
                return@onGenerate element.buildItemStack(player).apply {
                    modifyLore {
                        add("")
                        if (type == Type.ITEM) {
                            add("&f点击设置产物".color())
                        } else {
                            add("&f点击添加".color())
                        }
                    }
                    modifyMeta<ItemMeta> {
                        setDisplayName("$displayName &7(${element})".color())
                    }
                }
            }
            onClick { event, element ->
                player.closeInventory()
                submit(delay = 1) {
                    player.inputSign(arrayOf("", "", "第一行输入数量")) { len ->
                        val amount = len[0].toInt()
                        if (amount == 0) {
                            player.closeInventory()
                            submit(delay = 2) {
                                if (type == Type.REPLACE) {
                                    materialType(player, builder)
                                } else {
                                    open(player, builder)
                                }
                            }
                            return@inputSign
                        }
                        val task = MakeMaterial(element.id, MakeMaterialType.ZAPHKIEL, amount)
                        if (type == Type.REPLACE) {
                            list.getOrPut(builder) { mutableListOf() }.add(MaterialTask(true, task))
                            builder.replace.add(task)
                            player.closeInventory()
                            submit(delay = 1) {
                                materialType(player, builder)
                            }
                        } else {
                            builder.source = task
                            player.closeInventory()
                            submit(delay = 1) {
                                open(player, builder)
                            }
                        }
                    }
                }
            }
        }
    }

    fun refresh(player: Player, builder: MakeStack) {
        player.closeInventory()
        submit(delay = 1) {
            open(player, builder)
        }
    }

    fun Material.canUse(): Boolean {
        return try {
            buildItem(this).isNotAir()
        } catch (_: Exception) {
            false
        }
    }

}