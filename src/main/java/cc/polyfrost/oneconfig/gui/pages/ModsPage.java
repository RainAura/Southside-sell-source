/*
 * This file is part of OneConfig.
 * OneConfig - Next Generation Config Library for Minecraft: Java Edition
 * Copyright (C) 2021~2023 Polyfrost.
 *   <https://polyfrost.cc> <https://github.com/Polyfrost/>
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *   OneConfig is licensed under the terms of version 3 of the GNU Lesser
 * General Public License as published by the Free Software Foundation, AND
 * under the Additional Terms Applicable to OneConfig, as published by Polyfrost,
 * either version 1.0 of the Additional Terms, or (at your option) any later
 * version.
 *
 *   This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 * License.  If not, see <https://www.gnu.org/licenses/>. You should
 * have also received a copy of the Additional Terms Applicable
 * to OneConfig, as published by Polyfrost. If not, see
 * <https://polyfrost.cc/legal/oneconfig/additional-terms>
 */

package cc.polyfrost.oneconfig.gui.pages;

import cc.polyfrost.oneconfig.config.data.Mod;
import dev.diona.southside.module.Category;
import cc.polyfrost.oneconfig.gui.OneConfigGui;
import cc.polyfrost.oneconfig.gui.elements.BasicButton;
import cc.polyfrost.oneconfig.gui.elements.ModCard;
import cc.polyfrost.oneconfig.internal.assets.Colors;
import cc.polyfrost.oneconfig.internal.config.OneConfigConfig;
import cc.polyfrost.oneconfig.internal.config.core.ConfigCore;
import cc.polyfrost.oneconfig.utils.SearchUtils;
import cc.polyfrost.oneconfig.renderer.NanoVGHelper;
import cc.polyfrost.oneconfig.renderer.font.Fonts;
import cc.polyfrost.oneconfig.utils.InputHandler;
import cc.polyfrost.oneconfig.utils.color.ColorPalette;

import java.util.ArrayList;

public class ModsPage extends Page {

    private final ArrayList<ModCard> modCards = new ArrayList<>();
//    private final ArrayList<BasicButton> modCategories = new ArrayList<>();
    private int size;
    private int selected;

    public ModsPage(int x) {
        super(getModType(x).name());
        reloadMods();
//        modCategories.add(new BasicButton(64, 32, "All", BasicButton.ALIGNMENT_CENTER, ColorPalette.SECONDARY));
//        modCategories.add(new BasicButton(80, 32, "1", BasicButton.ALIGNMENT_CENTER, ColorPalette.SECONDARY));
//        modCategories.add(new BasicButton(64, 32, "2", BasicButton.ALIGNMENT_CENTER, ColorPalette.SECONDARY));
//        modCategories.add(new BasicButton(104, 32, "3", BasicButton.ALIGNMENT_CENTER, ColorPalette.SECONDARY));
//        modCategories.add(new BasicButton(80, 32, "4", BasicButton.ALIGNMENT_CENTER, ColorPalette.SECONDARY));
//        modCategories.add(new BasicButton(80, 32, "5", BasicButton.ALIGNMENT_CENTER, ColorPalette.SECONDARY));
//        modCategories.add(new BasicButton(88, 32, "6", BasicButton.ALIGNMENT_CENTER, ColorPalette.SECONDARY));
//        modCategories.add(new BasicButton(88, 32, "7", BasicButton.ALIGNMENT_CENTER, ColorPalette.SECONDARY));
//        for (int i = 0; i < modCategories.size(); i++) {
//            modCategories.get(i).setToggleable(true);
//            int finalI = i;
//            modCategories.get(i).setClickAction(() -> unselect(finalI));
//        }
//        modCategories.get(x).setToggled(true);
        selected = x;
    }

    public void draw(long vg, int x, int y, InputHandler inputHandler) {
        String filter = OneConfigGui.INSTANCE == null ? "" : OneConfigGui.INSTANCE.getSearchValue().toLowerCase().trim();
        int iX = x + 16;
        int iY = y + 16;
        ArrayList<ModCard> finalModCards = new ArrayList<>(modCards);
        for (ModCard modCard : finalModCards) {
            if (inSelection(modCard) && (filter.equals("") || SearchUtils.isSimilar(modCard.getModData().name, filter))) {
                if (iY + 135 >= y - scroll && iY <= y + 728 - scroll) modCard.draw(vg, iX, iY, inputHandler);
                iX += 260;
                if (iX > x + 796) {
                    iX = x + 16;
                    iY += 135;
                }
            }
        }
        size = iY - y + 135;
        if (iX == x + 16 && iY == y + 72) {
            NanoVGHelper.INSTANCE.drawText(vg, "Looks like there is nothing here. Try another category?", x + 16, y + 72, Colors.WHITE_60, 14f, Fonts.MEDIUM);
        }
    }

    @Override
    public int drawStatic(long vg, int x, int y, InputHandler inputHandler) {
        int iXCat = x + 16;
        boolean selected = false;
//        for (BasicButton btn : modCategories) {
//            btn.draw(vg, iXCat, y + 16, inputHandler);
//            iXCat += btn.getWidth() + 8;
//            if (btn.isToggled()) selected = true;
//        }
//        if (!selected) modCategories.get(0).setToggled(true);
        return 0;
    }

//    private void unselect(int index) {
//        for (int i = 0; i < modCategories.size(); i++) {
//            if (index == i) continue;
//            modCategories.get(i).setToggled(false);
//        }
//    }

    private boolean inSelection(ModCard modCard) {
        return (
                selected == 0 && modCard.getModData().modType == Category.Client
        ) || (
                selected == 1 && modCard.getModData().modType == Category.Combat
        ) || (
                selected == 2 && modCard.getModData().modType == Category.Misc
        ) || (
                selected == 3 && modCard.getModData().modType == Category.Movement
        ) || (
                selected == 4 && modCard.getModData().modType == Category.Player
        ) || (
                selected == 5 && modCard.getModData().modType == Category.Render
        ) || (
                selected == 6 && modCard.getModData().modType == Category.World
        );
    }

    private static Category getModType(int x) {
        return switch (x) {
            case 1 -> Category.Combat;
            case 2 -> Category.Misc;
            case 3 -> Category.Movement;
            case 4 -> Category.Player;
            case 5 -> Category.Render;
            case 6 -> Category.World;
            default -> Category.Client;
        };
    }

    public void reloadMods() {
        modCards.clear();
        for (Mod modData : ConfigCore.mods) {
            ModCard modCard = new ModCard(modData, modData.config == null || modData.config.isEnabled(), false, OneConfigConfig.favoriteMods.contains(modData.name), this);
            modData.config.registerToggleListener(modCard::setToggled);
            modCards.add(modCard);
        }
    }

    @Override
    public int getMaxScrollHeight() {
        return size;
    }

    @Override
    public boolean isBase() {
        return true;
    }
}
