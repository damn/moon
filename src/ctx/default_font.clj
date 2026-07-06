(ns ctx.default-font
  (:require [clojure.gdx.bitmap-font.get-data :as get-data]
            [clojure.gdx.bitmap-font.set-use-integer-positions :as set-use-integer-positions]
            [clojure.gdx.bitmap-font$bitmap-font-data.set-markup-enabled :as set-markup-enabled]
            [clojure.gdx.bitmap-font$bitmap-font-data.set-scale :as set-scale]
            [com.badlogic.gdx.utils.disposable :as disposable]
            [clojure.gdx.files.internal :as internal]
            [clojure.gdx.free-type-font-generator.generate-font :as generate-font]
            [clojure.gdx.free-type-font-generator.new :as new-generator]
            [clojure.gdx.free-type-font-generator$free-type-font-parameter.new :as new-parameter]
            [clojure.gdx.free-type-font-generator$free-type-font-parameter.set-mag-filter :as set-mag-filter]
            [clojure.gdx.free-type-font-generator$free-type-font-parameter.set-min-filter :as set-min-filter]
            [clojure.gdx.free-type-font-generator$free-type-font-parameter.set-size :as set-size]
            [clojure.gdx.texture$texture-filter.linear :as texture-filter-linear]))

(defn step
  [{:keys [ctx/files]}]
  (let [{:keys [path
                size
                quality-scaling
                use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                          :size 16
                                          :quality-scaling 2
                                          :use-integer-positions? false}
        generator (new-generator/f (internal/f files path))
        parameter (-> (new-parameter/f)
                      (set-size/f (* size quality-scaling))
                      (set-min-filter/f texture-filter-linear/v)
                      (set-mag-filter/f texture-filter-linear/v))
        font (generate-font/f generator parameter)
        font-data (get-data/f font)]
    (disposable/dispose! generator)
    (set-scale/f font-data (/ quality-scaling))
    (set-markup-enabled/f font-data true)
    (set-use-integer-positions/f font use-integer-positions?)
    font))
