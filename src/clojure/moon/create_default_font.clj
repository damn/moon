(ns clojure.moon.create-default-font
  (:require [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]
            [com.badlogic.gdx.graphics.g2d.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [com.badlogic.gdx.utils.disposable :as disposable]
            [com.badlogic.gdx.files :as files]
            [gdl.graphics.g2d.freetype.font-generator :as free-type-font-generator]
            [com.badlogic.gdx.graphics.texture$texture-filter :as texture-filter]))

(defn f [ctx]
  (assoc ctx
         :ctx/default-font (let [{:keys [path
                                         size
                                         quality-scaling
                                         use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                                                   :size 16
                                                                   :quality-scaling 2
                                                                   :use-integer-positions? false}
                                 generator (free-type-font-generator/new (files/internal (:ctx/files ctx) path))
                                 parameter {
                                            :set-size (* size quality-scaling)
                                            :set-min-filter texture-filter/Linear
                                            :set-mag-filter texture-filter/Linear
                                            }
                                 font (free-type-font-generator/generate-font generator parameter)
                                 font-data (bitmap-font/getData font)]
                             (disposable/dispose generator)
                             (bitmap-font-data/setScale font-data (/ quality-scaling))
                             (bitmap-font-data/set-markupEnabled font-data true)
                             (bitmap-font/setUseIntegerPositions font use-integer-positions?)
                             font)))
