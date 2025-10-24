# Libgdx dependencies

* Copied them inside the project to reduce bloat, remove global state
* -> What about tests ?

* Track here the changes (so if new version comes can apply patches)

# So copied from libgdx "1.14.0"
* gdx main src folder
* freetype src folder
* backends lwjgl3 src folder

# libgdx core [com.badlogicgames.gdx/gdx  "1.14.0"] (found out through lein deps :tree and checking ~/.m2/repository/com/badlogic... jar file)

* There are some non-class files resources but game worked without them (default-font??)
* Brings in: `[com.badlogicgames.gdx/gdx-jnigen-loader "2.5.2"]`

# [com.badlogicgames.gdx/gdx-backend-lwjgl3    "1.14.0"]

* Brings in:

                  [com.badlogicgames.jlayer/jlayer "1.0.1-gdx"]
                  [org.jcraft/jorbis "0.0.17"]
                  [org.lwjgl/lwjgl-glfw "3.3.3"]
                  [org.lwjgl/lwjgl-glfw "3.3.3" :classifier "natives-linux-arm32"]
                  [org.lwjgl/lwjgl-glfw "3.3.3" :classifier "natives-linux-arm64"]
                  [org.lwjgl/lwjgl-glfw "3.3.3" :classifier "natives-linux"]
                  [org.lwjgl/lwjgl-glfw "3.3.3" :classifier "natives-macos-arm64"]
                  [org.lwjgl/lwjgl-glfw "3.3.3" :classifier "natives-macos"]
                  [org.lwjgl/lwjgl-glfw "3.3.3" :classifier "natives-windows-x86"]
                  [org.lwjgl/lwjgl-glfw "3.3.3" :classifier "natives-windows"]
                  [org.lwjgl/lwjgl-jemalloc "3.3.3"]
                  [org.lwjgl/lwjgl-jemalloc "3.3.3" :classifier "natives-linux-arm32"]
                  [org.lwjgl/lwjgl-jemalloc "3.3.3" :classifier "natives-linux-arm64"]
                  [org.lwjgl/lwjgl-jemalloc "3.3.3" :classifier "natives-linux"]
                  [org.lwjgl/lwjgl-jemalloc "3.3.3" :classifier "natives-macos-arm64"]
                  [org.lwjgl/lwjgl-jemalloc "3.3.3" :classifier "natives-macos"]
                  [org.lwjgl/lwjgl-jemalloc "3.3.3" :classifier "natives-windows-x86"]
                  [org.lwjgl/lwjgl-jemalloc "3.3.3" :classifier "natives-windows"]
                  [org.lwjgl/lwjgl-openal "3.3.3"]
                  [org.lwjgl/lwjgl-openal "3.3.3" :classifier "natives-linux-arm32"]
                  [org.lwjgl/lwjgl-openal "3.3.3" :classifier "natives-linux-arm64"]
                  [org.lwjgl/lwjgl-openal "3.3.3" :classifier "natives-linux"]
                  [org.lwjgl/lwjgl-openal "3.3.3" :classifier "natives-macos-arm64"]
                  [org.lwjgl/lwjgl-openal "3.3.3" :classifier "natives-macos"]
                  [org.lwjgl/lwjgl-openal "3.3.3" :classifier "natives-windows-x86"]
                  [org.lwjgl/lwjgl-openal "3.3.3" :classifier "natives-windows"]
                  [org.lwjgl/lwjgl-opengl "3.3.3"]
                  [org.lwjgl/lwjgl-opengl "3.3.3" :classifier "natives-linux-arm32"]
                  [org.lwjgl/lwjgl-opengl "3.3.3" :classifier "natives-linux-arm64"]
                  [org.lwjgl/lwjgl-opengl "3.3.3" :classifier "natives-linux"]
                  [org.lwjgl/lwjgl-opengl "3.3.3" :classifier "natives-macos-arm64"]
                  [org.lwjgl/lwjgl-opengl "3.3.3" :classifier "natives-macos"]
                  [org.lwjgl/lwjgl-opengl "3.3.3" :classifier "natives-windows-x86"]
                  [org.lwjgl/lwjgl-opengl "3.3.3" :classifier "natives-windows"]
                  [org.lwjgl/lwjgl-stb "3.3.3"]
                  [org.lwjgl/lwjgl-stb "3.3.3" :classifier "natives-linux-arm32"]
                  [org.lwjgl/lwjgl-stb "3.3.3" :classifier "natives-linux-arm64"]
                  [org.lwjgl/lwjgl-stb "3.3.3" :classifier "natives-linux"]
                  [org.lwjgl/lwjgl-stb "3.3.3" :classifier "natives-macos-arm64"]
                  [org.lwjgl/lwjgl-stb "3.3.3" :classifier "natives-macos"]
                  [org.lwjgl/lwjgl-stb "3.3.3" :classifier "natives-windows-x86"]
                  [org.lwjgl/lwjgl-stb "3.3.3" :classifier "natives-windows"]
                  [org.lwjgl/lwjgl "3.3.3"]
                  [org.lwjgl/lwjgl "3.3.3" :classifier "natives-linux-arm32"]
                  [org.lwjgl/lwjgl "3.3.3" :classifier "natives-linux-arm64"]
                  [org.lwjgl/lwjgl "3.3.3" :classifier "natives-linux"]
                  [org.lwjgl/lwjgl "3.3.3" :classifier "natives-macos-arm64"]
                  [org.lwjgl/lwjgl "3.3.3" :classifier "natives-macos"]
                  [org.lwjgl/lwjgl "3.3.3" :classifier "natives-windows-x86"]
                  [org.lwjgl/lwjgl "3.3.3" :classifier "natives-windows"]

# [space.earlygrey/shapedrawer "2.5.0"]


# Next step: Search in intellij UNUSED DECLARATIONS

Code -> Analyze Code -> Run Inspection by Name -> Unused declaration (there are two options, choose Java Classes or Java Editor it was called)
- > cannot find nay

# First count java LoCs:
sh tasks/count_java.sh ~160K

# Removals:

rm -rf src/com/badlogic/gdx/graphics/g3d

=> 20K LoC ! Game still starts!

~140K LoC
140 files changed, 21573 deletions

* ScreenAdapter
* Screen (In Viewport reference)
* Game
* Unused Asset Resolvers (assets/loaders/resolvers only internalfilehandleresolver is used)
* graphics/FPSLogger
* graphics/g2d/Animation
* CpuSpriteBatch
* DistanceFieldFont
* PixmapPackerIO
* PolygonSprite
* RepeatablePolygonSprite
* FreetypeFontLoader
* FreeTypeFontGeneratorLoader
* TideMapLoader
* TiledMapLoader
* AtlasTmxMapLoader
* AtlasTmjMapLoader
* TmjMapLoader
* HexagonalTiledMapRenderer
* IsometricStaggeredTiledMapRenderer
* IsometricTiledMapRenderer
* OrthoCachedTiledMapRenderer
* OrthogonalTiledMapRenderer

Inline BatchTiledMapRenderer into my TmRenderer
Inile TiledMapRenderer interface
Inline maps.MapRenderer

TODO:
* PerspectiveCamera (delete doc references first)
* Texture3D and texture3dData
* net?
* GLProfiler?
