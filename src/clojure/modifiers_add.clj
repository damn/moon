(ns clojure.modifiers-add
  (:require [clojure.ops-add :as add]))

(defn f [mods other-mods]
  (merge-with add/f mods other-mods))
