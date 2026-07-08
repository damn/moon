(ns clojure.moon.create-default-font
  (:require [clojure.bitmap-font :as bitmap-font]
            [clojure.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [clojure.disposable :as disposable]
            [clojure.files :as files]
            [clojure.free-type-font-generator :as free-type-font-generator]
            [clojure.free-type-font-generator$free-type-font-parameter :as font-parameter]
            [clojure.generate-font :as generate-font]
            [clojure.texture$texture-filter :as texture-filter]))

(defn f [ctx]
  (assoc ctx
         :ctx/default-font (let [{:keys [path
                                         size
                                         quality-scaling
                                         use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                                                   :size 16
                                                                   :quality-scaling 2
                                                                   :use-integer-positions? false}
                                 generator (free-type-font-generator/f (files/internal (:ctx/files ctx) path))
                                 parameter (doto (font-parameter/new)
                                             (font-parameter/set-size! (* size quality-scaling))
                                             (font-parameter/set-min-filter! texture-filter/linear)
                                             (font-parameter/set-mag-filter! texture-filter/linear))
                                 font (generate-font/f generator parameter)
                                 font-data (bitmap-font/get-data font)]
                             (disposable/dispose! generator)
                             (bitmap-font-data/set-scale! font-data (/ quality-scaling))
                             (bitmap-font-data/set-markup-enabled! font-data true)
                             (bitmap-font/set-use-integer-positions! font use-integer-positions?)
                             font)))
