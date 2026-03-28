(ns clj.api.com.badlogic.gdx.graphics.g2d.freetype.font-generator.parameter
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn create []
  (FreeTypeFontGenerator$FreeTypeFontParameter.))

(defn set-size! [^FreeTypeFontGenerator$FreeTypeFontParameter parameter size]
  (set! (.size parameter) size))

(defn set-min-filter! [^FreeTypeFontGenerator$FreeTypeFontParameter parameter texture-filter]
  (set! (.minFilter parameter) texture-filter))

(defn set-mag-filter! [^FreeTypeFontGenerator$FreeTypeFontParameter parameter texture-filter]
  (set! (.magFilter parameter) texture-filter))
