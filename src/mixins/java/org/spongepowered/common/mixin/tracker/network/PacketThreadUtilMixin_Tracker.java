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
package org.spongepowered.common.mixin.tracker.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.util.concurrent.ThreadTaskExecutor;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.common.event.tracking.phase.packet.PacketPhaseUtil;

@Mixin(PacketThreadUtil.class)
public abstract class PacketThreadUtilMixin_Tracker {

    // @formatter:off
    @Shadow @Final private static Logger LOGGER;
    // @formatter:on

    @Redirect(method = "ensureRunningOnSameThread(Lnet/minecraft/network/IPacket;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/concurrent/ThreadTaskExecutor;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/concurrent/ThreadTaskExecutor;execute(Ljava/lang/Runnable;)V"))
    private static <T extends INetHandler> void tracker$redirectProcessPacket(ThreadTaskExecutor threadTaskExecutor, Runnable p_execute_1_,
            IPacket<T> p_218797_0_, T p_218797_1_, ThreadTaskExecutor<?> p_218797_2_) {
        threadTaskExecutor.execute(() -> {
            if (p_218797_1_.getConnection().isConnected()) {
                PacketPhaseUtil.onProcessPacket(p_218797_0_, p_218797_1_);
            } else {
                LOGGER.debug("Ignoring packet due to disconnection: " + p_218797_0_);
            }
        });
    }
}