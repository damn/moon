(ns clojure.moon.create-default-font
  (:require [gdl.bitmap-font :as bitmap-font]
            [gdl.bitmap-font.data :as bitmap-font-data]
            [clojure.disposable :as disposable]
            [clojure.files :as files]
            [gdl.font-generator :as free-type-font-generator]
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
                                 generator (free-type-font-generator/new (files/internal (:ctx/files ctx) path))
                                 parameter {
                                            :set-size (* size quality-scaling)
                                            :set-min-filter texture-filter/linear
                                            :set-mag-filter texture-filter/linear
                                            }
                                 font (free-type-font-generator/generate-font generator parameter)
                                 font-data (bitmap-font/get-data font)]
                             (disposable/dispose! generator)
                             (bitmap-font-data/set-scale! font-data (/ quality-scaling))
                             (bitmap-font-data/set-markup-enabled! font-data true)
                             (bitmap-font/set-use-integer-positions! font use-integer-positions?)
                             font)))
