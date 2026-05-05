(ns com.badlogic.gdx.graphics.g2d.bitmap-font.data
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(defn enable-markup! [^BitmapFont$BitmapFontData data]
  (set! (.markupEnabled data) true))

(defn set-scale! [^BitmapFont$BitmapFontData data scale]
  (.setScale data scale))
