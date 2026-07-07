(ns clojure.create-widget-one-to-one
  (:require [clojure.add-one-to-one-rows :refer [add-one-to-one-rows]]
            [clojure.ui-table :as table]))

(defn f [[_ property-type] property-id ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-one-rows ctx table property-type property-id)
    table))
