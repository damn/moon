(ns moon.application.create.default-font
  (:import (com.badlogic.gdx Application)
           (com.badlogic.gdx.graphics Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)))

(def path "exocet/films.EXL_____.ttf")
(def size 16)
(def quality-scaling 2)

(defn step
  [{:keys [ctx/app]
    :as ctx}]
  (assoc ctx :ctx/default-font (let [generator (FreeTypeFontGenerator. (.internal (.getFiles app) path))
                                     font (.generateFont generator
                                                         (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                                                           (set! (.size params) (* size quality-scaling))
                                                           ; :texture-filter/linear because scaling to world-units
                                                           (set! (.minFilter params) Texture$TextureFilter/Linear)
                                                           (set! (.magFilter params) Texture$TextureFilter/Linear)
                                                           params))]
                                 (.dispose generator)
                                 (.setScale (.getData font) (/ quality-scaling))
                                 (set! (.markupEnabled (.getData font)) true)
                                 (.setUseIntegerPositions font false)
                                 font)))
