(ns create.default-font
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font.data :as font.data]
            [com.badlogic.gdx.graphics.g2d.freetype.freetype-font-generator :as font-generator]
            [com.badlogic.gdx.graphics.g2d.freetype.freetype-font-generator.parameter :as parameter]
            [com.badlogic.gdx.graphics.texture.texture-filter :as texture-filter]))

(defn step
  [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/default-font
         (let [{:keys [path
                       size
                       quality-scaling]} {:path "fonts/films.EXL_____.ttf"
                                          :size 16
                                          :quality-scaling 2}
               generator (font-generator/create (files/internal (app/files app) path))
               font (font-generator/generate-font generator
                                                  (parameter/create
                                                   {:size (* size quality-scaling)
                                                    ; texture.filter/linear because scaling to world-units
                                                    :min-filter texture-filter/linear
                                                    :mag-filter texture-filter/linear}))]
           (font-generator/dispose! generator)
           (font.data/set-scale! (font/data font) (/ quality-scaling))
           (font.data/enable-markup! (font/data font))
           (font/set-use-integer-positions! font false)
           font)))
