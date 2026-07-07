(ns clojure.remove-mods
  (:require [clojure.modifiers-remove :as remove]))

(defn f [stats mods]
  (update stats :stats/modifiers remove/f mods))
