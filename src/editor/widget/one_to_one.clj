(ns editor.widget.one-to-one
  (:require [clojure.scene2d.actor.get-user-object :refer [get-user-object]]
            [editor.widget.one-to-one.add-one-to-one-rows :refer [add-one-to-one-rows]]
            [clojure.scene2d.group.children :refer [children]]
            [editor.widget :as widget]
            [gdx.scenes.scene2d.ui.table :as table]))

(defmethod widget/create :s/one-to-one [[_ property-type] property-id ctx]
  (let [table (table/create
               {:table/cell-defaults {:pad 5}})]
    (add-one-to-one-rows ctx table property-type property-id)
    table))

(defmethod widget/value :s/one-to-one [_  widget _schemas]
  (->> (children widget)
       (keep get-user-object)
       first))
