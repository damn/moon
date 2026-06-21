(ns gdx.scenes.scene2d.ui.table
  (:require [clojure.scenes.scene2d.ui.table :as table]
            [clojure.scenes.scene2d.ui.table.set-opts :refer [set-opts!]]))

(defn create [opts]
  (doto (table/create)
    (set-opts! opts)))
