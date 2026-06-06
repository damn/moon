(ns editor.widget.one-to-many
  (:require [gdx.scene2d.actor.get-user-object :refer [get-user-object]]
            [gdx.scene2d.group.children :refer [children]]
            [editor.widget :as widget]
            [editor.widget.one-to-many.add-one-to-many-rows :refer [add-one-to-many-rows]]
            [gdx.scenes.scene2d.ui.table :as table]))

(defmethod widget/create :s/one-to-many [[_ property-type] property-ids ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-many-rows ctx table property-type property-ids)
    table))

(defmethod widget/value :s/one-to-many [_  widget _schemas]
  (->> (children widget)
       (keep get-user-object)
       set))
