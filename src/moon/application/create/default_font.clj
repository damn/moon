(ns moon.application.create.default-font
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.files :as files])
  (:import (com.badlogic.gdx.graphics Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(defn step
  [{:keys [ctx/app]
    :as ctx}]
  (assoc ctx :ctx/default-font (let [{:keys [path
                                             size
                                             quality-scaling
                                             enable-markup?
                                             use-integer-positions?]} {
                                                                       :path "exocet/films.EXL_____.ttf"
                                                                       :size 16
                                                                       :quality-scaling 2
                                                                       :enable-markup? true
                                                                       :use-integer-positions? false
                                                                       ; :texture-filter/linear because scaling to world-units
                                                                       :min-filter :linear
                                                                       :mag-filter :linear
                                                                       }
                                     generator (FreeTypeFontGenerator. (files/internal (app/files app) path))
                                     font (.generateFont generator
                                                         (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                                                           (set! (.size params) (* size quality-scaling))
                                                           (set! (.minFilter params) Texture$TextureFilter/Linear)
                                                           (set! (.magFilter params) Texture$TextureFilter/Linear)
                                                           params))]
                                 (.setScale (.getData font) (/ quality-scaling))
                                 (set! (.markupEnabled (.getData font)) enable-markup?)
                                 (.setUseIntegerPositions font use-integer-positions?)
                                 font)))
