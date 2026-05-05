(ns moon.application.create.default-font
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.data :as font.data]
            [com.badlogic.gdx.graphics.g2d.freetype.freetype-font-generator :as generator]
            [com.badlogic.gdx.graphics.g2d.freetype.freetype-font-generator.parameters :as parameters]
            [com.badlogic.gdx.graphics.texture.texture-filter :as texture-filter]))

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
                                     generator (generator/create (files/internal (app/files app) path))
                                     font (generator/generate-font
                                           generator
                                           (parameters/create
                                            {:size (* size quality-scaling)
                                             :min-filter texture-filter/linear
                                             :mag-filter texture-filter/linear}))]
                                 (generator/dispose! generator)
                                 (font.data/set-scale! (font/data font) (/ quality-scaling))
                                 (font.data/enable-markup! (font/data font) enable-markup?)
                                 (font/set-use-integer-positions! font use-integer-positions?)
                                 font)))
