(ns clojure.editor.create-widget-s-one-to-many
  (:require 
            [clojure.table-set-opts :as table-set-opts]
            [clojure.ui.table.add-rows :refer [add-rows!]]
            [clojure.editor.create-widget :refer [create-widget]]
            [clojure.editor.create-widget-add-one-to-many-rows :as add-one-to-many-rows]
            [com.badlogic.gdx.scenes.scene2d.ui.table :as table]))

(defmethod create-widget :s/one-to-many
  [[_ property-type] property-ids ctx]
  (let [table (doto (table/new)
    (table-set-opts/set-opts! {:table/cell-defaults {:pad 5}}))]
    (add-one-to-many-rows/add-one-to-many-rows ctx table property-type property-ids)
    table))
