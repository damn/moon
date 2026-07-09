(ns gdx.font-generator
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator :as free-type-font-generator]
            [com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator$free-type-font-parameter :as parameter]))

(defn new [file-handle]
  (free-type-font-generator/new file-handle))

(let [
      k->opts
      {
       ; TODO convert texture-filter from keyword?
       :set-mag-filter parameter/set-mag-filter
       :set-min-filter parameter/set-min-filter
       :set-size       parameter/set-size
       }

      build
      (fn [config-opts]
        (let [config (parameter/new)]
          (doseq [[k v] config-opts]
            (let [apply! (k->opts k)]
              (assert apply! (str "Unknown config option: " k))
              (apply! config v)))
          config))
      ]
  (defn generate-font [generator parameter]
    (free-type-font-generator/generate-font generator (build parameter))))
