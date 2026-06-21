(ns gdx.scenes.scene2d.ui.table
  (:require [clojure.ui.table :as table]
            [clojure.ui.table.set-opts :refer [set-opts!]]))

(defn create [opts]
  (doto (table/create)
    (set-opts! opts)))
