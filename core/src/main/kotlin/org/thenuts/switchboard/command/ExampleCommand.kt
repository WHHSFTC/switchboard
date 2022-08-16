package org.thenuts.switchboard.command

context(CommandContext)
class ExampleCommand(val foo: Boolean) : CommandAbstract() {
    override val done: Boolean = false
}