(ns input.button-just-pressed
  (:require [clojure.gdx :as gdx]))

(defn f [input k]
  (gdx/input-is-button-just-pressed input k))
