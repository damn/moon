(ns create.default-font
  (:require [gdx.application :as app]
            [gdx.files :as files])
  (:import (com.badlogic.gdx.graphics Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn step
  [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/default-font (let [path "exocet/films.EXL_____.ttf"
                                     size 16
                                     quality-scaling 2
                                     generator (FreeTypeFontGenerator. (files/internal (app/files app) path))
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
                                 (.setUseIntegerPositions font false)
                                 font)))
