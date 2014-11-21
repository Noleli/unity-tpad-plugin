# Unity TPad Plugin

This is a plugin for the [Unity](http://unity3d.com/) game development environment. It let's you change textures on a [TPad](http://tpadtablet.org/) Nexus 7 variable friction tablet. It's my attempt at making something generally useful from a project-specific thing. So there may be a lot of cruft.

Here's the gist of how to use it:

- First, *you don't need the `android-src` directory to use it, only if you want to modify things on the Android side*
- Create a new Unity project. In the directory it sets up, create a `Plugins` folder inside `Assets`
- Add the `Android` folder and `tpad.cs` to `Plugins`
- Any image you want to use as a texture map should go in `Plugins/Android/res/drawable`. They'll be referred to by their filenames
- In your scene, add the `tpad` script as a component of an object. I like to use the Main Camera for this. (It's possible that declaring the `tpad` class `static` in `tpad.cs` would make this step unnecessary.)
- Any time you want to change the texture in your game logic, just call `tpad.setTexture("mytexturename")`. (You can use `tpad.setTexture("black")` to clear the texture.)

A few important things to note:

- The TPad doesn't support multitouch, so you can simulate different objects having different textures by changing the whole screen when different objects are touched
- Texture images should be 1280 x 744 (or 744 x 1280). Black maps to high friction (TPad off), white maps to low friction (TPad on full)
- Be sure that the orientation of your texture images matches the orientation of the game as configured in the Android Player Settings in Unity. It doesn't support auto-rotating.
- Right now it only supports gray-scale textures. There is color code in `android-src/src/UnityTPadIOIO.java`, but it's commented out and untested. Maybe it works!
- I think the package name in `Android/AndroidManifest.xml` has to match the bundle identifier in the Unity Android Player Settings. (But it might not. I didn't have a chance to check.)
- Also in the Player Settings, it defaults to building against some silly low version of Android. Up it to at least API 18.
- I had to change some things to make it more generalizable and so you could load any image without predefining it in the Java. So it might leak like a sieve. (But it seems ok to me.)
- If you want to dig into the Android code, load the `android-src` folder into Eclipse. After you build it, drag a copy of `unitytpadioio.jar` from `android-src/bin` to `Plugins/Android`
- At some points during my debugging process, I had to unplug and replug the TPad USB connector. It seems to be pretty reliable for the most part, though.

Have fun!