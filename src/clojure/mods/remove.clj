(ns clojure.mods.remove
  (:require [clojure.ops.remove :as remove]))

(defn f [mods other-mods]
  (merge-with remove/f mods other-mods))
