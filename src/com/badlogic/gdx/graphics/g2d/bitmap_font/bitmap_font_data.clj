(ns com.badlogic.gdx.graphics.g2d.bitmap-font.bitmap-font-data
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(defn set-scale! [^BitmapFont$BitmapFontData data scale]
  (.setScale data scale))

(defn scale-x [^BitmapFont$BitmapFontData data]
  (.scaleX data))

(defn enable-markup! [^BitmapFont$BitmapFontData data]
  (set! (.markupEnabled data) true))
