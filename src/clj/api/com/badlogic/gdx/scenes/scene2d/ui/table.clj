(ns clj.api.com.badlogic.gdx.scenes.scene2d.ui.table
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]
            [moon.ui.table :as table]
            [moon.ui.widget-group :as widget-group])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(extend-type Table
  table/Table
  (add-rows! [table rows]
    (doseq [row rows]
      (doseq [props-or-actor row]
        (cond
         (map? props-or-actor) (-> (.add table ^Actor (:actor props-or-actor))
                                   (cell/set-opts! (dissoc props-or-actor :actor)))
         :else (.add table ^Actor props-or-actor)))
      (.row table))
    table)

  (set-opts! [table {:keys [rows cell-defaults] :as opts}]
    (cell/set-opts! (.defaults table) cell-defaults)
    (doto table
      (table/add-rows! rows)
      (widget-group/set-opts! opts))))

(defn create [opts]
  (-> (Table.)
      (table/set-opts! opts)))
