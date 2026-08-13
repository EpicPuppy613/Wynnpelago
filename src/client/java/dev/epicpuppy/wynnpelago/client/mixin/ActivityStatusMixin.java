package dev.epicpuppy.wynnpelago.client.mixin;

import com.wynntils.models.activities.type.ActivityStatus;
import dev.epicpuppy.wynnpelago.client.WynnpelagoClient;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ActivityStatus.class)
public class ActivityStatusMixin {
    @Inject(method = "from", at = @At("HEAD"), cancellable = true)
    private static void from(String statusLine, CallbackInfoReturnable<ActivityStatus> cir) {
        if (!WynnpelagoClient.enabled) {
            return;
        }
        cir.setReturnValue(Status.from(statusLine));
    }

    @RequiredArgsConstructor
    private enum Status {
        STARTED(Pattern.compile(ChatFormatting.GREEN + "Currently (in progress|tracking)"), ActivityStatus.STARTED),
        AVAILABLE(Pattern.compile(ChatFormatting.YELLOW + "Can be .+"), ActivityStatus.AVAILABLE),
        UNAVAILABLE(Pattern.compile(ChatFormatting.RED + "Cannot be .+"), ActivityStatus.UNAVAILABLE),
        COMPLETED(
                Pattern.compile(ChatFormatting.GREEN + "(Already completed|Can be explored again)"),
                ActivityStatus.COMPLETED);

        private final Pattern statusPattern;
        private final ActivityStatus statusMapping;

        public static ActivityStatus from(String statusLine) {
            for (Status status : values()) {
                if (status.statusPattern.matcher(statusLine).matches()) return status.statusMapping;
            }

            return null;
        }
    }
}
