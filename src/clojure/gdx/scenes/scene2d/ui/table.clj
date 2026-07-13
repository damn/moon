(ns clojure.gdx.scenes.scene2d.ui.table
  (:require [com.badlogic.gdx.scenes.scene2d.ui.cell :as cell]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [com.badlogic.gdx.scenes.scene2d.utils.layout :as layout]))

(defn- apply-cell-opts! [cell opts]
  (doseq [[option arg] opts]
    (case option
      :fill-x?    (cell/fillX cell)
      :fill-y?    (cell/fillY cell)
      :expand?    (cell/expand cell)
      :expand-x?  (cell/expandX cell)
      :expand-y?  (cell/expandY cell)
      :bottom?    (cell/bottom cell)
      :colspan    (cell/colspan cell arg)
      :pad        (cell/pad cell arg)
      :pad-top    (cell/padTop cell arg)
      :pad-bottom (cell/padBottom cell arg)
      :width      (cell/width cell arg)
      :height     (cell/height cell arg)
      :center?    (cell/center cell)
      :right?     (cell/right cell)
      :left?      (cell/left cell))))

(defn add-cell! [table cell-declaration]
  (-> (table/add table (:actor cell-declaration))
      (apply-cell-opts! (dissoc cell-declaration :actor))))

(defn add-rows! [table rows]
  (doseq [row rows]
    (doseq [props-or-actor row]
      (cond
        (map? props-or-actor)
        (add-cell! table props-or-actor)

        ; TODO Remove else case
        :else (table/add table props-or-actor)))
    (table/row table))
  table)

(def ^:private set-opt-fns
  {:table/rows (fn [table rows]
                 (add-rows! table rows)
                 (layout/pack table))
   :table/cell-defaults (fn [table defaults]
                          (apply-cell-opts! (table/defaults table) defaults))})

(defn set-opts! [table opts]
  (doseq [[k v] opts :when (set-opt-fns k)]
    ((set-opt-fns k) table v))
  table)

(defn create [opts]
  (doto (table/new)
    (set-opts! opts)))
