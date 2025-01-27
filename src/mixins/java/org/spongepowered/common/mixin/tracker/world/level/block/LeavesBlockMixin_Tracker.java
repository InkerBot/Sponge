/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.tracker.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.common.bridge.world.level.LevelBridge;
import org.spongepowered.common.event.tracking.PhaseContext;
import org.spongepowered.common.event.tracking.PhaseTracker;
import org.spongepowered.common.event.tracking.phase.block.BlockPhase;
import org.spongepowered.common.world.server.SpongeLocatableBlockBuilder;

import java.util.Random;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin_Tracker extends BlockMixin_Tracker {

    @Shadow @Final public static BooleanProperty PERSISTENT;
    @Shadow @Final public static IntegerProperty DISTANCE;

    @Redirect(method = "tick",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean tracker$switchContextForDecay(final net.minecraft.server.level.ServerLevel serverWorld, final BlockPos pos,
            final net.minecraft.world.level.block.state.BlockState newState, final int flags) {
        final PhaseTracker instance = PhaseTracker.getInstance();
        try (final PhaseContext<@NonNull ?> context = BlockPhase.State.BLOCK_DECAY.createPhaseContext(instance)
                                           .source(new SpongeLocatableBlockBuilder()
                                               .world((ServerWorld) serverWorld)
                                               .position(pos.getX(), pos.getY(), pos.getZ())
                                               .state((BlockState) newState)
                                               .build())) {
            if (context != null) {
                context.buildAndSwitch();
            }
            return serverWorld.setBlock(pos, newState, flags);
        }
    }

    /**
     * @author gabizou - February 6th, 2020 - Minecraft 1.14.3
     * @reason Rewrite to handle both drops and the change state for leaves
     * that are considered to be decaying, so the drops do not leak into
     * whatever previous phase is being handled in. Since the issue is that
     * the block change takes place in a different phase (more than likely),
     * the drops are either "lost" or not considered for drops because the
     * blocks didn't change according to whatever previous phase.
     *
     * @param worldIn The world in
     * @param pos The position
     */
    @Overwrite
    public void randomTick(final net.minecraft.world.level.block.state.BlockState state, final net.minecraft.server.level.ServerLevel worldIn, final BlockPos pos, final Random random) {
        if (!state.getValue(LeavesBlockMixin_Tracker.PERSISTENT) && state.getValue(LeavesBlockMixin_Tracker.DISTANCE) == 7) {
            // Sponge Start - PhaseTracker checks and phase entry
            if (!((LevelBridge) worldIn).bridge$isFake()) {
                try (final PhaseContext<@NonNull ?> context = BlockPhase.State.BLOCK_DECAY.createPhaseContext(PhaseTracker.SERVER)
                        .source(new SpongeLocatableBlockBuilder()
                                .world((ServerWorld) worldIn)
                                .position(pos.getX(), pos.getY(), pos.getZ())
                                .state((BlockState) state)
                                .build())) {
                    context.buildAndSwitch();
                    Block.dropResources(state, worldIn, pos);
                    worldIn.removeBlock(pos, false);
                }
                return;
            }
            // Sponge End
            Block.dropResources(state, worldIn, pos);
            worldIn.removeBlock(pos, false);
        }

    }
}
