(ns clojure.com.badlogic.gdx.graphics.g2d.bitmap-font-bitmap-font-data
  "Backing data for a [[clojure.com.badlogic.gdx.graphics.g2d.bitmap-font]]."
  (:import (com.badlogic.gdx.graphics.g2d BitmapFont$BitmapFontData)))

(defn scale-x [^BitmapFont$BitmapFontData font-data]
  (.scaleX font-data))

(defn set-scale!
  "Scales the font by the specified amount in both directions."
  [^BitmapFont$BitmapFontData font-data scale]
  (.setScale font-data scale))

(defn set-markup-enabled! [^BitmapFont$BitmapFontData font-data v]
  (set! (.markupEnabled font-data) v))
