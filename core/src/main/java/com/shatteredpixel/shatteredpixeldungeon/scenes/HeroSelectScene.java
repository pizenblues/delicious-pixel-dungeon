/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.Rankings;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHeroInfo;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import java.util.ArrayList;

public class HeroSelectScene extends PixelScene {
	private Image background;
	private RenderedTextBlock title;
	private RenderedTextBlock heroDesc;
	private ArrayList<StyledButton> heroBtns = new ArrayList<>();
	private StyledButton startBtn;
	private IconButton infoButton;
	private IconButton btnExit;

	@Override
	public void create() {
		super.create();
		Dungeon.hero = null;
		Badges.loadGlobal();
		Journal.loadGlobal();

		background = new Image(TextureCache.createSolid(0xFF1f102a), 0, 0, 444, 250){
			@Override
			public void update() {
				if (GamesInProgress.selectedClass != null) {
					if (rm > 1f) {
						rm -= Game.elapsed;
						gm = bm = rm;
					} else {
						rm = gm = bm = 1;
					}
				}
			}
		};

		background.scale.set(Camera.main.height/background.height);
		background.x = (Camera.main.width - background.width())/2f;
		background.y = (Camera.main.height - background.height())/2f;
		PixelScene.align(background);

		try {
			//loading these big jpgs fails sometimes, so we have a catch for it
			background.texture("splashes/background.png");
		} catch (Exception e){
			Game.reportException(e);
			background.texture(TextureCache.createSolid(0xFF1f102a));
			background.frame(0, 0, 444, 250);
		}
		add(background);

		title = PixelScene.renderTextBlock(Messages.get(this, "title"), 10);
		title.hardlight(Window.TITLE_COLOR);
		PixelScene.align(title);
		add(title);

		heroDesc = PixelScene.renderTextBlock(Messages.get(this, "heroDesc"), 6);
		heroDesc.align(RenderedTextBlock.CENTER_ALIGN);
		add(heroDesc);
		heroDesc.visible = heroDesc.active = false;

		// resets everything if not class is selected
		startBtn = new StyledButton(Chrome.Type.RED_BUTTON, ""){
			@Override
			protected void onClick() {
				super.onClick();

				if (GamesInProgress.selectedClass == null) return;

				Dungeon.hero = null;
				Dungeon.daily = Dungeon.dailyReplay = false;
				ActionIndicator.clearAction();
				InterlevelScene.mode = InterlevelScene.Mode.DESCEND;

				Game.switchScene( InterlevelScene.class );
			}
		};

		startBtn.icon(Icons.get(Icons.ENTER));
		startBtn.setSize(80, 21);
		startBtn.textColor(Window.TITLE_COLOR);
		add(startBtn);
		startBtn.visible = startBtn.active = false;

		infoButton = new IconButton(Icons.get(Icons.INFO)){
			@Override
			protected void onClick() {
				super.onClick();
				HeroClass cls = GamesInProgress.selectedClass;
				if (cls != null) {
					Window w = new WndHeroInfo(GamesInProgress.selectedClass);
					ShatteredPixelDungeon.scene().addToFront(w);
				}
			}

			@Override
			protected String hoverText() {
				return Messages.titleCase(Messages.get(WndKeyBindings.class, "hero_info"));
			}
		};
		infoButton.visible = infoButton.active = false;
		infoButton.setSize(20, 21);
		add(infoButton);

		for (HeroClass cl : HeroClass.values()){
			HeroBtn button = new HeroBtn(cl);
			add(button);
			heroBtns.add(button);
		}

		// Initiate character selection screen
		btnExit = new ExitButton();
		btnExit.setPos( 3, 3 );
		add( btnExit );
		btnExit.visible = btnExit.active = !SPDSettings.intro();

		title.setPos((Camera.main.width - title.width()) / 2f, (Camera.main.height - 79));
		float gridGap = 2;
		float buttonWidth = 24;
		float originalCenter = (Camera.main.width - ((buttonWidth*5) + gridGap)) / 2;
		float positionX = originalCenter;
		float positionY = title.bottom() + 10;

		for (StyledButton button : heroBtns) {
			button.setRect(positionX, positionY, buttonWidth, HeroBtn.HEIGHT);
			positionX = button.right() + gridGap;
		}
	}

	private void setSelectedHero(HeroClass cl){
		GamesInProgress.selectedClass = cl;

		try {
			//loading these big jpgs fails sometimes, so we have a catch for it
			background.texture(cl.splashArt());
		} catch (Exception e){
			Game.reportException(e);
			background.texture(TextureCache.createSolid(0xFF2d2f31));
			background.frame(0, 0, 444, 240);
		}

		background.hardlight(1.5f,1.5f,1.5f);
		title.text("Play as " + Messages.titleCase(cl.title()));
		startBtn.visible = startBtn.active = true;
		startBtn.text("Start");
		startBtn.setSize(120, 21);
		title.setPos((Camera.main.width - title.width()) / 2f, (Camera.main.height - 79));
		startBtn.setPos((Camera.main.width - startBtn.width())/2f, (Camera.main.height) - 30 );
		PixelScene.align(startBtn);
		infoButton.visible = infoButton.active = true;
		infoButton.setPos( title.right(), title.top() - title.height() );
	}

	@Override
	public void update() {
		super.update();
		if (SPDSettings.intro() && Rankings.INSTANCE.totalNumber > 0){
			SPDSettings.intro(false);
		}
		btnExit.visible = btnExit.active = !SPDSettings.intro();
	}

	@Override
	protected void onBackPressed() {
		if (btnExit.active){
			ShatteredPixelDungeon.switchScene(TitleScene.class);
		} else {
			super.onBackPressed();
		}
	}

	private class HeroBtn extends StyledButton {
		private HeroClass cl;
		private static final int HEIGHT = 24;

		HeroBtn ( HeroClass cl ){
			super(Chrome.Type.GREY_BUTTON_TR, Messages.titleCase(""), 7);
			this.cl = cl;
			icon(new Image(cl.spritesheet(), 0, 15, 12, 15));
		}

		@Override
		public void update() {
			super.update();
			if (cl != GamesInProgress.selectedClass){
				if (!cl.isUnlocked()){
					icon.brightness(0f);
				} else {
					icon.brightness(0.5f);
				}
			} else {
				icon.brightness(1f);
			}
		}

		@Override
		protected void onClick() {
			super.onClick();

			if( !cl.isUnlocked() ){
				ShatteredPixelDungeon.scene().addToFront( new WndMessage(cl.unlockMsg()));
			} else if (GamesInProgress.selectedClass == cl) {
				Window w = new WndHeroInfo(cl);
				if (landscape()){
					w.offset(Camera.main.width/6, 0);
				}
				ShatteredPixelDungeon.scene().addToFront(w);
			} else {
				setSelectedHero(cl);
			}
		}
	}
}