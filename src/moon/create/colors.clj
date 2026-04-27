(ns moon.create.colors
  (:require [clojure.gdx.colors :as colors]))

(defn step [ctx]
  (colors/put! {"PRETTY_NAME" [0.84 0.8 0.52 1]})
  ctx)
