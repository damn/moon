(ns clj.api.com.badlogic.gdx.graphics.g2d.bitmap-font.data
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(defn set-scale! [^BitmapFont$BitmapFontData data scale]
  (.setScale data scale))

(defn enable-markup! [^BitmapFont$BitmapFontData data bool]
  (set! (.markupEnabled data) bool))

(defn scale-x [^BitmapFont$BitmapFontData data]
  (.scaleX data))
