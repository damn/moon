(ns clojure.gdx.scene2d.ui.table.add-rows
  (:require [clojure.gdx.scene2d.ui.table.add :refer [add!]])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn add-rows! [table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]

      (cond
       (map? props-or-actor)
       (add! table props-or-actor)

       ; TODO Remove else case
       :else (Table/.add table ^Actor props-or-actor)
       ))
    (Table/.row table))
  table)
