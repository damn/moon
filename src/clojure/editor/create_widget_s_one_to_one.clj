(ns clojure.editor.create-widget-s-one-to-one
  (:require [clojure.editor.create-widget :refer [create-widget]]
            [clojure.editor.create-widget-add-one-to-one-rows :as add-one-to-one-rows]
            [clojure.ui-table :as table]))

(defmethod create-widget :s/one-to-one
  [[_ property-type] property-id ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-one-rows/add-one-to-one-rows ctx table property-type property-id)
    table))
