package net.minecraft.util;

import dev.diona.southside.Southside;
import dev.diona.southside.event.events.MoveInputEvent;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState()
    {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown())
        {
            ++this.moveForward;
            this.forwardKeyDown = true;
        }
        else
        {
            this.forwardKeyDown = false;
        }

        if (this.gameSettings.keyBindBack.isKeyDown())
        {
            --this.moveForward;
            this.backKeyDown = true;
        }
        else
        {
            this.backKeyDown = false;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown())
        {
            ++this.moveStrafe;
            this.leftKeyDown = true;
        }
        else
        {
            this.leftKeyDown = false;
        }

        if (this.gameSettings.keyBindRight.isKeyDown())
        {
            --this.moveStrafe;
            this.rightKeyDown = true;
        }
        else
        {
            this.rightKeyDown = false;
        }

        MoveInputEvent event = new MoveInputEvent(this.moveForward, this.moveStrafe, this.gameSettings.keyBindJump.isKeyDown(), this.gameSettings.keyBindSneak.isKeyDown(), 0.3F);
        Southside.eventBus.post(event);

        this.jump = event.isJump();
        this.sneak = event.isSneak();
        this.moveForward = event.getMoveForward();
        this.moveStrafe = event.getMoveStrafe();
        if (this.sneak)
        {
            float multiplier = event.getSneakSlowDownMultiplier();
            this.moveStrafe = (float)((double)this.moveStrafe * multiplier);
            this.moveForward = (float)((double)this.moveForward * multiplier);
        }
    }
}
