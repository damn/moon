(ns create.put-colors
  (:require [clojure.gdx.graphics.color :refer [Color]]
            [clojure.gdx.graphics.colors :as colors]))

(defn step [ctx]
  (doseq [[name rgba] {"PRETTY_NAME" [0.84 0.8 0.52 1]}]
    (colors/put! name (Color rgba)))
  ctx)
