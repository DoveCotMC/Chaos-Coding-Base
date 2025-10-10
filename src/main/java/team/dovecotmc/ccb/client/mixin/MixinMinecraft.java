package team.dovecotmc.ccb.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.dovecotmc.ccb.client.boundingbox.ObbHitResult;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Shadow
    @Nullable
    public HitResult hitResult;

    @Inject(
            method = "startUseItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionHand;values()[Lnet/minecraft/world/InteractionHand;", shift = At.Shift.AFTER)
    )
    private void startUseItem$ccbObbUseItem(CallbackInfo ci) {
        if (hitResult instanceof ObbHitResult obbHitResult) {
            System.out.println("Use");
            System.out.println(obbHitResult);
        }
    }

    @Inject(
            method = "startAttack",
            at = @At(value = "RETURN")
    )
    private void startAttack$ccbObbStartAttack(CallbackInfoReturnable<Boolean> cir) {
        if (hitResult instanceof ObbHitResult obbHitResult) {
            System.out.println("Attack");
            System.out.println(obbHitResult);
        }
    }
}
