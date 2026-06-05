(ns game.ctx.create-font
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.files :as files])
  (:import (com.badlogic.gdx.graphics Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn create-font
  [{:keys [ctx/app]}
   {:keys [path
           size
           quality-scaling
           use-integer-positions?]}]
  (let [generator (FreeTypeFontGenerator. (files/internal (app/files app) path))
        font (.generateFont generator
                            (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                              (set! (.size params) (* size quality-scaling))
                              ; texture.filter/linear because scaling to world-units
                              (set! (.minFilter params) Texture$TextureFilter/Linear)
                              (set! (.magFilter params) Texture$TextureFilter/Linear)
                              params))]
    (.dispose generator)
    (.setScale (.getData font) (/ quality-scaling))
    (set! (.markupEnabled (.getData font)) true)
    (.setUseIntegerPositions font use-integer-positions?)
    font))
