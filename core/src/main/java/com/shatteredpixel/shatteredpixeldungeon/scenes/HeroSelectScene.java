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
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChallenges;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHeroInfo;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.gltextures.TextureCache;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HeroSelectScene extends PixelScene {

	private Image background;
	private Image fadeLeft, fadeRight;
	private IconButton btnFade; //only on landscape

	//fading UI elements
	private RenderedTextBlock title;
	private ArrayList<StyledButton> heroBtns = new ArrayList<>();
	private RenderedTextBlock heroName; //only on landscape
	private RenderedTextBlock heroDesc; //only on landscape
	private StyledButton startBtn;
	private IconButton infoButton;
	private IconButton btnOptions;
	//private GameOptions optionsPane;
	private IconButton btnExit;

	@Override
	public void create() {
		super.create();

		Dungeon.hero = null;

		Badges.loadGlobal();
		Journal.loadGlobal();

		background = new Image(TextureCache.createSolid(0xFF2d2f31), 0, 0, 800, 450){
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
		add(background);

		fadeLeft = new Image(TextureCache.createGradient(0xFF000000, 0xFF000000, 0x00000000));
		fadeLeft.x = background.x-2;
		fadeLeft.scale.set(3, background.height());
		add(fadeLeft);

		fadeRight = new Image(fadeLeft);
		fadeRight.x = background.x + background.width() + 2;
		fadeRight.y = background.y + background.height();
		fadeRight.angle = 180;
		add(fadeRight);

		title = PixelScene.renderTextBlock(Messages.get(this, "title"), 12);
		title.hardlight(Window.TITLE_COLOR);
		PixelScene.align(title);
		add(title);

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
					if (landscape()) {
						w.offset(Camera.main.width / 6, 0);
					}
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

		//optionsPane = new GameOptions();
		//optionsPane.visible = optionsPane.active = false;
		//optionsPane.layout();
		//add(optionsPane);

		btnOptions = new IconButton(Icons.get(Icons.PREFS)){
			/*
			@Override
			protected void onClick() {
				super.onClick();
				optionsPane.visible = !optionsPane.visible;
				optionsPane.active = !optionsPane.active;
			}

			@Override
			protected void onPointerDown() {
				super.onPointerDown();
			}

			@Override
			protected void onPointerUp() {
				updateOptionsColor();
			}

			@Override
			protected String hoverText() {
				return Messages.get(HeroSelectScene.class, "options");
			}
			 */
		};
		updateOptionsColor();
		//btnOptions.visible = false;

		
		if (DeviceCompat.isDebug() || Badges.isUnlocked(Badges.Badge.VICTORY)){
			//add(btnOptions);
		} else {
			Dungeon.challenges = 0;
			SPDSettings.challenges(0);
			SPDSettings.customSeed("");
		}

		// Initiate character selection screen
		background.visible = false;
		int btnHeight = HeroBtn.HEIGHT;

		// exit button
		btnExit = new ExitButton();
		btnExit.setPos( 3, 3 );
		//btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );
		btnExit.visible = btnExit.active = !SPDSettings.intro();

		if(landscape()){
			title.setPos(0, btnExit.bottom()+6);
			float gridGap = 2;
			float buttonWidth = 60;
			float positionX = 0;
			float positionY = title.bottom() + gridGap*3;
			for (StyledButton button : heroBtns) {
				button.setRect(positionX, positionY, buttonWidth, HeroBtn.HEIGHT);
				if(positionX < buttonWidth){
					positionX = button.right() + gridGap;
					positionY = button.top();
				}else{
					positionX = 0;
					positionY += HeroBtn.HEIGHT + gridGap;
				}
			}
		}else{
			title.setPos((Camera.main.width - title.width()) / 2f, (Camera.main.height / 2));
			float gridGap = 2;
			float buttonWidth = 60;
			float originalCenter = (Camera.main.width - ((buttonWidth*2) + gridGap)) / 2;
			float positionX = originalCenter;
			float positionY = title.bottom() + gridGap*4;
			for (StyledButton button : heroBtns) {
				button.setRect(positionX, positionY, buttonWidth, HeroBtn.HEIGHT);
				if(positionX < (originalCenter + buttonWidth)){
					positionX = button.right() + gridGap;
					positionY = button.top();
				}else{
					positionX = originalCenter;
					positionY += HeroBtn.HEIGHT + gridGap;
				}
			}
		}
	}

	private void updateOptionsColor(){
		if (!SPDSettings.customSeed().isEmpty()){
			//btnOptions.icon().hardlight(1f, 1.5f, 0.67f);
		} else if (SPDSettings.challenges() != 0){
			//btnOptions.icon().hardlight(2f, 1.33f, 0.5f);
		} else {
			//btnOptions.icon().resetColor();
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
			background.frame(0, 0, 800, 450);
		}
		background.visible = true;
		background.hardlight(1.5f,1.5f,1.5f);

		title.text("Play as " + Messages.titleCase(cl.title()));
		startBtn.visible = startBtn.active = true;
		startBtn.text("Start");
		startBtn.setSize(120, 21);

		if(landscape()){
			title.setPos(0, btnExit.bottom()+6);
			startBtn.setPos(0, (Camera.main.height) - 30 );
		}else{
			title.setPos((Camera.main.width - title.width()) / 2f, (Camera.main.height / 2));
			startBtn.setPos((Camera.main.width - startBtn.width())/2f, (Camera.main.height) - 30 );
		}

		PixelScene.align(startBtn);
		infoButton.visible = infoButton.active = true;
		infoButton.setPos( Camera.main.width - infoButton.width(), 0 );
		updateOptionsColor();
	}

	private float uiAlpha;

	@Override
	public void update() {
		super.update();
		if (SPDSettings.intro() && Rankings.INSTANCE.totalNumber > 0){
			SPDSettings.intro(false);
		}
		btnExit.visible = btnExit.active = !SPDSettings.intro();
		//do not fade when a window is open
		for (Object v : members){
			if (v instanceof Window) resetFade();
		}
	}

	// TO BE DELETED
	private void updateFade(){}

	// TO BE DELETED
	private void resetFade(){}

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
		private static final int MIN_WIDTH = 20;
		private static final int HEIGHT = 24;

		// sprite for the hero's button
		HeroBtn ( HeroClass cl ){
			// hero button label
			super(Chrome.Type.GREY_BUTTON_TR, Messages.titleCase(cl.title()));
			this.cl = cl;
			// select sprite from spritesheet
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