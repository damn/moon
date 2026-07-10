(ns clojure.ui-table
  (:require [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [clojure.table-set-opts :refer [set-opts!]]))

(defn create
  ([]
   (table/new))
  ([opts]
   (doto (create)
     (set-opts! opts))))
