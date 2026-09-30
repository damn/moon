(ns gdx.actor.group.widget.table
  (:require [gdx.cell :as cell]
            [gdx.layout :as layout])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn add-cell! [table cell-declaration]
  (-> (.add ^Table table ^Actor (:actor cell-declaration))
      (cell/set-opts! (dissoc cell-declaration :actor))))

(defn add-rows! [table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]
      (cond
        (map? props-or-actor)
        (add-cell! table props-or-actor)

        ; TODO Remove else case
        :else (.add ^Table table ^Actor props-or-actor)))
    (.row ^Table table))
  table)

(def ^:private set-opt-fns
  {:table/rows (fn [table rows]
                 (add-rows! table rows)
                 (layout/pack table))
   :table/cell-defaults (fn [table defaults]
                          (cell/set-opts! (.defaults ^Table table) defaults))})

(defn set-opts! [table opts]
  (doseq [[k v] opts :when (set-opt-fns k)]
    ((set-opt-fns k) table v))
  table)

(defn create [opts]
  (doto (Table.)
    (set-opts! opts)))
