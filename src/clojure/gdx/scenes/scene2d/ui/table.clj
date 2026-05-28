(ns clojure.gdx.scenes.scene2d.ui.table
  (:require [clojure.gdx.scenes.scene2d.ui.cell :as cell]
            [clojure.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scenes.scene2d.ui.widget-group :as widget-group])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn add! [^Table table cell-declaration]
  (-> (.add table ^Actor (:actor cell-declaration))
      (cell/set-opts! (dissoc cell-declaration :actor))))

(defn add-rows! [^Table table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]

      (cond
       (map? props-or-actor)
       (add! table props-or-actor)

       ; TODO Remove else case
       :else (.add table ^Actor props-or-actor)
       ))
    (.row table))
  table)

(defn set-opts! [^Table table opts]
  (when-let [rows (:table/rows opts)]
    (add-rows! table rows)
    (widget-group/pack! table))
  (when-let [defaults (:table/cell-defaults opts)]
    (cell/set-opts! (.defaults table) defaults))
  (widget-group/set-opts! table opts))

(defmethod actor/create :ui/table [opts]
  (doto (Table.)
    (set-opts! opts)))
