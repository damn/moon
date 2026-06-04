(ns game.ctx.create-font
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.files :as files]
            [clojure.gdx.graphics.g2d.bitmap-font :as font]
            [clojure.gdx.graphics.g2d.bitmap-font.data :as font.data]
            [clojure.gdx.graphics.g2d.freetype.freetype-font-generator :as font-generator]
            [clojure.gdx.graphics.g2d.freetype.freetype-font-generator.parameter :as parameter]
            [clojure.gdx.graphics.texture.texture-filter :as texture-filter]))

(defn create-font
  [{:keys [ctx/app]}
   {:keys [path
           size
           quality-scaling
           use-integer-positions?]}]
  (let [generator (font-generator/create (files/internal (app/files app) path))
        font (font-generator/generate-font generator
                                           (parameter/create
                                            {:size (* size quality-scaling)
                                             ; texture.filter/linear because scaling to world-units
                                             :min-filter texture-filter/linear
                                             :mag-filter texture-filter/linear}))]
    (font-generator/dispose! generator)
    (font.data/set-scale! (font/data font) (/ quality-scaling))
    (font.data/enable-markup! (font/data font))
    (font/set-use-integer-positions! font use-integer-positions?)
    font))
