@file:Suppress("DEPRECATION")
package juuxel.adorn.block

import juuxel.adorn.api.block.BlockVariant
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class KitchenCupboardBlock(variant: BlockVariant) : AbstractKitchenCounterBlock(variant), BlockEntityProvider {
    override fun onUse(
        state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand?, hitResult: BlockHitResult?
    ): ActionResult {
        player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
        return ActionResult.SUCCESS
    }

    override fun createScreenHandlerFactory(state: BlockState, world: World, pos: BlockPos) =
        world.getBlockEntity(pos) as? NamedScreenHandlerFactory

    override fun onStateReplaced(state1: BlockState, world: World, pos: BlockPos, state2: BlockState, b: Boolean) {
        if (state1.block != state2.block) {
            val entity = world.getBlockEntity(pos)

            if (entity is Inventory) {
                ItemScatterer.spawn(world, pos, entity)
                world.updateComparators(pos, this)
            }

            super.onStateReplaced(state1, world, pos, state2, b)
        }
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, entity: LivingEntity?, stack: ItemStack) {
        if (stack.hasCustomName()) {
            (world.getBlockEntity(pos) as? LootableContainerBlockEntity)?.customName = stack.name
        }
    }

    override fun hasComparatorOutput(state: BlockState) = true

    override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos) =
        ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos))

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? =
        AdornBlockEntities.KITCHEN_CUPBOARD.instantiate(pos, state)
}
