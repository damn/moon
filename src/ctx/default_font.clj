(ns ctx.default-font
  (:require
            [com.badlogic.gdx.graphics.g2d.bitmap-font$bitmap-font-data :as bitmap-font-data]
            [com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]
            [com.badlogic.gdx.utils.disposable :as disposable]
            [com.badlogic.gdx.files :as files]
            [clojure.gdx.free-type-font-generator.generate-font :as generate-font]
            [clojure.gdx.free-type-font-generator.new :as new-generator]
            [clojure.gdx.free-type-font-generator$free-type-font-parameter.new :as new-parameter]
            [clojure.gdx.free-type-font-generator$free-type-font-parameter.set-mag-filter :as set-mag-filter]
            [clojure.gdx.free-type-font-generator$free-type-font-parameter.set-min-filter :as set-min-filter]
            [clojure.gdx.free-type-font-generator$free-type-font-parameter.set-size :as set-size]
            [com.badlogic.gdx.graphics.texture$texture-filter :as texture-filter]))

(defn step
  [{:keys [ctx/files]}]
  (let [{:keys [path
                size
                quality-scaling
                use-integer-positions?]} {:path "fonts/films.EXL_____.ttf"
                                          :size 16
                                          :quality-scaling 2
                                          :use-integer-positions? false}
        generator (new-generator/f (files/internal files path))
        parameter (-> (new-parameter/f)
                      (set-size/f (* size quality-scaling))
                      (set-min-filter/f texture-filter/linear)
                      (set-mag-filter/f texture-filter/linear))
        font (generate-font/f generator parameter)
        font-data (bitmap-font/get-data font)]
    (disposable/dispose! generator)
    (bitmap-font-data/set-scale! font-data (/ quality-scaling))
    (bitmap-font-data/set-markup-enabled! font-data true)
    (bitmap-font/set-use-integer-positions! font use-integer-positions?)
    font))
