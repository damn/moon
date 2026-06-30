(ns input.key-pressed
  (:require [clojure.gdx :as gdx]))

(defn f [input k]
  (gdx/input-is-key-pressed input k))
