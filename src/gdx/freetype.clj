(ns gdx.freetype
  (:require [com.badlogic.gdx.graphics.g2d.bitmap-font.enable-markup :as enable-markup]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.set-scale :as set-scale])
  (:import (com.badlogic.gdx.graphics Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn generate-font
  [file-handle
   {:keys [size
           quality-scaling
           use-integer-positions?]}]
  (let [generator (FreeTypeFontGenerator. file-handle)
        font (.generateFont generator
                            (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                              (set! (.size params) (* size quality-scaling))
                              ; texture.filter/linear because scaling to world-units
                              (set! (.minFilter params) Texture$TextureFilter/Linear)
                              (set! (.magFilter params) Texture$TextureFilter/Linear)
                              params))]
    (.dispose generator)
    (set-scale/f! font (/ quality-scaling))
    (enable-markup/f! font)
    (.setUseIntegerPositions font use-integer-positions?)
    font))
