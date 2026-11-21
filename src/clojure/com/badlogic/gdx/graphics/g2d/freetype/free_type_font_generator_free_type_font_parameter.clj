(ns clojure.com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator-free-type-font-parameter
  (:import (com.badlogic.gdx.graphics Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn create []
  (FreeTypeFontGenerator$FreeTypeFontParameter.))

(defn set-size! [^FreeTypeFontGenerator$FreeTypeFontParameter parameter size]
  (set! (.size parameter) size))

(defn set-min-filter! [^FreeTypeFontGenerator$FreeTypeFontParameter parameter ^Texture$TextureFilter filter]
  (set! (.minFilter parameter) filter))

(defn set-mag-filter! [^FreeTypeFontGenerator$FreeTypeFontParameter parameter ^Texture$TextureFilter filter]
  (set! (.magFilter parameter) filter))
