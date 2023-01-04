@file:JvmName("Main")
package net.azisaba.playerdataitemfinder

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import kotlinx.cli.vararg
import net.azisaba.playerdataitemfinder.filter.FilterParser
import xyz.acrylicstyle.util.InvalidArgumentException
import xyz.acrylicstyle.util.StringReader
import java.io.IOException

object Main {
    @Throws(InvalidArgumentException::class, IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgParser("PlayerDataItemFinder", prefixStyle = ArgParser.OptionPrefixStyle.GNU)
        val input by parser.argument(FileArgType, "input", "Input file").vararg()
        val ignoreInvalidNbt by parser.option(ArgType.Boolean, "ignore-invalid-nbt", "i", "Ignore invalid NBT data").default(false)
        val filter by parser.option(ArgType.String, "filter", "f", "Apply filter").required()
        parser.parse(args)
        val predicate = FilterParser.parse(StringReader.create(filter))
        val finder = PlayerDataItemFinder.create(input, ignoreInvalidNbt, predicate)
        if (finder.files.isEmpty()) {
            Log.error("No valid input files")
            return
        }
        finder.processAll()
    }
}
