(ns clojure.editor.create-widget-s-one-to-many
  (:require [clojure.table.add-rows :refer [add-rows!]]
            [clojure.editor.create-widget :refer [create-widget]]
            [clojure.editor.create-widget-add-one-to-many-rows :as add-one-to-many-rows]
            [clojure.ui-table :as table]))

(defmethod create-widget :s/one-to-many
  [[_ property-type] property-ids ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-many-rows/add-one-to-many-rows ctx table property-type property-ids)
    table))
