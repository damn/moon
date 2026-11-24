(ns clojure.com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator-free-type-font-parameter
  "Parameter container class that helps configure how free type bitmap font data and [[clojure.com.badlogic.gdx.graphics.g2d.bitmap-font]] instances are
  generated.
  
  The packer field is for advanced usage, where it is necessary to pack multiple BitmapFonts (i.e. styles, sizes, families)
  into a single Texture atlas. If no packer is specified, the generator will use its own PixmapPacker to pack the glyphs into
  a power-of-two sized texture, and the resulting free type bitmap font data will have a valid [[clojure.com.badlogic.gdx.graphics.g2d.texture-region]] which
  can be used to construct a new [[clojure.com.badlogic.gdx.graphics.g2d.bitmap-font]]."
  (:import (com.badlogic.gdx.graphics Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn create []
  (FreeTypeFontGenerator$FreeTypeFontParameter.))

(defn set-size!
  "The size in pixels"
  [^FreeTypeFontGenerator$FreeTypeFontParameter parameter size]
  (set! (.size parameter) size))

(defn set-min-filter!
  "Minification filter"
  [^FreeTypeFontGenerator$FreeTypeFontParameter parameter ^Texture$TextureFilter filter]
  (set! (.minFilter parameter) filter))

(defn set-mag-filter!
  "Magnification filter"
  [^FreeTypeFontGenerator$FreeTypeFontParameter parameter ^Texture$TextureFilter filter]
  (set! (.magFilter parameter) filter))
