package com.lildan42.cft.items;

import com.lildan42.cft.CFT2Mod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;

public class CFTRemovalWandItem extends Item {
    private static final double REMOVAL_BOX_SIZE = 100.0;
    private static final double VISIBILITY_THRESHOLD = 0.95;

    public CFTRemovalWandItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if(world.isClient()) {
            return ActionResult.PASS;
        }

        List<Entity> entities = world.getOtherEntities(user,
                Box.of(user.getEyePos(), REMOVAL_BOX_SIZE, REMOVAL_BOX_SIZE, REMOVAL_BOX_SIZE),
                e -> e.getEyePos().subtract(user.getEyePos()).normalize().dotProduct(user.getHeadRotationVector()) > VISIBILITY_THRESHOLD);

        if(entities.isEmpty()) {
            return ActionResult.PASS;
        }

        Entity closest = entities.stream().min(Comparator.comparingDouble(
                        e -> e.getEyePos().subtract(user.getEyePos()).lengthSquared()))
                .orElse(null);

        closest.remove(Entity.RemovalReason.DISCARDED);

        world.getPlayers().forEach(player ->
                player.sendMessage(Text.translatable(CFT2Mod.getTranslatableKey("chatMessage", "entity_deleted"), closest.getDisplayName()), true));

        return ActionResult.SUCCESS_SERVER;
    }
}
