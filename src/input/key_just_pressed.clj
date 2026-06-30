(ns input.key-just-pressed
  (:require [clojure.gdx :as gdx]))

(defn f [input k]
  (gdx/input-is-key-just-pressed input k))
