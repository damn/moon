(ns moon.stats.remove-mods
  (:require [moon.modifiers.remove :as remove]))

(defn f [stats mods]
  (update stats :stats/modifiers remove/f mods))
