package edu.cornell.gdiac.physics;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.physics.*;
import edu.cornell.gdiac.physics.obstacle.*;

public class EnergyBar {
    private static final float MAX_ENERGY = 1.0f;
    private static final float MIN_ENERGY = 0.0f;
    private static final float RECHARGE_RATE = 0.01f;
    private static final float SPEED_FACTOR = 0.001f; // adjust as needed

    private float currentEnergy;
    private float speed;

    public EnergyBar(float initialEnergy) {
        this.currentEnergy = initialEnergy;
        this.speed = 0.0f;
    }

    public void update(float dt) {
        // decrease energy based on speed
        float energyChange = speed * SPEED_FACTOR * dt;
        currentEnergy -= energyChange;
        // clamp energy within bounds
        currentEnergy = MathUtils.clamp(currentEnergy, MIN_ENERGY, MAX_ENERGY);
        // recharge energy over time
        currentEnergy += RECHARGE_RATE * dt;
        currentEnergy = MathUtils.clamp(currentEnergy, MIN_ENERGY, MAX_ENERGY);
    }

}

