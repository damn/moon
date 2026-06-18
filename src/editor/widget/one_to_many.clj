(ns editor.widget.one-to-many
  (:require [com.badlogic.gdx.scenes.scene2d.actor.get-user-object :refer [get-user-object]]
            [com.badlogic.gdx.scenes.scene2d.group.children :refer [children]]
            [editor.widget.one-to-many.add-one-to-many-rows :refer [add-one-to-many-rows]]
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
