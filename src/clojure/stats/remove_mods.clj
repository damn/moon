(ns clojure.stats.remove-mods
  (:require [clojure.mods.remove :as remove]))

(defn f [stats mods]
  (update stats :stats/modifiers remove/f mods))
