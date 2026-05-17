(ns game.impl.default-font
  (:require [clojure.gdx.app :as app]
            [clojure.gdx.files :as files]
            [com.badlogic.gdx.graphics.texture.texture-filter :as texture.filter]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.data :as font.data]
            [com.badlogic.gdx.graphics.g2d.freetype.freetype-font-generator :as generator]
            [com.badlogic.gdx.graphics.g2d.freetype.freetype-font-generator.parameter :as parameter]))

(def path "exocet/films.EXL_____.ttf")
(def size 16)
(def quality-scaling 2)

(defn create
  [{:keys [ctx/app]}]
  (let [generator (generator/create (files/internal (app/files app) path))
        font (generator/generate-font generator
                                      (parameter/create
                                       {:size (* size quality-scaling)
                                        ; :texture-filter/linear because scaling to world-units
                                        :min-filter texture.filter/linear
                                        :mag-filter texture.filter/linear}))]
    (generator/dispose! generator)
    (font.data/set-scale! (font/data font) (/ quality-scaling))
    (font.data/set-markup-enabled! (font/data font) true)
    (font/use-integer-positions! font false)
    font))
