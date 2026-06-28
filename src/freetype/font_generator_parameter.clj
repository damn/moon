(ns freetype.font-generator-parameter
  (:import (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn create
  [{:keys [size
           min-filter
           mag-filter]}]
  (let [this (FreeTypeFontGenerator$FreeTypeFontParameter.)]
    (set! (.size this) size)
    (set! (.minFilter this) min-filter)
    (set! (.magFilter this) mag-filter)
    this))
