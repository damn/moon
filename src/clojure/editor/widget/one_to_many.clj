(ns clojure.editor.widget.one-to-many
  (:require [gdl.get-user-object :refer [get-user-object]]
            [gdl.group.children :refer [children]]
            [clojure.editor.widget.one-to-many.add-one-to-many-rows :refer [add-one-to-many-rows]]
            [gdx.scenes.scene2d.ui.table :as table]))

(defn create
  [[_ property-type] property-ids ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defn value
  [_  widget _schemas]
  (->> (children widget)
       (keep get-user-object)
       set))
