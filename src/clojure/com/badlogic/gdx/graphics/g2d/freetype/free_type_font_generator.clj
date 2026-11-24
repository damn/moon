(ns clojure.com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator
  "Generates [[clojure.com.badlogic.gdx.graphics.g2d.bitmap-font]] and [[clojure.com.badlogic.gdx.graphics.g2d.bitmap-font-bitmap-font-data]] instances from TrueType, OTF, and other FreeType supported fonts.
  
  Usage example:
  
  FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(\"myfont.ttf\"));
  BitmapFont font = gen.generateFont(16);
  gen.dispose(); // Don't dispose if doing incremental glyph generation.
  
  The generator has to be disposed once it is no longer used. The returned [[clojure.com.badlogic.gdx.graphics.g2d.bitmap-font]] instances are managed by the user
  and have to be disposed as usual."
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn create
  "[create]"
  [^FileHandle file-handle]
  (FreeTypeFontGenerator. file-handle))

(defn generate-font
  "Generates a new [[clojure.com.badlogic.gdx.graphics.g2d.bitmap-font]]. The size is expressed in pixels. Throws a GdxRuntimeException if the font could not be
  generated. Using big sizes might cause such an exception.
  
  - `parameter`: configures how the font is generated"
  [^FreeTypeFontGenerator generator
                     ^FreeTypeFontGenerator$FreeTypeFontParameter parameter]
  (.generateFont generator parameter))
