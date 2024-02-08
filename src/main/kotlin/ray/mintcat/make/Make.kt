package ray.mintcat.make

import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin

@RuntimeDependencies(
    RuntimeDependency(
        value = "org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2",
        relocate = ["!kotlin.", "!kotlin1922."]
    ),
    RuntimeDependency(
        value = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2",
        relocate = ["!kotlin.", "!kotlin1922."]
    )
)
object Make : Plugin()