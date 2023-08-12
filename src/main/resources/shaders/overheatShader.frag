/*
 * This file is meant to be paired with a JANGL TextureShaderVert.
 */

#version 410

uniform sampler2D texSampler;
uniform float overheatPercent;
in vec2 texCoords;
out vec4 fragColor;

void main() {
    float greenMultiplier = 1 - overheatPercent;
    float redMultiplier = overheatPercent;

    vec4 textureColor = texture(texSampler, texCoords);
    textureColor.x *= redMultiplier;
    textureColor.y *= greenMultiplier;

    fragColor = textureColor;
}