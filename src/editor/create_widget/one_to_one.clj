(ns editor.create-widget.one-to-one
  (:require [editor.widget.one-to-one.add-one-to-one-rows :refer [add-one-to-one-rows]]
            [gdx.scene2d.ui.table :as table]))

(defn f [[_ property-type] property-id ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-one-rows ctx table property-type property-id)
    table))
