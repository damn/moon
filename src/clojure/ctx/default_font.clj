(ns clojure.ctx.default-font
  (:require [gdl.internal :as internal]
            [gdl.texture.filter :as texture.filter]
            [gdl.get-data :refer [get-data]]
            [gdl.enable-markup :refer [enable-markup!]]
            [gdl.set-scale :as set-scale]
            [gdl.set-use-integer-positions :as set-use-integer-positions]
            [gdl.font-generator :as font-generator]
            [gdl.font-generator.generate-font :as generate-font]
            [gdl.font-generator.dispose :refer [dispose!]]
            [gdl.font-generator-parameter :as parameter]))

(defn step
  [{:keys [ctx/files]}]
  (let [{:keys [path
                size
                quality-scaling
                use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                          :size 16
                                          :quality-scaling 2
                                          :use-integer-positions? false}
        generator (font-generator/f (internal/f files path))
        font (generate-font/f generator
                              (parameter/create
                               {:size (* size quality-scaling)
                                ; texture.filter/linear because scaling to world-units
                                :min-filter texture.filter/linear
                                :mag-filter texture.filter/linear}))]
    (dispose! generator)
    (set-scale/f (get-data font) (/ quality-scaling))
    (enable-markup! (get-data font))
    (set-use-integer-positions/f! font use-integer-positions?)
    font))
