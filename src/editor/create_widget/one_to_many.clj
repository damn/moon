(ns editor.create-widget.one-to-many
  (:require [editor.widget.one-to-many.add-one-to-many-rows :refer [add-one-to-many-rows]]
            [gdx.scene2d.ui.table :as table]))

(defn f
  [[_ property-type] property-ids ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))
