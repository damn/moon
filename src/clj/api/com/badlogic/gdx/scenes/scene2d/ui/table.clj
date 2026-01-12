(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.table
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.widget-group :as widget-group])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn add-rows! [^Table table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]
      (cond
       (map? props-or-actor) (-> (.add table ^Actor (:actor props-or-actor))
                                 (cell/set-opts! (dissoc props-or-actor :actor)))
       :else (.add table ^Actor props-or-actor)))
    (.row table))
  table)

(defn set-opts! [^Table table {:keys [rows cell-defaults] :as opts}]
  (cell/set-opts! (.defaults table) cell-defaults)
  (doto table
    (add-rows! rows)
    (widget-group/set-opts! opts)))

(defn create [opts]
  (-> (Table.)
      (set-opts! opts)))
