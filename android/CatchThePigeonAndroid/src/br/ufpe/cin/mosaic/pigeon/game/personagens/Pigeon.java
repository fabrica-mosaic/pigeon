package br.ufpe.cin.mosaic.pigeon.game.personagens;

import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class Pigeon extends Ave {
	
	/** Velocidade da Ave */
	private float velocity = 10.0f;
	
	/** Posicao do pombo na tela no eixo x*/
	public static float posX = 0;
	/** Posicao do pombo na tela no eixo y*/
	public static float posY = 0;
	
	public Pigeon(final float pX, final float pY, final TiledTextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion);
		Pigeon.posX = pX;
		Pigeon.posY = pY;
	}

	@Override
	protected void onManagedUpdate(final float pSecondsElapsed) {
		this.mPhysicsHandler.setVelocityX(this.velocity);
		Pigeon.posX += this.velocity;
		super.onManagedUpdate(pSecondsElapsed);
	}
	
	@Override
	public float getVelocity() {	
		return velocity;
	}	
}