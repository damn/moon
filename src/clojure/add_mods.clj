(ns clojure.add-mods
  (:require [clojure.modifiers-add :as add]))

(defn f [stats mods]
  (update stats :stats/modifiers add/f mods))
