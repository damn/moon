(ns ctx.default-font
  (:require [clojure.free-type-font-generator$free-type-font-parameter :as font-parameter]
            [clojure.free-type-font-generator :as free-type-font-generator]
            [clojure.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [clojure.bitmap-font :as bitmap-font]
            [clojure.disposable :as disposable]
            [clojure.files :as files]
            [clojure.texture$texture-filter :as texture-filter]))

(defn step
  [{:keys [ctx/files]}]
  (let [{:keys [path
                size
                quality-scaling
                use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                          :size 16
                                          :quality-scaling 2
                                          :use-integer-positions? false}
        generator (free-type-font-generator/new (files/internal files path))
        parameter (doto (font-parameter/new)
                    (font-parameter/set-size! (* size quality-scaling))
                    (font-parameter/set-min-filter! texture-filter/linear)
                    (font-parameter/set-mag-filter! texture-filter/linear))
        font (free-type-font-generator/generate-font! generator parameter)
        font-data (bitmap-font/get-data font)]
    (disposable/dispose! generator)
    (bitmap-font-data/set-scale! font-data (/ quality-scaling))
    (bitmap-font-data/set-markup-enabled! font-data true)
    (bitmap-font/set-use-integer-positions! font use-integer-positions?)
    font))
