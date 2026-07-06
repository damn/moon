(ns com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator$free-type-font-parameter
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.graphics Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn new []
  (FreeTypeFontGenerator$FreeTypeFontParameter.))

(defn set-mag-filter! [^FreeTypeFontGenerator$FreeTypeFontParameter parameter ^Texture$TextureFilter filter]
  (set! (.magFilter parameter) filter)
  parameter)

(defn set-min-filter! [^FreeTypeFontGenerator$FreeTypeFontParameter parameter ^Texture$TextureFilter filter]
  (set! (.minFilter parameter) filter)
  parameter)

(defn set-size! [^FreeTypeFontGenerator$FreeTypeFontParameter parameter size]
  (set! (.size parameter) size)
  parameter)
