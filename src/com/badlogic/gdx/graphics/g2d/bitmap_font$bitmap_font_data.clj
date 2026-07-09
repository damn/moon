(ns com.badlogic.gdx.graphics.g2d.bitmap-font$bitmap-font-data
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(defn scaleX [data]
  (.scaleX ^BitmapFont$BitmapFontData data))

(defn set-markupEnabled [data enabled?]
  (set! (.markupEnabled ^BitmapFont$BitmapFontData data) enabled?))

(defn setScale [data scale]
  (.setScale ^BitmapFont$BitmapFontData data scale))
