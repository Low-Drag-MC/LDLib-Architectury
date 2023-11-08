#version 150

uniform vec2 iResolution;
uniform float iTime;

in vec4 vertexColor;
in vec2 texCoord;

out vec4 fragColor;

void mainImage(out vec4 fragColor, in vec2 fragCoord) {
    vec2 F = fragCoord.xy;
    vec2 r = iResolution.xy, u = (F + F - r)/r.y/1.888;
    fragColor.rgba = vec4(vec3(0.012,1.000,0.648), 0.);

    float a = .004/(abs(length(u*u*u*u)-.04)+.005);
    fragColor.a = a;
    fragColor.rgb += .004/(abs(length(u*u*u*u)-.04)+.005) * (cos(vec3(1.))+1.);
}

void main() {
    mainImage(fragColor, vec2(texCoord.x * iResolution.x, texCoord.y * iResolution.y));
}