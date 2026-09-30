(ns moon.scene2d.table
  (:require [gdx.layout :as layout])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Cell Table)))

(defn- set-cell-opts! [cell opts]
  (doseq [[option arg] opts]
    (case option
      :fill-x?    (.fillX ^Cell cell)
      :fill-y?    (.fillY ^Cell cell)
      :expand?    (.expand ^Cell cell)
      :expand-x?  (.expandX ^Cell cell)
      :expand-y?  (.expandY ^Cell cell)
      :bottom?    (.bottom ^Cell cell)
      :colspan    (.colspan ^Cell cell (int arg))
      :pad        (.pad ^Cell cell (float arg))
      :pad-top    (.padTop ^Cell cell (float arg))
      :pad-bottom (.padBottom ^Cell cell (float arg))
      :width      (.width ^Cell cell (float arg))
      :height     (.height ^Cell cell (float arg))
      :center?    (.center ^Cell cell)
      :right?     (.right ^Cell cell)
      :left?      (.left ^Cell cell))))

(defn add-cell! [table cell-declaration]
  (-> (.add ^Table table ^Actor (:actor cell-declaration))
      (set-cell-opts! (dissoc cell-declaration :actor))))

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
                          (set-cell-opts! (.defaults ^Table table) defaults))})

(defn set-opts! [table opts]
  (doseq [[k v] opts :when (set-opt-fns k)]
    ((set-opt-fns k) table v))
  table)

(defn create [opts]
  (doto (Table.)
    (set-opts! opts)))
