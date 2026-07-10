(ns clojure.editor.create-widget-s-one-to-one
  (:require 
            [clojure.table-set-opts :as table-set-opts]
            [clojure.editor.create-widget :refer [create-widget]]
            [clojure.editor.create-widget-add-one-to-one-rows :as add-one-to-one-rows]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]))

(defmethod create-widget :s/one-to-one
  [[_ property-type] property-id ctx]
  (let [table (doto (table/new)
    (table-set-opts/set-opts! {:table/cell-defaults {:pad 5}}))]
    (add-one-to-one-rows/add-one-to-one-rows ctx table property-type property-id)
    table))
