(ns moon.table
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.cell :as cell])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Table)))

(defn add-rows! [^Table table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]
      (cond
       (map? props-or-actor) (-> (.add table ^Actor (:actor props-or-actor))
                                 (cell/set-opts! (dissoc props-or-actor :actor)))
       ; TODO Remove else case
       :else (.add table ^Actor props-or-actor)))
    (.row table))
  table)

; TODO use doto.
(defn set-opts! [^Table table {:keys [rows cell-defaults] :as opts}]
  (cell/set-opts! (.defaults table) cell-defaults)
  (add-rows! table rows)
  table)

(defn create ^Table [opts]
  (-> (Table.)
      (set-opts! opts)))
