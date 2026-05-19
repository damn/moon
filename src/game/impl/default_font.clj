(ns game.impl.default-font
  (:require [gdl.app :as app]
            [gdl.files :as files]
            [com.badlogic.gdx.graphics.texture.texture-filter :as texture.filter]
            [gdl.graphics.g2d.bitmap-font :as font]
            [gdl.graphics.g2d.bitmap-font.data :as font.data]
            [gdl.graphics.g2d.freetype.font-generator :as font-generator]
            [com.badlogic.gdx.graphics.g2d.freetype.freetype-font-generator :as generator]))

(def path "exocet/films.EXL_____.ttf")
(def size 16)
(def quality-scaling 2)

(defn create
  [{:keys [ctx/app]}]
  (let [generator (generator/create (files/internal (app/files app) path))
        font (font-generator/generate-font generator
                                           {:size (* size quality-scaling)
                                            ; :texture-filter/linear because scaling to world-units
                                            :min-filter texture.filter/linear
                                            :mag-filter texture.filter/linear})]
    (font-generator/dispose! generator)
    (font.data/set-scale! (font/data font) (/ quality-scaling))
    (font.data/set-markup-enabled! (font/data font) true)
    (font/use-integer-positions! font false)
    font))
