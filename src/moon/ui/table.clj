(ns moon.ui.table
  (:require [moon.ui.cell :as cell]
            [moon.ui.widget-group :as widget-group])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn add! [^Table table actor]
  (.add table ^Actor actor))

(defn cells [^Table table]
  (.getCells table))

(defn add-rows! [table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]
      (cond
       (map? props-or-actor) (-> (add! table (:actor props-or-actor))
                                 (cell/set-opts! (dissoc props-or-actor :actor)))
       :else (add! table props-or-actor)))
    (Table/.row table))
  table)

(defn set-opts! [table {:keys [rows cell-defaults] :as opts}]
  (cell/set-opts! (.defaults ^Table table) cell-defaults)
  (doto table
    (add-rows! rows)
    (widget-group/set-opts! opts)))

(defn create [opts]
  (-> (Table.)
      (set-opts! opts)))
