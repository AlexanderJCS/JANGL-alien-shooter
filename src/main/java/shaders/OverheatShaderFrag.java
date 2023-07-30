package shaders;

import game.gameobjects.player.GunOverheat;
import jangl.graphics.shaders.FragmentShader;

import java.io.UncheckedIOException;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;

public class OverheatShaderFrag extends FragmentShader {
    private GunOverheat overheat;

    public OverheatShaderFrag(GunOverheat overheat) throws UncheckedIOException {
        super("src/main/resources/shaders/overheatShader.frag");

        this.overheat = overheat;
    }

    public void setGunOverheat(GunOverheat overheat) {
        this.overheat = overheat;
    }

    @Override
    public void setUniforms(int programID) {
        int uniformLocation = glGetUniformLocation(programID, "overheatPercent");
        glUniform1f(uniformLocation, this.overheat.getOverheatPercent() * 1.5f);
    }
}
