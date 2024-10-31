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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.effects.CircleArc;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroPortraitSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.GameMath;

public class StatusPane extends Component {

	private NinePatch bg;
	private Image avatar;
	private Button heroInfo;
	public static float talentBlink;
	private float warning;
	public static final float FLASH_RATE = (float)(Math.PI*1.5f); //1.5 blinks per second
	private int lastTier = 0;
	private int currentStatus = 0;
	private Image rawShielding;
	private Image shieldedHP;
	private Image hp;
	private BitmapText hpText;
	private Button heroInfoOnBar;
	private Image exp;
	private BitmapText expText;
	private int lastLvl = -1;
	private int currentStrength = 0;
	private BitmapText level;
	private BitmapText strength;
	private BuffIndicator buffs;
	private Compass compass;
	private BusyIndicator busy;
	private CircleArc counter;
	private static String asset = Assets.Interfaces.STATUS;

	public StatusPane( boolean large ){
		super();

		bg = new NinePatch( asset, 0, 0, 128, 36, 85, 0, 45, 0 );
		add( bg );

		heroInfo = new Button(){
			@Override
			protected void onClick () {
				Camera.main.panTo( Dungeon.hero.sprite.center(), 5f );
				GameScene.show( new WndHero() );
			}
			
			@Override
			public GameAction keyAction() {
				return SPDAction.HERO_INFO;
			}

			@Override
			protected String hoverText() {
				return Messages.titleCase(Messages.get(WndKeyBindings.class, "hero_info"));
			}
		};
		add(heroInfo);

		avatar = HeroPortraitSprite.avatar( Dungeon.hero.heroClass, currentStatus );
		add( avatar );

		talentBlink = 0;

		compass = new Compass( Statistics.amuletObtained ? Dungeon.level.entrance() : Dungeon.level.exit() );
		add( compass );

		rawShielding = new Image(asset, 0, 40, 44, 4);
		rawShielding.alpha(0.5f);
		add(rawShielding);

		shieldedHP = new Image(asset, 0, 40, 44, 4);
		add(shieldedHP);

		hp = new Image(asset, 0, 36, 44, 4);
		add( hp );

		hpText = new BitmapText(PixelScene.pixelFont);
		hpText.alpha(0.6f);
		add(hpText);

		heroInfoOnBar = new Button(){
			@Override
			protected void onClick () {
				Camera.main.panTo( Dungeon.hero.sprite.center(), 5f );
				GameScene.show( new WndHero() );
			}
		};
		add(heroInfoOnBar);

		exp = new Image(asset, 0, 44, 44, 4);
		add( exp );

		level = new BitmapText( PixelScene.pixelFont);
		level.hardlight( 0xFFFFFF );
		add( level );

		strength = new BitmapText( PixelScene.pixelFont);
		strength.hardlight(0xFFFFAA);
		add(strength);

		buffs = new BuffIndicator( Dungeon.hero, large );
		add( buffs );

		busy = new BusyIndicator();
		add( busy );

		counter = new CircleArc(18, 4.25f);
		counter.color( 0x808080, true );
		counter.show(this, busy.center(), 0f);
	}

	@Override
	protected void layout() {
		bg.x = x;
		bg.y = y;
		bg.size( 140, bg.height );

		avatar.x = bg.x - avatar.width / 2f + 24;
		avatar.y = bg.y - avatar.height / 2f + 28;
		PixelScene.align(avatar);

		heroInfo.setRect( x, y, 40, 40 );

		compass.x = avatar.x;
		compass.y = avatar.y;
		PixelScene.align(compass);

		hp.x = shieldedHP.x = rawShielding.x = x + 59;
		hp.y = shieldedHP.y = rawShielding.y = y + 5;

		hpText.scale.set(PixelScene.align(0.5f));
		hpText.x = hp.x + 1;
		hpText.y = hp.y + (hp.height - (hpText.baseLine()+hpText.scale.y))/2f;
		hpText.y -= 0.001f; //prefer to be slightly higher
		PixelScene.align(hpText);

		exp.x = 59;
		exp.y = 14;

		heroInfoOnBar.setRect(heroInfo.right(), y, 32, 9);

		buffs.setRect( x + 48, y + 30, 50, 8 );

		busy.x = x + 1;
		busy.y = y + 33;

		counter.point(busy.center());
	}
	
	private static final int[] warningColors = new int[]{0x660000, 0xCC0000, 0x660000};
	private int oldHP = 0;
	private int oldShield = 0;
	private int oldMax = 0;

	@Override
	public void update() {
		super.update();
		int health = Dungeon.hero.HP;
		int shield = Dungeon.hero.shielding();
		int max = Dungeon.hero.HT;

		if (!Dungeon.hero.isAlive()) {
			avatar.tint(0x000000, 0.5f);
		}else if ((health/(float)max) <= 0.8f){
			avatar.copy(HeroPortraitSprite.avatar( Dungeon.hero.heroClass, 1));

			if((health/(float)max) <= 0.5f){
				avatar.copy(HeroPortraitSprite.avatar( Dungeon.hero.heroClass, 2));
			}

			if((health/(float)max) <= 0.2f){
				warning += Game.elapsed * 5f *(0.4f - (health/(float)max));
				warning %= 1f;
				//avatar.tint(ColorMath.interpolate(warning, warningColors), 0.5f );
				avatar.copy(HeroPortraitSprite.avatar( Dungeon.hero.heroClass, 3));
			}
		} else if (talentBlink > 0.33f){ //stops early so it doesn't end in the middle of a blink
			talentBlink -= Game.elapsed;
			avatar.copy( HeroPortraitSprite.avatar( Dungeon.hero.heroClass, 4 ));
			//avatar.tint(1, 1, 0, (float)Math.abs(Math.cos(talentBlink*FLASH_RATE))/2f);
		} else {
			avatar.copy(HeroPortraitSprite.avatar( Dungeon.hero.heroClass, 0));
			avatar.resetColor();
		}

		hp.scale.x = Math.max( 0, (health-shield)/(float)max);
		shieldedHP.scale.x = health/(float)max;

		if (shield > health) {
			rawShielding.scale.x = Math.min(1, shield / (float) max);
		} else {
			rawShielding.scale.x = 0;
		}

		if (oldHP != health || oldShield != shield || oldMax != max){
			if (shield <= 0) {
				hpText.text(health + "/" + max);
			} else {
				hpText.text(health + "+" + shield + "/" + max);
			}
			oldHP = health;
			oldShield = shield;
			oldMax = max;
		}

		exp.scale.x = (32 / exp.width) * Dungeon.hero.exp / Dungeon.hero.maxExp();

		if (Dungeon.hero.lvl != lastLvl) {

			if (lastLvl != -1) {
				showStarParticles();
			}

			lastLvl = Dungeon.hero.lvl;

			level.scale.set(PixelScene.align(0.8f));
			level.text( "LVL " + Integer.toString( lastLvl ) );
			level.measure();
			level.x = x + 48;
			level.y = y + 22;
			PixelScene.align(level);
		}

		currentStrength = Dungeon.hero.STR;
		strength.scale.set(PixelScene.align(0.8f));
		strength.text( "STR " + Integer.toString( currentStrength ) );
		strength.measure();
		strength.x = x + 66;
		strength.y = y + 22;
		PixelScene.align(level);

		counter.setSweep((1f - Actor.now()%1f)%1f);
	}

	public void alpha( float value ){
		value = GameMath.gate(0, value, 1f);
		bg.alpha(value);
		avatar.alpha(value);
		rawShielding.alpha(0.5f*value);
		shieldedHP.alpha(value);
		hp.alpha(value);
		hpText.alpha(0.6f*value);
		exp.alpha(value);
		if (expText != null) expText.alpha(0.6f*value);
		level.alpha(value);
		strength.alpha(value);
		compass.alpha(value);
		busy.alpha(value);
		counter.alpha(value);
	}

	public void showStarParticles(){
		Emitter emitter = (Emitter)recycle( Emitter.class );
		emitter.revive();
		emitter.pos( avatar.center() );
		emitter.burst( Speck.factory( Speck.STAR ), 12 );
	}

}
