(ns clojure.gdx.graphics.g2d.freetype.free-type-font-generator
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator :as generator]
            [com.badlogic.gdx.graphics.g2d.freetype.free-type-font-generator$free-type-font-parameter :as parameter]))

(defn new [file-handle]
  (generator/new file-handle))

(let [k->opts
      {
       ; TODO convert texture-filter from keyword?
       :set-mag-filter parameter/set-magFilter
       :set-min-filter parameter/set-minFilter
       :set-size       parameter/set-size
       }

      build
      (fn [config-opts]
        (let [config (parameter/new)]
          (doseq [[k v] config-opts]
            (let [apply! (k->opts k)]
              (assert apply! (str "Unknown config option: " k))
              (apply! config v)))
          config))]
  (defn generate-font [generator parameter]
    (generator/generateFont generator (build parameter))))
